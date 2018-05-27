package hadoop;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Counter;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.lib.input.CombineTextInputFormat;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.MultipleOutputs;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.security.UserGroupInformation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import client.EntityIdClient;
import dgraph.DClient;
import dgraph.node.Candidate;
import dgraph.node.Company;
import dgraph.node.Industry;
import dgraph.node.NodeUtil;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

/**
 * User: JerryYou
 *
 * Date: 2018-05-22
 *
 * Copyright (c) 2018 devops
 *
 * <<licensetext>>
 */
public class CandidateEntityPutToDgraphMapred {


  private static Logger logger = LoggerFactory.getLogger(CandidateEntityPutToDgraphMapred.class);

  public static class MyMap extends Mapper<LongWritable, Text, Text, Text> {
    private Counter skipperCounter;
    private Counter noFiledCounter;
    private Counter originSuccessCounter;
    private Counter jsonSuccessCounter;
    private Counter errorCounter;
    private Counter timeOutErrorCounter;
    private Counter indexNotMatchSize;
    private DClient dClient;
    private EntityIdClient entityIdClient;
    private int setBatch = 0;
    private int checkUid = 0;
    private int source = 0;
    private MultipleOutputs<Text, Text> mos;

    @Override
    protected void setup(Context context) throws IOException, InterruptedException {
      super.setup(context);
      skipperCounter = context.getCounter("runner", "skipperCounter");
      errorCounter = context.getCounter("runner", "errorCounter");
      noFiledCounter = context.getCounter("runner", "noFiledCounter");
      originSuccessCounter = context.getCounter("runner", "originSuccessCounter");
      jsonSuccessCounter = context.getCounter("runner", "jsonSuccessCounter");
      timeOutErrorCounter = context.getCounter("runner", "timeOutErrorCounter");
      indexNotMatchSize = context.getCounter("runner", "indexNotMatchSize");
      setBatch = context.getConfiguration().getInt("batch", 100);
      checkUid = context.getConfiguration().getInt("checkUid", 0);
      source = context.getConfiguration().getInt("source", 0);
      String dgraphServer = context.getConfiguration().get("DgraphServer", "");
      String entityServer = context.getConfiguration().get("EntityServer", "");
      String[] dServers = dgraphServer.split(",");
      String[] enServers = entityServer.split(":");
      logger.info("Server:" + dgraphServer + " , server:" + entityServer + ", batch:" + setBatch);
      dClient = new DClient(dServers);
      entityIdClient = new EntityIdClient(enServers[0],Integer.parseInt(enServers[1]));
      mos = new MultipleOutputs<>(context);
    }

    @Override
    protected void cleanup(Context context) throws IOException, InterruptedException {
      super.cleanup(context);
    }

    private void getCompnays(List<JsonObject> dealingJsonObject, List<Company> companyList) {
      for (JsonObject jsonObject : dealingJsonObject) {
        JsonArray workExpr = jsonObject.getJsonArray("work_experiences", new JsonArray());
        int workExprSize = workExpr.size();
        for (int i = 0; i < workExprSize; i ++) {
          JsonObject companyObject = workExpr.getJsonObject(i).getJsonObject("company", new JsonObject());
          String companyNorName = companyObject.getString("name_nor", "");
          String companyName = companyObject.getString("name", "");
          Company company = new Company();
          company.setUnique_ids(Arrays.asList(companyName, companyNorName));
          companyList.add(company);
        }
      }
    }

    private void checkExistCompnayUidCandidates(List<JsonObject> dealingJsonObject, List<String>
        companyUid, List<Candidate> candidateList) {
      int index = 0;
      for (JsonObject jsonObject : dealingJsonObject) {
        String docId = jsonObject.getString("doc_id", "");
        JsonObject info = jsonObject.getJsonObject("info", new JsonObject());
        String candidateName = info.getString("chinese_name", "");
        String gender = info.getString("gender", "");
        JsonArray workExpr = jsonObject.getJsonArray("work_experiences", new JsonArray());
        int workExprSize = workExpr.size();
        List<Company> candidateCompanys = new ArrayList<>();
        for (int i = 0; i < workExprSize; i ++) {
          Company company = new Company();
          String uid = companyUid.get(index);
          if (!"".equals(uid)) {
            company.setUid(uid);
            candidateCompanys.add(company);
          }
          index = index + 1;
        }
        Candidate candidate = new Candidate();
        if (!"".equals(docId)) {
          candidate.setUnique_id(docId);
          candidate.setUnique_ids(Arrays.asList(docId));
          candidate.setName(candidateName);
          candidate.setType("候选人");
          candidate.setGender(gender);
          candidate.setCandidate_company(candidateCompanys);
          candidateList.add(candidate);
        }
      }
      if (companyUid.size() > index) {
        indexNotMatchSize.increment(1L);
      }
    }

    @Override
    public void run(Context context) throws IOException, InterruptedException {
      setup(context);
      List<Candidate> candidateList = new ArrayList<>();
      List<Company> companyList = new ArrayList<>();
      List<JsonObject> dealingJsonObject = new ArrayList<>();
      List<String> originContent = new ArrayList<>();
      String type = "候选人";
      int batch = 0;
      while (context.nextKeyValue()) {
        Text text = context.getCurrentValue();
        String info = text.toString();
        JsonObject infoObject;
        int lastIndexOf = info.lastIndexOf("##########");
        String docId = info.substring(lastIndexOf + "##########".length());
        String json = info.substring(0, lastIndexOf);
        try {
          infoObject = new JsonObject(json);
        } catch (Exception e) {
          errorCounter.increment(1);
          continue;
        }
        JsonObject basic = infoObject.getJsonObject("basic", new JsonObject());
        basic.put("doc_id", docId);
        dealingJsonObject.add(basic);
        if (batch >= setBatch) {
          if (source == 1) {
            // ..todo
          } else if (source == 2) {
            batchCandidatesPut(dealingJsonObject, companyList, candidateList, type, originContent);
          }
          dealingJsonObject.clear();
          candidateList.clear();
          companyList.clear();
          originContent.clear();
          batch = 0;
        }
        jsonSuccessCounter.increment(1L);
        batch++;
      }
      if (batch > 0) {
        if (source == 1) {
          // ..todo
        } else if (source == 2) {
          batchCandidatesPut(dealingJsonObject, companyList, candidateList, type, originContent);
        }
      }
      cleanup(context);
    }

    private List<Industry> getIndustry(List<Company> companies) {
      List<Industry> industryList = new ArrayList<>();
      for (Company company : companies) {
        industryList.addAll(company.getIndustry());
      }
      return industryList;
    }
    private void batchCandidatesPut(List<JsonObject> dealingJsonObject, List<Company> companyList,
                                    List<Candidate> candidateList, String type, List<String> originContent) {
      getCompnays(dealingJsonObject, companyList);
      List<String> companyUid = entityIdClient.checkEntityList(companyList, "公司");
      checkExistCompnayUidCandidates(dealingJsonObject, companyUid, candidateList);
      Map<String, List<String>> candidateRet;
      if (checkUid > 0) {
        List<Candidate> newCandidateList = new ArrayList<>();
        entityIdClient.getNoneExistEntityList(candidateList, type, newCandidateList);
        candidateRet = NodeUtil.insertEntity(dClient, newCandidateList);
        if (newCandidateList.size() > 0) {
          if (candidateRet.size() == 0) {
            writeOriginContentMap(mos, originContent);
            timeOutErrorCounter.increment(1L);
          }
        }
      } else {
        candidateRet = NodeUtil.insertEntity(dClient, candidateList);
        if (candidateRet.size() == 0) {
          writeOriginContentMap(mos, originContent);
          timeOutErrorCounter.increment(1L);
        }
      }
      entityIdClient.putFeedEntityWithNames(candidateRet, type);
      originSuccessCounter.increment(candidateRet.size());
      writeUidMap(mos, candidateRet);
    }
  }

  private static  void writeUidMap(MultipleOutputs<Text, Text> mos, Map<String, List<String>> uidMap) {
    String dir = "uidmap";
    Set<Map.Entry<String, List<String>>> entrySet=  uidMap.entrySet();
    Iterator<Map.Entry<String, List<String>>> iterator = entrySet.iterator();
    while(iterator.hasNext()) {
      Map.Entry<String, List<String>> entry = iterator.next();
      String key = entry.getKey();
      List<String> values = entry.getValue();
      StringBuffer sb = new StringBuffer();
      for (String value : values) {
        sb.append(value + ",");
      }
      try {
        mos.write(dir, new Text(sb.toString()), new Text(key), dir + "/part");
      } catch (IOException e) {
        e.printStackTrace();
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
  }
  private static void writeOriginContentMap(MultipleOutputs<Text, Text> mos, List<String> contents) {
    String dir = "origincontent";
    for (String info : contents) {
      int index = info.indexOf("\t");
      String key = info.substring(0, index);
      String value = info.substring(index + "\t".length());
      try {
        mos.write(dir, new Text(key), new Text(value), dir + "/part");
      } catch (IOException e) {
        e.printStackTrace();
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
  }

  public void configJob(Job job, String input, String output) throws Exception {
    job.setJarByClass(CandidateEntityPutToDgraphMapred.class);
    job.setJobName("CompanyEntityPutToDgraphMapred - " + input.substring(input.lastIndexOf("/") +
        1));
    job.setMapperClass(MyMap.class);
    FileInputFormat.setInputPaths(job, input);
    FileSystem fs = FileSystem.get(job.getConfiguration());
    Path outPath = new Path(output);
    fs.delete(outPath, true);
    FileOutputFormat.setOutputPath(job, outPath);
    List<String> list = Arrays.asList("uidmap", "origincontent");
    for (String dir : list) {
      MultipleOutputs.addNamedOutput(job, dir, TextOutputFormat.class, Text.class, Text.class);
    }
    job.setInputFormatClass(CombineTextInputFormat.class);
    job.setOutputFormatClass(TextOutputFormat.class);
    job.setMapOutputKeyClass(Text.class);
    job.setMapOutputValueClass(Text.class);
    job.setOutputKeyClass(Text.class);
    job.setOutputValueClass(Text.class);
    job.setNumReduceTasks(0);
  }
  @SuppressWarnings("RegexpSinglelineJava")
  public int run(String[] args) throws Exception {
    if (args.length < 8) {
      System.err.println("Usage: CompanyEntityPutToDgraphMapred <Input> <ConfDir> <OutPut> " +
          "<DgraphServer> <EntityServer> <batch> <checkUid> <source>");
      System.exit(1);
    }
    String input = args[0];
    String confDir = args[1];
    String output = args[2];
    Configuration conf = new Configuration();
    conf.set("DgraphServer", args[3]);
    conf.set("EntityServer", args[4]);
    conf.setInt("batch", Integer.valueOf(args[5]));
    conf.setInt("checkUid", Integer.valueOf(args[6]));
    conf.setInt("source", Integer.valueOf(args[7]));
    conf.setInt("reduceNum", Integer.valueOf(args[7]));
    conf.addResource(confDir + "/core-site.xml");
    conf.addResource(confDir + "/hdfs-site.xml");
    conf.addResource(confDir + "/hbase-site.xml");
    conf.addResource(confDir + "/yarn-site.xml");
    // java.lang.NoSuchMethodError: com.google.protobuf
    // conf.setBoolean(MRJobConfig.MAPREDUCE_JOB_USER_CLASSPATH_FIRST, true);
    conf.setLong("mapreduce.input.fileinputformat.split.maxsize", 1024L * 1024 * 1024);
    conf.set("mapreduce.reduce.shuffle.memory.limit.percent", "0.25");
    System.setProperty("java.security.krb5.conf", confDir + "/krb5.conf");
    try {
      UserGroupInformation.loginUserFromKeytab("mindcube@WGQ.HIGGS.COM",
          confDir + "/krb5_mindcube.keytab");
    } catch (IOException e) {
      logger.info("key tab error:" + e.getMessage());
    }
    Job job = Job.getInstance(conf);
    configJob(job, input, output);
    job.waitForCompletion(true);
    return 0;
  }

  @SuppressWarnings("RegexpSinglelineJava")
  public static void main(String[] args) throws Exception {
    args = new String[] {
        "test/candidate/test",
        "/src/main/resources",
        "test_out/candidate/test",
        "172.20.0.68:9080",
        "172.20.0.14:26544",
        "70",
        "0",
        "2"
    };
    int exitCode = new CandidateEntityPutToDgraphMapred().run(args);
    System.exit(exitCode);
  }

}

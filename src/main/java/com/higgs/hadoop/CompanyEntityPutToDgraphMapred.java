package com.higgs.hadoop;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Counter;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.CombineTextInputFormat;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.MultipleOutputs;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.util.Tool;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.higgs.client.EntityIdClient;
import com.higgs.dgraph.DClient;
import com.higgs.dgraph.node.Company;
import com.higgs.dgraph.node.Industry;
import com.higgs.dgraph.node.Label;
import com.higgs.dgraph.node.NodeUtil;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;


/**
 * Created by Jerry on 2017/4/12. 输入文件格式：（docId \t json) 输出到dgraph
 */
public class CompanyEntityPutToDgraphMapred extends Configured implements Tool {

  private static Logger logger = LoggerFactory.getLogger(CompanyEntityPutToDgraphMapred.class);

  public static class MyMap extends Mapper<LongWritable, Text, Text, Text> {
    private Counter skipperCounter;
    private Counter noFiledCounter;
    private Counter originSuccessCounter;
    private Counter jsonSuccessCounter;
    private Counter errorCounter;
    private DClient dClient;
    private EntityIdClient entityIdClient;
    private int setBatch = 0;
    private int checkUid = 0;
    private int source = 0;
    private Counter timeOutErrorCounter;
    private MultipleOutputs<Text, Text> mos;

    @Override
    protected void setup(Context context) throws IOException, InterruptedException {
      super.setup(context);
      mos = new MultipleOutputs<>(context);
      skipperCounter = context.getCounter("runner", "skipperCounter");
      timeOutErrorCounter = context.getCounter("runner", "timeOutErrorCounter");
      errorCounter = context.getCounter("runner", "errorCounter");
      noFiledCounter = context.getCounter("runner", "noFiledCounter");
      originSuccessCounter = context.getCounter("runner", "originSuccessCounter");
      jsonSuccessCounter = context.getCounter("runner", "jsonSuccessCounter");
      setBatch = context.getConfiguration().getInt("batch", 100);
      checkUid = context.getConfiguration().getInt("checkUid", 0);
      source = context.getConfiguration().getInt("source", 0);
      String dgraphServer = context.getConfiguration().get("DgraphServer", "");
      String entityServer = context.getConfiguration().get("EntityServer", "");
      String[] dServers = dgraphServer.split(",");
      String[] enServers = entityServer.split(":");
      logger.info("Server:" + dgraphServer + " , server:" + entityServer + ", batch:" + setBatch);
      dClient = new DClient(dServers);
      entityIdClient = new EntityIdClient(enServers[0], Integer.parseInt(enServers[1]));
    }

    @Override
    protected void cleanup(Context context) throws IOException, InterruptedException {
      super.cleanup(context);
    }

    @Override
    public void run(Context context) throws IOException, InterruptedException {
      setup(context);
      List<Company> companyList = new ArrayList<Company>();
      List<String> originContent = new ArrayList<>();
      String type = "公司";
      int batch = 0;
      while (context.nextKeyValue()) {
        JsonObject infoObject = new JsonObject();
        String info = context.getCurrentValue().toString().trim();
        originContent.add(info);
        int index = info.indexOf("\t");
        String json = info.substring(index + "\t".length());
        try {
          infoObject = new JsonObject(json);
        } catch (Exception e) {
          errorCounter.increment(1);
          e.printStackTrace();
        }

        String qccUnique = infoObject.getString("qcc_unique", "");
        String name = infoObject.getString("name", "");
        String location = infoObject.getString("location", "");
        String establish_at = infoObject.getString("establish_at", "");
        String legal_person = infoObject.getString("legal_person", "");
        JsonArray normed_industry_names = infoObject.getJsonArray("normed_industry_names", new
            JsonArray());
        JsonArray normed_industry_codes = infoObject.getJsonArray("normed_industry_codes", new
            JsonArray());
        if (!"".equals(name)) {
          int industrySize = normed_industry_names.size();
          List<Industry> industryList = new ArrayList<>();
          for (int j = 0; j < industrySize; j++) {
            String industryName = normed_industry_names.getString(j);
            String industryCode = normed_industry_codes.getString(j);
            Industry industry = new Industry();
            industry.setCode(Integer.parseInt(industryCode));
            industry.setName(industryName);
            industry.setUnique_ids(Arrays.asList(industryCode, industryName));
            industryList.add(industry);
          }
          Company company = new Company();
          company.setName(name);
          company.setUnique_ids(Arrays.asList(name));
          company.setLocation(location);
          company.setEstablish_at(establish_at);
          company.setLegal_person(legal_person);
          company.setType(type);
          company.setIndustry(industryList);
          companyList.add(company);
          batch++;
        } else {
          skipperCounter.increment(1L);
        }
        if (batch >= setBatch) {
          if (source == 1) {
            // json object
            Map<String, List<String>> ret = NodeUtil.putEntity(dClient, companyList);
            entityIdClient.putFeedEntityWithUidNamesMap(ret, type);
            originSuccessCounter.increment(ret.size());
            writeUidMap(mos, ret);
          } else if (source == 2) {
            batchPut(companyList, originContent, type);
          }
          companyList.clear();
          originContent.clear();
          batch = 0;
        }
        jsonSuccessCounter.increment(1L);
      }
      if (batch > 0) {
        if (source == 1) {
          // json object : need test
          Map<String, List<String>> ret = NodeUtil.putEntity(dClient, companyList);
          entityIdClient.putFeedEntityWithUidNamesMap(ret, type);
          originSuccessCounter.increment(ret.size());
          writeUidMap(mos, ret);
        } else if (source == 2) {
          batchPut(companyList, originContent, type);
        }
      }
      cleanup(context);
    }

    private void batchPut(List<Company> companyList, List<String> originContent, String type) {
      List<Industry> checkIndustries = getIndustry(companyList);
      entityIdClient.putEntityListUid(checkIndustries, "行业");
      Map<String, List<String>> companyRet;
      if (checkUid > 0) {
        List<Company> newComapnyList = new ArrayList<>();
        entityIdClient.getNoneExistEntityList(companyList, type, newComapnyList);
        companyRet = NodeUtil.insertEntity(dClient,newComapnyList);
        if (newComapnyList.size() > 0) {
          if (companyRet.size() == 0) {
            writeOriginContentMap(mos, originContent);
            timeOutErrorCounter.increment(1L);
          }
        }
      } else {
        companyRet = NodeUtil.insertEntity(dClient,companyList);
        if (companyRet.size() == 0) {
          writeOriginContentMap(mos, originContent);
          timeOutErrorCounter.increment(1L);
        }
      }
      entityIdClient.putFeedEntityWithUidNamesMap(companyRet, type);
      originSuccessCounter.increment(companyRet.size());
      writeUidMap(mos, companyRet);
    }
  }

  private static List<Label> getLabeledCompany(List<Company> companies) {
    List<Label> labelList = new ArrayList<>();
    for (Company company : companies) {
      Label label = new Label();
      label.setUid("0x118b");
      // label.setLabel_name("公司类型");
      label.setCompany(company);
      labelList.add(label);
    }
    return labelList;
  }

  private static List<Industry> getIndustry(List<Company> companies) {
    List<Industry> industryList = new ArrayList<>();
    for (Company company : companies) {
      industryList.addAll(company.getIndustry());
    }
    return industryList;
  }

  private static void writeUidMap(MultipleOutputs<Text, Text> mos, Map<String, List<String>> uidMap) {
    String dir = "uidmap";
    Set<Map.Entry<String, List<String>>> entrySet = uidMap.entrySet();
    Iterator<Map.Entry<String, List<String>>> iterator = entrySet.iterator();
    while (iterator.hasNext()) {
      Map.Entry<String, List<String>> entry = iterator.next();
      String key = entry.getKey();
      List<String> values= entry.getValue();
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

  private static void writeOriginContentMap(MultipleOutputs<Text, Text> mos, List<String>
      contents) {
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

  public static class Reduce extends Reducer<Text, Text, Text, Text> {

    private Counter counterReducer;
    private MultipleOutputs<Text, Text> mos;
    private Counter errorCounter;
    private DClient dClient;
    private EntityIdClient entityIdClient;
    private int setBatch = 0;
    private int checkUid = 0;
    private int source = 0;
    private Counter originSuccessCounter;
    private Counter jsonSuccessCounter;
    private Counter timeOutErrorCounter;
    private Counter skipperCounter;

    @Override
    protected void setup(Context context) throws IOException, InterruptedException {
      super.setup(context);
      mos = new MultipleOutputs<>(context);
      counterReducer = context.getCounter("reduce", "counterReducer");
      errorCounter = context.getCounter("reduce", "errorCounter");
      originSuccessCounter = context.getCounter("reduce", "originSuccessCounter");
      jsonSuccessCounter = context.getCounter("reduce", "jsonSuccessCounter");
      timeOutErrorCounter = context.getCounter("reduce", "timeOutErrorCounter");
      skipperCounter = context.getCounter("reduce", "skipperCounter");
      setBatch = context.getConfiguration().getInt("batch", 100);
      checkUid = context.getConfiguration().getInt("checkUid", 0);
      source = context.getConfiguration().getInt("source", 0);
      String dgraphServer = context.getConfiguration().get("DgraphServer", "");
      String entityServer = context.getConfiguration().get("EntityServer", "");
      String[] dServers = dgraphServer.split(",");
      String[] enServers = entityServer.split(":");
      logger.info("Server:" + dgraphServer + " , server:" + entityServer + ", batch:" + setBatch);
      dClient = new DClient(dServers);
      entityIdClient = new EntityIdClient(enServers[0], Integer.parseInt(enServers[1]));
    }

    @Override
    protected void cleanup(Context context) throws IOException, InterruptedException {
      super.cleanup(context);
    }

    @Override
    public void run(Context context) throws IOException, InterruptedException {
      setup(context);
      int batch = 0;
      String type = "公司";
      List<Company> companyList = new ArrayList<>();
      List<String> originContent = new ArrayList<>();
      while (context.nextKey()) {
        String key = context.getCurrentKey().toString();
        Iterable<Text> iterable = context.getValues();
        for (Text json : iterable) {
          JsonObject infoObject = new JsonObject();
          originContent.add(key + "\t" + json.toString());
          try {
            infoObject = new JsonObject(json.toString());
          } catch (Exception e) {
            errorCounter.increment(1);
            e.printStackTrace();
          }
          String qccUnique = infoObject.getString("qcc_unique", "-");
          String name = infoObject.getString("name", "-");
          String location = infoObject.getString("location", "-");
          String establish_at = infoObject.getString("establish_at", "-");
          String legal_person = infoObject.getString("legal_person", "-");
          JsonArray normed_industry_names = infoObject.getJsonArray("normed_industry_names", new
              JsonArray());
          JsonArray normed_industry_codes = infoObject.getJsonArray("normed_industry_codes", new
              JsonArray());
          Label label = new Label();
          label.setLabel_name("公司类型");
          // "公司类型": "0x118b"
          label.setUid("0x118b");

          if (!"".equals(name)) {
            int industrySize = normed_industry_names.size();
            List<Industry> industryList = new ArrayList<>();
            for (int j = 0; j < industrySize; j++) {
              String industryName = normed_industry_names.getString(j);
              int industryCode = normed_industry_codes.getInteger(j);
              Industry industry = new Industry();
              industry.setCode(industryCode);
              industry.setName(industryName);
              industry.setUnique_ids(Arrays.asList(industryName, String.valueOf(industryCode)));
              industryList.add(industry);
            }
            Company company = new Company();
            company.setName(name);
            company.setUnique_ids(Arrays.asList(name));
            company.setLocation(location);
            company.setEstablish_at(establish_at);
            company.setLegal_person(legal_person);
            company.setType(type);
            company.setIndustry(industryList);
            company.setHas_label(label);
            companyList.add(company);
            batch++;
          } else {
            // skip ...
            skipperCounter.increment(1L);
          }
          if (batch >= setBatch) {
            List<Industry> checkIndustries = getIndustry(companyList);
            entityIdClient.putEntityListUid(checkIndustries, "行业");
            Map<String, List<String>> companyRet = NodeUtil.insertEntity(dClient, companyList);
            // Map<String, String> ret = NodeUtil.insertEntity(dClient, entityIdClient,
            // getLabeledCompany(companyList), type, checkUid);
            entityIdClient.putFeedEntityWithUidNamesMap(companyRet, type);
            if (companyRet.size() == 0) {
              writeOriginContentMap(mos, originContent);
              timeOutErrorCounter.increment(1L);
            }
            originSuccessCounter.increment(companyRet.size());
            writeUidMap(mos, companyRet);
            companyList.clear();
            batch = 0;
            originContent.clear();
          }
          jsonSuccessCounter.increment(1L);
          // for values
        }
        // while
      }
      cleanup(context);
    }

    @Override
    protected void reduce(Text text, Iterable<Text> iterable, Context context) throws
        IOException, InterruptedException {
      super.reduce(text, iterable, context);
    }
  }


  public void configJob(Job job, String input, String output) throws Exception {
    job.setJarByClass(CompanyEntityPutToDgraphMapred.class);
    job.setJobName("CompanyEntityPutToDgraphMapred -" + input.substring(input.lastIndexOf("/") +
        1));
    job.setMapperClass(MyMap.class);
    FileInputFormat.setInputPaths(job, input);
    FileSystem fs = FileSystem.get(job.getConfiguration());
    Path outPath = new Path(output);
    fs.delete(outPath, true);
    FileOutputFormat.setOutputPath(job, outPath);
    job.setInputFormatClass(CombineTextInputFormat.class);
    job.setOutputFormatClass(TextOutputFormat.class);
    job.setMapOutputKeyClass(Text.class);
    job.setMapOutputValueClass(Text.class);
    job.setOutputKeyClass(Text.class);
    job.setOutputValueClass(Text.class);
    job.setReducerClass(Reduce.class);
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
    conf.setLong("mapreduce.input.fileinputformat.split.maxsize", 256L * 1024 * 1024);
    conf.set("mapreduce.reduce.shuffle.memory.limit.percent", "0.25");
    Job job = Job.getInstance(conf);
    configJob(job, input, output);
    job.waitForCompletion(true);
    return 0;
  }

  @SuppressWarnings("RegexpSinglelineJava")
  public static void main(String[] args) throws Exception {
    int exitCode = new CompanyEntityPutToDgraphMapred().run(args);
    System.exit(exitCode);
  }
}

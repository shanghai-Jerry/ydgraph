package hadoop;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Counter;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.MRJobConfig;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.lib.input.CombineTextInputFormat;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.util.Tool;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import client.EntityIdClient;
import dgraph.DClient;
import dgraph.node.Company;
import dgraph.node.Industry;
import dgraph.node.Label;
import dgraph.node.NodeUtil;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;


/**
 * Created by Jerry on 2017/4/12.
 * 输入文件格式：（docId \t json)
 * 输出到dgraph
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

    @Override
    protected void setup(Context context) throws IOException, InterruptedException {
      super.setup(context);
      skipperCounter = context.getCounter("runner", "skipperCounter");
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
      entityIdClient = new EntityIdClient(enServers[0],Integer.parseInt(enServers[1]));
    }

    @Override
    protected void cleanup(Context context) throws IOException, InterruptedException {
      super.cleanup(context);
    }

    @Override
    public void run(Context context) throws IOException, InterruptedException {
      setup(context);
      List<Company> companyList = new ArrayList<Company>();
      String type = "公司";
      int batch = 0;
      while (context.nextKeyValue()) {
        JsonObject infoObject = new JsonObject();
        String info = context.getCurrentValue().toString().trim();
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
            industry.setUnique_id(industryName);
            industryList.add(industry);
          }
          Company company = new Company();
          company.setName(name);
          company.setUnique_id(name);
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
            Map<String, String> ret = NodeUtil.putEntity(dClient, companyList);
            entityIdClient.putFeedEntity(ret,  type);
            originSuccessCounter.increment(ret.size());
            writeUidMap(context, ret);
          } else if (source == 2) {
            long startTime = System.currentTimeMillis();
            List<Industry> checkIndustries = getIndustry(companyList);
            entityIdClient.checkEntityListAndPutUid(checkIndustries, "行业");
            Map<String, String> companyRet = NodeUtil.insertEntity(dClient, companyList);
            NodeUtil.putEntityUid(companyList, companyRet);
            long endStart = System.currentTimeMillis();
            // logger.info("insertEntity time:" + (endStart - startTime) + " ms");
            startTime = System.currentTimeMillis();
            entityIdClient.putFeedEntity(companyRet, type);
            endStart = System.currentTimeMillis();
            // logger.info("putFeedEntity time:" + (endStart - startTime) + " ms");
            originSuccessCounter.increment(companyRet.size());
            Map<String, String> ret = NodeUtil.insertEntity(dClient, getLabeledIndustry(companyList));
            writeUidMap(context, companyRet);
          }
          companyList.clear();
          batch = 0;
        }
        jsonSuccessCounter.increment(1L);
      }
      if (batch > 0) {
        if (source == 1) {
          // json object
          Map<String, String> ret = NodeUtil.putEntity(dClient, companyList);
          entityIdClient.putFeedEntity(ret,  type);
          originSuccessCounter.increment(ret.size());
          writeUidMap(context, ret);
        } else if (source == 2) {
          long startTime = System.currentTimeMillis();
          List<Industry> checkIndustries = getIndustry(companyList);
          entityIdClient.checkEntityListAndPutUid(checkIndustries, "行业");
          Map<String, String> companyRet = NodeUtil.insertEntity(dClient, companyList);
          NodeUtil.putEntityUid(companyList, companyRet);
          long endStart = System.currentTimeMillis();
          // logger.info("insertEntity time:" + (endStart - startTime) + " ms");
          startTime = System.currentTimeMillis();
          entityIdClient.putFeedEntity(companyRet, type);
          endStart = System.currentTimeMillis();
          // logger.info("putFeedEntity time:" + (endStart - startTime) + " ms");
          originSuccessCounter.increment(companyRet.size());
          Map<String, String> ret = NodeUtil.insertEntity(dClient, getLabeledIndustry(companyList));
          writeUidMap(context, companyRet);
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

    private List<Label> getLabeledIndustry(List<Company> companies) {
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

    private void writeUidMap(Context context, Map<String, String> uidMap) {
      Set<Map.Entry<String, String>> entrySet=  uidMap.entrySet();
      Iterator<Map.Entry<String, String>> iterator = entrySet.iterator();
      while(iterator.hasNext()) {
        Map.Entry<String, String> entry = iterator.next();
        String key = entry.getKey();
        String value = entry.getValue();
        try {
          context.write(new Text(key), new Text(value));
        } catch (IOException e) {
          e.printStackTrace();
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }
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

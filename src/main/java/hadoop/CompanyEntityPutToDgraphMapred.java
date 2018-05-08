package hadoop;


import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Counter;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.hadoop.util.Tool;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import client.EntityIdClient;
import dgraph.Config;
import dgraph.DClient;
import dgraph.node.Company;
import dgraph.node.NodeUtil;
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

  public static class Map extends Mapper<LongWritable, Text, Text, Text> {
    private Counter skipperCounter;
    private Counter noFiledCounter;
    private Counter originSuccessCounter;
    private Counter jsonSuccessCounter;
    private Counter errorCounter;
    private DClient dClient;
    private EntityIdClient entityIdClient;

    @Override
    protected void setup(Context context) throws IOException, InterruptedException {
      super.setup(context);
      skipperCounter = context.getCounter("runner", "skipperCounter");
      errorCounter = context.getCounter("runner", "errorCounter");
      noFiledCounter = context.getCounter("runner", "noFiledCounter");
      originSuccessCounter = context.getCounter("runner", "originSuccessCounter");
      jsonSuccessCounter = context.getCounter("runner", "jsonSuccessCounter");
      dClient = new DClient(Config.TEST_HOSTNAME, Config.TEST_PORT);
      entityIdClient = new EntityIdClient(Config.EntityId_Host, Config.EntityIdService_PORT);
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
        if (!"".equals(qccUnique)) {
          List<String> names = new ArrayList<String>();
          Company company = new Company();
          names.add(qccUnique);
          names.add(name);
          company.setName(name);
          company.setNames(names);
          company.setLocation(location);
          company.setEstablish_at(establish_at);
          company.setLegal_person(legal_person);
          company.setType(type);
          companyList.add(company);
        }
        if (batch > Config.batch) {
          NodeUtil.putEntity(dClient, entityIdClient, companyList, type, 1);
          companyList.clear();
          batch = 0;
        }
        batch++;
      }
      if (batch > 0) {
        NodeUtil.putEntity(dClient, entityIdClient, companyList, type, 1);
      }
      cleanup(context);
    }
  }

  public void configJob(Job job, String input, String output) throws Exception {
    job.setJarByClass(CompanyEntityPutToDgraphMapred.class);
    job.setJobName("CompanyEntityPutToDgraphMapred -" + input.substring(input.lastIndexOf("/") +
        1));
    job.setMapperClass(Map.class);
    FileInputFormat.setInputPaths(job, input);
    FileSystem fs = FileSystem.get(job.getConfiguration());
    Path outPath = new Path(output);
    fs.delete(outPath, true);
    FileOutputFormat.setOutputPath(job, outPath);
    job.setMapOutputKeyClass(Text.class);
    job.setMapOutputValueClass(Text.class);
    job.setNumReduceTasks(0);
  }
  @SuppressWarnings("RegexpSinglelineJava")
  public int run(String[] args) throws Exception {
    if (args.length < 3) {
      System.err.println("Usage: CompanyEntityPutToDgraphMapred <Input> <ConfDir> <OutPut>");
      System.exit(1);
    }
    String input = args[0];
    String confDir = args[1];
    String output = args[2];
    Configuration conf = new Configuration();
    conf.addResource(confDir + "/core-site.xml");
    conf.addResource(confDir + "/hdfs-site.xml");
    conf.addResource(confDir + "/hbase-site.xml");
    conf.addResource(confDir + "/yarn-site.xml");
    conf.set("mapreduce.reduce.shuffle.memory.limit.percent", "0.25");
    System.setProperty("java.security.krb5.conf", confDir + "/krb5.conf");
    UserGroupInformation.setConfiguration(conf);
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
    args = new String[]{
        "/user/mindcube/company/test_data",
        "/Users/devops/workspace/hbase-Demo/src/main/resources",
        "/user/mindcube/test_out/test_data",
    };
    int exitCode = new CompanyEntityPutToDgraphMapred().run(args);
    System.exit(exitCode);
  }
}

package hadoop.dgraph;


import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Counter;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.hadoop.util.Tool;

import java.io.IOException;

import dgraph.Config;
import dgraph.DClient;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

// import client.CompanyNormalizationClient;

/**
 * Created by Jerry on 2017/4/12. 简历解析结果：originResumeContent 存入hbase 输入文件格式：（docId \t
 * resume_extractor_json）来源 去重后 无输出文件格式
 */
public class TestResumePutMapred extends Configured implements Tool {

  private static Logger logger = LoggerFactory.getLogger(TestResumePutMapred.class);

  public static class Map extends Mapper<LongWritable, Text, NullWritable, Put> {
    private Counter skipperCounter;
    private Counter noFiledCounter;
    private Counter originSuccessCounter;
    private Counter jsonSuccessCounter;
    private DClient dClient;
    private final String TEST_HOSTNAME = "172.20.0.68";
    private final int TEST_PORT = 9080;
    private String schema = "uid:int . \n" + "chineseName:string @index(exact,term) . \n" +
        "gender:int @index(int) . \n" + "currentJobTitle:string @index(exact,term) .\n" +
        "industries: uid @reverse . \n" + "code:int @index(int) . \n" + "title:string @index" +
        "(exact,term) . \n" + "seniority:int @index(int) . \n" + "salary:float @index(float) . " +
        "\n" + "educationDegree:int @index(int) . \n" + "pastWorkExperiences:uid @reverse . \n" +
        "org:uid @reverse . \n" + "suggest:string @index(exact,term) .\n" + "jobTitle: string " +
        "@index(exact,term) .\n" + "educationExperiences:uid @reverse . \n" + "school:uid " +
        "@reverse . \n" + "age:int @index(int) . \n";

    @Override
    protected void setup(Context context) throws IOException, InterruptedException {
      super.setup(context);
      skipperCounter = context.getCounter("runner", "skipperCounter");
      noFiledCounter = context.getCounter("runner", "noFiledCounter");
      originSuccessCounter = context.getCounter("runner", "originSuccessCounter");
      jsonSuccessCounter = context.getCounter("runner", "jsonSuccessCounter");
      dClient = new DClient(Config.TEST_HOSTNAME);
      // dClient.InitDict("/Users/devops/workspace/hbase-Demo/src/StartMain/resources/school_dict
      // .txt");
      dClient.alterSchema(schema);
    }

    @Override
    protected void cleanup(Context context) throws IOException, InterruptedException {
      super.cleanup(context);
    }

    @Override
    public void run(Context context) throws IOException, InterruptedException {
      setup(context);
      try {
        while (context.nextKeyValue()) {
          String info = context.getCurrentValue().toString().trim();
          // 提取特征处理
          String[] strings = info.split("\t");
          if (strings.length == 2) {
            String rowKey = strings[0];
            JsonObject infoObject = new JsonObject(strings[1]);
            jsonSuccessCounter.increment(1L);
          } else {
            skipperCounter.increment(1);
          }
        }
      } finally {
        cleanup(context);
      }
    }
  }

  public void configJob(Job job, String input, String tableName) throws Exception {
    job.setJarByClass(TestResumePutMapred.class);
    job.setJobName("hadoop.dgraph put -" + input.substring(input.lastIndexOf("/") + 1));
    job.setMapperClass(Map.class);
    FileInputFormat.setInputPaths(job, input);
    FileSystem fs = FileSystem.get(job.getConfiguration());
    Path outPath = new Path("resume/test/test_out");
    fs.delete(outPath, true);
    FileOutputFormat.setOutputPath(job, outPath);
    job.setMapOutputKeyClass(Text.class);
    job.setMapOutputValueClass(Text.class);
    job.setNumReduceTasks(0);
  }

  @SuppressWarnings("RegexpSinglelineJava")
  public int run(String[] args) throws Exception {
    if (args.length < 3) {
      System.err.println("Usage: ResumeOriginContentMapredTest <Input> <ConfDir> <tableName>");
      System.exit(1);
    }
    String input = args[0];
    String confDir = args[1];
    String tableName = args[2];
    System.setProperty("java.security.krb5.conf", confDir + "/krb5.conf");
    Configuration conf = new Configuration();
    conf.addResource(confDir + "/core-site.xml");
    conf.addResource(confDir + "/hdfs-site.xml");
    conf.addResource(confDir + "/hbase-site.xml");
    conf.addResource(confDir + "/yarn-site.xml");
    conf.set("mapreduce.reduce.shuffle.memory.limit.percent", "0.25");
    UserGroupInformation.setConfiguration(conf);
    try {
      UserGroupInformation.loginUserFromKeytab("idmg@WGQ.HIGGS.COM", confDir + "/krb5.keytab");
    } catch (IOException e) {
      logger.info("key tab error:" + e.getMessage());
    }
    Job job = Job.getInstance(conf);
    configJob(job, input, tableName);
    job.waitForCompletion(true);
    return 0;
  }

  @SuppressWarnings("RegexpSinglelineJava")
  public static void main(String[] args) throws Exception {
    args = new String[]{"/Users/devops/workspace/hbase-Demo/src/StartMain/resources/part-m-00371" +
        "", "/Users/devops/workspace/hbase-Demo/src/StartMain/resources", "idmg:resume_test",};
    int exitCode = new TestResumePutMapred().run(args);
    System.exit(exitCode);
  }
}

package hadoop.dgraph;

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

import dgraph.Config;
import dgraph.DClient;
import dgraph.node.Company;
import dgraph.node.Label;
import dgraph.node.NodeUtil;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

/**
 * User: JerryYou
 *
 * Date: 2018-05-10
 *
 * Copyright (c) 2018 devops
 *
 * <<licensetext>>
 */
public class TestCompanyLabelToDgraph extends Configured implements Tool {

  private static Logger logger = LoggerFactory.getLogger(TestCompanyLabelToDgraph.class);


  public static class Map extends Mapper<LongWritable, Text, Text, Text> {

    private Counter errorCounter;
    private Counter jsonSuccessCounter;
    private DClient dClient;

    @Override
    protected void setup(Context context) throws IOException, InterruptedException {
      super.setup(context);
      errorCounter = context.getCounter("runner", "errorCounter");
      jsonSuccessCounter = context.getCounter("runner", "jsonSuccessCounter");
      dClient = new DClient(Config.TEST_HOSTNAME);
    }

    @Override
    protected void cleanup(Context context) throws IOException, InterruptedException {
      super.cleanup(context);
    }

    @Override
    protected void map(LongWritable key, Text value, Context context) throws IOException,
        InterruptedException {
    }

    @Override
    public void run(Context context) throws IOException, InterruptedException {
      setup(context);
      List<Company> companyList = new ArrayList<Company>();
      int batch = 0;
      while (context.nextKeyValue()) {
        String info = context.getCurrentValue().toString();
        int index = info.lastIndexOf("\t");
        String uid = info.substring(index + "\t".length());
        if (!uid.startsWith("0x")) {
          errorCounter.increment(1);
        } else {
          Label label = new Label();
          label.setUid("0x40f0ae");
          Company company = new Company();
          company.setUid(uid);
          company.setHas_label(label);
          companyList.add(company);
          batch++;
        }
        if (batch >= 200) {
          NodeUtil.addEntityEdge(dClient, companyList);
          companyList.clear();
          batch = 0;
        }
        jsonSuccessCounter.increment(1);
      }
      if (batch > 0) {
        NodeUtil.addEntityEdge(dClient, companyList);
      }

      cleanup(context);
    }
  }


  public void configJob(Job job, String input, String output) throws Exception {
    job.setJarByClass(TestCompanyLabelToDgraph.class);
    job.setJobName("TestCompanyLabelToDgraph -" + input.substring(input.lastIndexOf("/") + 1));
    job.setMapperClass(TestCompanyLabelToDgraph.Map.class);
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
      System.err.println("Usage: TestCompanyLabelToDgraph <Input> <ConfDir> <OutPut>");
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
      UserGroupInformation.loginUserFromKeytab("mindcube@WGQ.HIGGS.COM", confDir +
          "/krb5_mindcube.keytab");
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
    args = new String[]{"/user/mindcube/test_out/20180312/00",
        "/Users/devops/workspace/hbase-TestDemo/src/main/resources",
        "/user/mindcube/test_out/label/00",};
    int exitCode = new TestCompanyLabelToDgraph().run(args);
    System.exit(exitCode);
  }

}

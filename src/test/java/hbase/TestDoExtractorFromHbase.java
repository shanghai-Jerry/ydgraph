package hbase;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.mapreduce.TableMapReduceUtil;
import org.apache.hadoop.hbase.mapreduce.TableMapper;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Counter;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.hadoop.util.Tool;

import java.io.IOException;
import java.util.HashMap;

import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

/**
 * Created by Jerry on 2017/5/3.
 * hbase 简历更新数据获取, 用于检测最新变化的简历。
 * 如果有重复的docId简历，需要使用重复docId去hbase中更新数据
 * 不可使用新的docId
 */
public class TestDoExtractorFromHbase extends Configured implements  Tool {

  public static Logger logger = LoggerFactory.getLogger(TestDoExtractorFromHbase.class);

  public static class MyMapper extends TableMapper<Text, Text> {

    private String columnF;
    private String qualify;
    private Counter number;
    private Counter effectiveNumber;
    private static java.util.Map<String, Integer> sourceMap = new HashMap<String, Integer>();

    @Override
    protected void setup(Context context) throws IOException, InterruptedException {
      super.setup(context);
      number = context.getCounter("map-update", "rowKeyCounter");
      columnF = context.getConfiguration().get("map_columnFamily", "");
      qualify = context.getConfiguration().get("map_quality", "");
      logger.info("columnFamily is:" + columnF + ", quality is:" + qualify);
      effectiveNumber = context.getCounter("table-get", "effectiveNumberCounter");
      sourceMap.put("yahui", 101);
      sourceMap.put("ifc", 102);
    }

    @Override
    protected void cleanup(Context context) throws IOException, InterruptedException {
      super.cleanup(context);
    }

    @Override
    protected void map(ImmutableBytesWritable row, Result result, Context
        context) throws IOException, InterruptedException {
      number.increment(1);
      String rowKey = Bytes.toString(row.get());
      if (!"".equals(columnF) && !"".equals(qualify) && result != null) {
        effectiveNumber.increment(1);
        String info = Bytes.toString(result.getValue(columnF.getBytes(), qualify.getBytes()));
        if (info != null) {
          JsonObject infoObject = new JsonObject(info);
          Object infoSource = infoObject.getValue("source", null);
          if (infoSource != null && sourceMap.containsKey(infoSource)) {
            infoObject.put("source", sourceMap.get(infoSource));
          }
          context.write(new Text(rowKey), new Text(infoObject.toString()));
        }
      }
    }
  }

  public void configJob(Job job, String mapTableName, String output, long min, long max)
      throws IOException, ClassNotFoundException, InterruptedException {
    job.setJarByClass(TestDoExtractorFromHbase.class);
    Path outPath = new Path(output);
    Path deletePath = new Path(output + "/data");
    FileSystem fs = FileSystem.get(job.getConfiguration());
    fs.delete(deletePath, true);
    FileOutputFormat.setOutputPath(job, outPath);
    job.setJobName("TestDoExtractorFromHbase-" + output.substring(output.lastIndexOf("/")));
    job.setMapOutputKeyClass(Text.class);
    job.setMapOutputValueClass(Text.class);
    Scan scan = new Scan();
    scan.setCaching(500);        // 1 is the default in Scan, which will be bad for MapReduce jobs
    scan.setCacheBlocks(false);  // don't set to true for MR jobs
    scan.setTimeRange(min, max);
    TableMapReduceUtil.initTableMapperJob(mapTableName, scan, MyMapper.class,
        Text.class, Text.class, job);
    job.waitForCompletion(true);
  }

  @SuppressWarnings("RegexpSinglelineJava")
  public int run(String[] args) throws Exception {
    // TODO Auto-generated method stub
    if (args.length < 7) {
      System.err.println("Usage: TestDoExtractorFromHbase <mapTableName>" +
          "<map_columnFamily> <map_quality> <Output> <MinTime> <MaxTime> <ConfDir>");
      System.exit(1);
    }
    // 2018.4.8
    Configuration conf = new Configuration();
    String mapTableName = args[0];
    String mapColumnFamily = args[1];
    String mapQualify = args[2];
    String output = args[3];
    String minTs= args[4];
    String maxTs = args[5];
    String confDir = args[6];
    // System.setProperty("java.security.krb5.conf", confDir + "/krb5.conf");
    logger.info("tableName is:" + mapTableName + ", Output is:" + output);
    conf.addResource(confDir + "/core-site.xml");
    conf.addResource(confDir + "/hdfs-site.xml");
    conf.addResource(confDir + "/hbase-site.xml");
    conf.addResource(confDir + "/yarn-site.xml");
    conf.set("mapreduce.reduce.shuffle.memory.limit.percent", "0.25");
    conf.set("map_columnFamily", mapColumnFamily);
    conf.set("map_quality", mapQualify);
    UserGroupInformation.setConfiguration(conf);
    /*try {
      UserGroupInformation.loginUserFromKeytab("idmg@WGQ.HIGGS.COM",
          confDir + "/krb5.keytab");
    } catch (IOException e) {
      logger.info("key tab error:" + e.getMessage());
    }*/
    Job job = Job.getInstance(conf);
    configJob(job, mapTableName, output, Long.parseLong(minTs), Long.parseLong(maxTs));
    return 0;
  }

  @SuppressWarnings("RegexpSinglelineJava")
  public static void main(String[] args) throws Exception {

   args = new String[] {
        "idmg:resume_test",
        "data",
        "json",
        "resume/resume_update/test_04_11",
        "1523376000910",
        "1523462399915",
        "/Users/devops/workspace/hbase-demo/src/main/resources"
    };

    int exitCode = new TestDoExtractorFromHbase().run(args);
    System.exit(exitCode);
  }
}

package hbase;


import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.mapreduce.TableMapReduceUtil;
import org.apache.hadoop.hbase.mapreduce.TableReducer;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.mapreduce.Counter;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.hadoop.util.Tool;

import java.io.IOException;

import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

/**
 * Created by Jerry on 2017/4/12. 简历解析结果：originResumeContent 存入hbase 输入文件格式：（docId \t
 * resume_extractor_json）来源 去重后 无输出文件格式
 */
public class ResumeOriginContentMapred extends Configured implements Tool {

  private static Logger logger = LoggerFactory.getLogger(ResumeOriginContentMapred.class);

  public static class Map extends Mapper<LongWritable, Text, NullWritable, Put> {
    private Counter skipperCounter;
    private Counter noFiledCounter;
    private Counter originSuccessCounter;
    private Counter jsonSuccessCounter;

    @Override
    protected void setup(Context context) throws IOException, InterruptedException {
      super.setup(context);
      skipperCounter = context.getCounter("runner", "skipperCounter");
      noFiledCounter = context.getCounter("runner", "noFiledCounter");
      originSuccessCounter = context.getCounter("runner", "originSuccessCounter");
      jsonSuccessCounter = context.getCounter("runner", "jsonSuccessCounter");
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
            String originResumeContent = infoObject.getString("originResumeContent", "");
            if (!"".equals(originResumeContent)) {
              Put put = new Put(Bytes.toBytes(rowKey));
              put.addColumn(Bytes.toBytes("data"), Bytes.toBytes("originResumeContent"), Bytes
                  .toBytes(originResumeContent));
              context.write(NullWritable.get(), put);
            }
            infoObject.remove("originResumeContent");
            Put put = new Put(Bytes.toBytes(rowKey));
            put.addColumn(Bytes.toBytes("data"), Bytes.toBytes("json"), Bytes.toBytes(infoObject
                .toString()));
            context.write(NullWritable.get(), put);
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

  public static class reducer extends TableReducer<Writable, Put, Writable> {
    @Override
    protected void reduce(Writable key, Iterable<Put> values, Context context) throws
        IOException, InterruptedException {
      int count = 0;
      while (values.iterator().hasNext()) {
        Put put = values.iterator().next();
        context.write(key, put);
      }
    }

    @Override
    public void run(Context context) throws IOException, InterruptedException {
      super.run(context);
    }
  }

  public void configJob(Job job, String input, String tableName) throws Exception {
    job.setJarByClass(ResumeOriginContentMapred.class);
    job.setJobName("ResumeOriginContentMapred_auth-" + input.substring(input.lastIndexOf("/") + 1));
    job.setMapperClass(ResumeOriginContentMapred.Map.class);
    FileInputFormat.setInputPaths(job, input);
    job.setMapOutputKeyClass(NullWritable.class);
    job.setMapOutputValueClass(Put.class);
    TableMapReduceUtil.initTableReducerJob(tableName, ResumeOriginContentMapred.reducer.class, job);

  }

  @SuppressWarnings("RegexpSinglelineJava")
  public int run(String[] args) throws Exception {
    if (args.length < 3) {
      System.err.println("Usage: ResumeOriginContentMapred <Input> <ConfDir> <tableName>");
      System.exit(1);
    }
    String input = args[0];
    String confDir = args[1];
    String tableName = args[2];
    Configuration conf = new Configuration();
    conf.addResource(confDir + "/core-site.xml");
    conf.addResource(confDir + "/hdfs-site.xml");
    conf.addResource(confDir + "/hbase-site.xml");
    conf.addResource(confDir + "/yarn-site.xml");
    conf.set("mapreduce.reduce.shuffle.memory.limit.percent", "0.25");
    UserGroupInformation.setConfiguration(conf);
    Job job = Job.getInstance(conf);
    configJob(job, input, tableName);
    job.waitForCompletion(true);
    return 0;
  }

  @SuppressWarnings("RegexpSinglelineJava")
  public static void main(String[] args) throws Exception {
    int exitCode = new ResumeOriginContentMapred().run(args);
    System.exit(exitCode);
  }
}
package com.higgs.hadoop;

import com.higgs.client.EntityIdClient;
import com.higgs.dgraph.DClient;
import com.higgs.grakn.CompanyEntity;
import com.higgs.grakn.Schema;

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

import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;


/**
 * User: JerryYou
 *
 * Date: 2019-08-22
 *
 * Copyright (c) 2018 devops
 *
 * <<licensetext>>
 */
public class CompanyEntity4GraknMapred extends Configured implements Tool {
  /**
   * Created by Jerry on 2018/8/22. 输入文件格式：（docId \t json)
   * 输出满足grakn格式的gql文件数据
   * 通过console导入文件数据到grakn中
   */
    private static Logger logger = LoggerFactory.getLogger(CompanyEntity4GraknMapred.class);

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
        logger.info("Server:" + dgraphServer + " , server:" + entityServer + ", batch:" + setBatch);
      }

      @Override
      protected void cleanup(Context context) throws IOException, InterruptedException {
        super.cleanup(context);
      }

      @Override
      public void run(Context context) throws IOException, InterruptedException {
        setup(context);
        int batch = 0;
        while (context.nextKeyValue()) {
          JsonObject infoObject = new JsonObject();
          String info = context.getCurrentValue().toString().trim();
          int index = info.indexOf("\t");
          String json = info.substring(0, index + "\t".length());
          try {
            infoObject = new JsonObject(json);
          } catch (Exception e) {
            errorCounter.increment(1);
            e.printStackTrace();
          }
          context.write(new Text(infoObject.getString("normedName", "NoDef")), new Text("1"));
          jsonSuccessCounter.increment(1L);
        }
        cleanup(context);
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
      }

      @Override
      protected void cleanup(Context context) throws IOException, InterruptedException {
        super.cleanup(context);
      }

      @Override
      public void run(Context context) throws IOException, InterruptedException {
        setup(context);
        int batch = 0;
        while (context.nextKey()) {
          String key = context.getCurrentKey().toString();
          // don't care about value
          if ("NoDef".equals(key)) {
            errorCounter.increment(1L);
            continue;
          }
          CompanyEntity companyEntity = new CompanyEntity(key);
          companyEntity.isA(Schema.Entity.ENTITY_TYPE.getName())
              .has(Schema.Attribute.ENTITY_TYPE
              .getName(), new String[]{Schema.Entity.COMPANY.getName(),Schema.Entity.DIRECTION.getName
                  ()})
              .has(Schema.Attribute.NAME.getName(), new String[]{key});
          context.write(new Text(companyEntity.toString()), new Text(""));
          jsonSuccessCounter.increment(1L);
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
      job.setJarByClass(CompanyEntity4GraknMapred.class);
      job.setJobName("CompanyEntity4GraknMapred -" + input.substring(input.lastIndexOf("/") +
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
      job.setNumReduceTasks(10);
    }

    @SuppressWarnings("RegexpSinglelineJava")
    public int run(String[] args) throws Exception {
      if (args.length < 3) {
        System.err.println("Usage: CompanyEntity4GraknMapred <Input> <ConfDir> <OutPut>");
        System.exit(1);
      }
      String input = args[0];
      String confDir = args[1];
      String output = args[2];
      Configuration conf = new Configuration();
      conf.addResource(confDir + "/hadoop-config/core-site.xml");
      conf.addResource(confDir + "/hadoop-config/hdfs-site.xml");
      conf.addResource(confDir + "/hadoop-config/hbase-site.xml");
      conf.addResource(confDir + "/hadoop-config/yarn-site.xml");
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
      int exitCode = new CompanyEntity4GraknMapred().run(args);
      System.exit(exitCode);
    }
}

package com.higgs.hbase;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.mapreduce.HFileOutputFormat2;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Counter;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.Tool;

import java.io.IOException;
import java.util.HashMap;

import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

/**
 * User: JerryYou
 *
 * Date: 2018-11-27
 *
 * Copyright (c) 2018 devops
 *
 * 通过BulkLoad快速将海量数据导入到Hbase
 *
 * 在第一次建立Hbase表的时候，我们可能需要往里面一次性导入大量的初始化数据。我们很自然地想到将数据一条条插入到Hbase中，
 *  或者通过MR方式等。但是这些方式不是慢就是在导入的过程的占用Region资源导致效率低下，所以很不适合一次性导入大量数据。
 *  针对这个问题介绍如何通过Hbase的BulkLoad
 *  方法来快速将海量数据导入到Hbase中
 *
 *  Bulk Load 的实现原理是通过一个 MapReduce Job 来实现的，通过 Job 直接生成一个 HBase 的内部 HFile 格式文件，
 *  用来形成一个特殊的 HBase 数据表，然后直接将数据文件加载到运行的集群中。
 *  与使用HBase API相比，使用Bulkload导入数据占用更少的CPU和网络资源
 *
 *  1. MapReduce作业需要使用HFileOutputFormat2来生成HBase数据文件。
 *  为了有效的导入数据，需要配置HFileOutputFormat2使得每一个输出文件都在一个合适的区域中。
 *  为了达到这个目的，MapReduce作业会使用Hadoop的TotalOrderPartitioner类根据表的key值将输出分割开来。
 *  HFileOutputFormat2的方法configureIncrementalLoad()会自动的完成上面的工作。
 *
 *  2. 告诉RegionServers数据的位置并导入数据。这一步是最简单的，
 *  通常需要使用LoadIncrementalHFiles(更为人所熟知是completebulkload工具)，
 *  将文件在HDFS上的位置传递给它，它就会利用RegionServer将数据导入到相应的区域
 *
 *  注： 得到输出后，调用：LoadIncrementalHFileToHBase, 将输出的HFile数据导入到Hbase中
 *
 * <<licensetext>>
 */
public class BulkLoadImportHFile extends Configured implements Tool {

  public static Logger logger = LoggerFactory.getLogger(DoExtractorFromHbase.class);

  public static class BulkloadMapper extends Mapper <LongWritable, Text, ImmutableBytesWritable,
      Put> {

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
    }

    @Override
    protected void cleanup(Context context) throws IOException, InterruptedException {
      super.cleanup(context);
    }

    @Override
    protected void map(LongWritable key, Text value, Context context) throws
        IOException, InterruptedException {
      number.increment(1);
      String line = value.toString();
      String[] items = line.split("\t");
      if (items.length == 2) {
        String row = items[0];
        String text = items[1];
        if (!"".equals(columnF) && !"".equals(qualify)) {
          effectiveNumber.increment(1);
          ImmutableBytesWritable rowKey = new ImmutableBytesWritable(row.getBytes());
          Put put = new Put(row.getBytes());
          put.addColumn(columnF.getBytes(), qualify.getBytes(), text.getBytes());
          context.write(rowKey, put);
        }
      }
    }
  }

  public void configJob(Job job, String input, String output, String tableName) throws
      IOException, ClassNotFoundException, InterruptedException {
    // String input = "hdfs://xxxx/input";
    // String output = "hdfs://xxx/output";
    Path deletePath = new Path(output);
    FileSystem fs = FileSystem.get(job.getConfiguration());
    fs.delete(deletePath, true);

    job.setJarByClass(BulkLoadImportHFile.class);
    job.setMapperClass(BulkloadMapper.class);
    job.setJobName("BulkLoadImportHFile-" + output.substring(output.lastIndexOf("/")));
    // map out set
    job.setMapOutputKeyClass(ImmutableBytesWritable.class);
    job.setMapOutputValueClass(Put.class);
    // set output format
    job.setOutputFormatClass(HFileOutputFormat2.class);

    Connection connection = ConnectionFactory.createConnection(job.getConfiguration());
    // HTable table = new HTable(job.getConfiguration(), tableName);
    Table table = connection.getTable(TableName.valueOf(tableName));
    HFileOutputFormat2.configureIncrementalLoad(job, table, connection.getRegionLocator(TableName
        .valueOf(tableName)));
    FileInputFormat.setInputPaths(job, input);
    FileOutputFormat.setOutputPath(job, new Path(output));
    job.waitForCompletion(true);
  }

  @SuppressWarnings("RegexpSinglelineJava")
  public int run(String[] args) throws Exception {
    // TODO Auto-generated method stub
    if (args.length < 7) {
      System.err.println("Usage: DoExtractorFromHbase <mapTableName>" + "<map_columnFamily> " +
          "<map_quality> <InPut> <Output> ");
      System.exit(1);
    }
    Configuration conf = new Configuration();
    String mapTableName = args[0];
    String mapColumnFamily = args[1];
    String mapQualify = args[2];
    String output = args[3];
    String input = args[4];
    logger.info("tableName is:" + mapTableName + ", Output is:" + output);
    conf.set("mapreduce.reduce.shuffle.memory.limit.percent", "0.25");
    conf.set("map_columnFamily", mapColumnFamily);
    conf.set("map_quality", mapQualify);
    Job job = Job.getInstance(conf);
    configJob(job, input, output, mapTableName);
    return 0;
  }

  @SuppressWarnings("RegexpSinglelineJava")
  public static void main(String[] args) throws Exception {
    int exitCode = new BulkLoadImportHFile().run(args);
    System.exit(exitCode);
  }
}

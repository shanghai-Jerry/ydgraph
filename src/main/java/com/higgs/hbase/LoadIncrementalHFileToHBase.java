package com.higgs.hbase;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.mapreduce.LoadIncrementalHFiles;

/**
 * User: JerryYou
 *
 * Date: 2018-11-27
 *
 * Copyright (c) 2018 devops
 *
 *  将 BulkLoadImportHFile 生成的HFile output 当作输入， 导入数据到hbase
 *
 * <<licensetext>>
 */
public class LoadIncrementalHFileToHBase {

  public static void main(String[] args) throws Exception {
    if (args.length < 2) {
      System.exit(-1);
    }
    String input = args[0];
    String tableName = args[1];
    Configuration configuration = HBaseConfiguration.create();
    Connection connection = ConnectionFactory.createConnection(configuration);
    LoadIncrementalHFiles loader = new LoadIncrementalHFiles(configuration);
    loader.doBulkLoad(new Path(input), connection.getAdmin(), connection.getTable(TableName
        .valueOf(tableName)), connection.getRegionLocator(TableName.valueOf(tableName)));
    // loader.doBulkLoad(new Path(input), new HTable(configuration, tableName));
  }
}

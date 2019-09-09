package com.higgs.cassandra;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.ColumnDefinitions;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.policies.FallthroughRetryPolicy;
import com.higgs.utils.FileUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.core.json.JsonObject;

/**
 * User: JerryYou
 *
 * Date: 2019-09-06
 *
 * Copyright (c) 2018 devops
 *
 * <<licensetext>>
 */
public class SearchLogExport {

  private Cluster cluster;
  private Session session;
  private String table;
  static Logger logger = LoggerFactory.getLogger(SearchLogExport.class);

  public SearchLogExport(String name,String pass, int  port,String keySpace, String table,
                         String... address) {
    cluster = Cluster.builder().addContactPoints(address)
        .withCredentials(name, pass)
        .withPort(port)
        .withRetryPolicy(FallthroughRetryPolicy.INSTANCE)
        .build();
    session = cluster.connect(keySpace);
    this.table = table;
  }

  public void query(QueryFilter queryFilter, String outPutPath) {
    if (!queryFilter.isVailad()) {
      logger.info("query is invalid ..");
      return;
    }
    String sql = "select * from " + this.table + " where  channel = '"+queryFilter.getChannel()
        +"' and name = '"+queryFilter.getName()+"' and receive_at <='" + queryFilter.getReceiveLessThen()+"' and " +
        "receive_at >='"
        + queryFilter.getReceiveGreatThen() + "' ";
    if (queryFilter.getUid() != 0) {
      sql += " and uid = " + queryFilter.getUid();
    }
    ResultSet resultSet = session.execute(sql);
    ColumnDefinitions columnDefinitions = resultSet.getColumnDefinitions();
    int size = resultSet.all().size();
    logger.info(queryFilter.toString() + ", get data size:" + size);
    List<String> out = new ArrayList<>();
    int count = 0;
    int finished = 0;
    for (Row row : resultSet) {
      JsonObject resultJson = new JsonObject();
      for (ColumnDefinitions.Definition definition : columnDefinitions) {
        if (definition.getName() == "uid") {
          resultJson.put(definition.getName(), row.getInt(definition.getName()));
        } else {
          resultJson.put(definition.getName(), row.getString(definition.getName()));
        }
      }
      count++;
      finished++;
      if (count % queryFilter.getBatchSize() == 0) {
        FileUtils.saveFiles(outPutPath, out, true);
        logger.info("batch saved :"+ finished + "/" + size);
        count = 0;
        out.clear();
      } else {
        out.add(resultJson.encode());
      }
    }
    if (count > 0) {
      FileUtils.saveFiles(outPutPath, out, true);
    }
    logger.info("total saved :"+ finished + "/" + size);
  }

  public static void main(String[] args) {

    if (args.length < 1) {
      System.err.println("Usage: SearchLogExport <Config>");
      System.exit(-1);
    }
    String configStr = null;
    try {
      configStr = new String(Files.readAllBytes(Paths.get(args[0])));
    } catch (IOException e) {
      logger.info("config file not exists!");
      e.printStackTrace();
      System.exit(-1);
    }
    JsonObject config = new JsonObject(configStr);
    JsonObject cassandra = config.getJsonObject("cassandra", new JsonObject());
    JsonObject filter = config.getJsonObject("filter", new JsonObject());
    JsonObject outPut = config.getJsonObject("out", new JsonObject());

    String cassandraServer = cassandra.getString("servers", "127.0.0.1");
    int cassandraPort = cassandra.getInteger("port", 9042);
    String[] cassandraServers = cassandraServer.split(",");
    String username = cassandra.getString("username", "cassandra");
    String pass = cassandra.getString("password", "cassandra");
    String keySpace = cassandra.getString("keyspace", "bi");
    String table = cassandra.getString("table", "access_log");
    String output = outPut.getString("outpath", ".");
    String fileName = outPut.getString("file", "search_log.txt");
    String channel = filter.getString("channel", "consultant");
    String name = filter.getString("name", "PROJECT_PUBLIC_SEARCH");
    String receiveAtDown = filter.getString("receive_at_gt","");
    String receiveAtUp = filter.getString("receive_at_lt","");
    int uid = filter.getInteger("uid", 0);
    int batch = filter.getInteger("batch", 1000);
    QueryFilter queryFilter = new QueryFilter();
    queryFilter
        .setChannel(channel)
        .setName(name)
        .setUid(uid)
        .setReceiveGreatThen(receiveAtDown)
        .setReceiveLessThen(receiveAtUp).setBatchSize(batch);
    ;

    SearchLogExport searchLogExport = new SearchLogExport(username, pass, cassandraPort
        ,keySpace,table,cassandraServers);

    String outPutPath = output.endsWith("/") ? output + fileName : output + "/" + fileName;

    searchLogExport.query(queryFilter, outPutPath);


  }
}

package com.bigchange.mysql;

import org.apache.commons.pool2.impl.GenericObjectPool;

import java.sql.Connection;

import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

/**
 * User: JerryYou
 *
 * Date: 2019-05-29
 *
 * Copyright (c) 2018 devops
 *
 * <<licensetext>>
 */
class MySQLConnection {

  private static Logger  logger = LoggerFactory.getLogger(MySQLConnection.class);

  private String host;
  private int port;
  private String database;
  private String table;
  private String username;
  private String password;
  private GenericObjectPool connectionPool;
  private String url;

  public MySQLConnection(String host, int port, String database, String table, String username,
                         String password) {
    this.host = host;
    this.port = port;
    this.database = database;
    this.table = table;
    this.username = username;
    this.password = password;
    this.url = "jdbc:mysql://" + host + ":" + port + "/" + database + "?user=" + username +
        "&password=" + password;

    connectionPool = new GenericObjectPool(new ConnectionFactory(this
        .url));
  }

  @Override
  public String toString() {
    return "jdbc:mysql://" + host + ":" + port + "/" + database + "?user=" + username +
        "&password=" + password;
  }


  public Connection getConnection() {
    try {
      return  (Connection) connectionPool.borrowObject();
    } catch (Exception e) {
      logger.info("get connection error!!");
    }
    return null;
  }

  public void returnConnection(Connection connection) {
    connectionPool.returnObject(connection);
  }

}
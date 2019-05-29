package com.bigchange.mysql;

import org.apache.commons.pool2.impl.GenericObjectPool;

import java.sql.Connection;

/**
 * User: JerryYou
 *
 * Date: 2019-05-29
 *
 * Copyright (c) 2018 devops
 *
 * <<licensetext>>
 */
public class ConnectionPool {

    String jdbc;
    GenericObjectPool connectionPool;

    public ConnectionPool(String jdbc) {
      this.jdbc = jdbc;
      GenericObjectPool connectionPool = new GenericObjectPool(new ConnectionFactory(this
          .jdbc));
    }
    public Connection getConnection() throws Exception {
      return (Connection) connectionPool.borrowObject();
    }

    public void returnConnection(Connection connection) {
      connectionPool.returnObject(connection);
    }
}

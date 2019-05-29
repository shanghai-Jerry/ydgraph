package com.bigchange.mysql;

import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * User: JerryYou
 *
 * Date: 2019-05-29
 *
 * Copyright (c) 2018 devops
 *
 * <<licensetext>>
 */
class ConnectionFactory extends BasePooledObjectFactory<Connection> {
  String jdbc;

  public ConnectionFactory(String jdbc) {
    this.jdbc = jdbc;
  }

  @Override
  public Connection create() throws SQLException {
    return DriverManager.getConnection(jdbc);
  }
  @Override
  public PooledObject wrap(Connection connection) {
    return new DefaultPooledObject(connection);
  }
}
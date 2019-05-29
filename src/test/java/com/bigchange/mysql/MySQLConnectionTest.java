package com.bigchange.mysql; 

import org.junit.Test; 
import org.junit.Before; 
import org.junit.After;

import java.sql.Connection;

/** 
* MySQLConnection Tester. 
* 
* @author <Authors name> 
* @since <pre>May 29, 2019</pre> 
* @version 1.0 
*/ 
public class MySQLConnectionTest { 

  MySQLConnection mySQLConnection;
  Connection connection;
@Before
public void before() throws Exception {
    mySQLConnection = new MySQLConnection("", 3306, "", "", "","");
    // connectionPool = new ConnectionPool(mySQLConnection.toString()).getConnection();
    connection = mySQLConnection.getConnection();
} 

@After
public void after() throws Exception { 
} 

/** 
* 
* Method: toString() 
* 
*/ 
@Test
public void testToString() throws Exception { 
//TODO: Test goes here... 
} 


} 

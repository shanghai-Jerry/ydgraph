package com.higgs.dgraph;

import com.higgs.utils.Util;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import io.dgraph.DgraphProto;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

import static com.higgs.utils.Util.parseLatency;
import static org.junit.Assert.*;

/**
 * User: JerryYou
 *
 * Date: 2018-07-11
 *
 * Copyright (c) 2018 devops
 *
 * <<licensetext>>
 */
public class DemoTest {

  private DClient dClient = new DClient(Config.addressList);
  private Demo demo = new Demo(dClient);

  private static final Logger logger = LoggerFactory.getLogger(DemoTest.class);

  @Before
  public void setUp() throws Exception {
  }

  @After
  public void tearDown() throws Exception {
  }

  @Test
  public void queryTest() {
    String query = "";
    try {
      query = new String(Files.readAllBytes(Paths.get
          ("src/main/resources/query_test/query_test.query")));

    } catch (IOException e) {
      e.printStackTrace();
    }
    query = String.format(query);
    DgraphProto.Response res = dClient.getDgraphClient()
        .newTransaction()
        .query(query);
    // queryWithVars seems not a good choice
    // 获取时间
    // res.getLatency()
    Util.formatPrintJson(res.getJson().toStringUtf8());
    parseLatency(res);
    // JsonObject jsonObject = new JsonObject(res.getJson().toStringUtf8());
    //String name = jsonObject.getJsonArray("query", new JsonArray()).getJsonObject(0).getString("name");
    // logger.info("name:" + name);
  }
  @Test
  public void test () {
    logger.info("res:" + 20* 1.0/ (20 * 2));
  }

  @Test
  public void queryDemo() {
    // Query
    String query = "";
    try {
      query = new String(Files.readAllBytes(Paths.get
          ("src/main/resources/query_node/test.query")));

    } catch (IOException e) {
      e.printStackTrace();
    }
    System.out.println("querying ....\n" + query);
    Map<String, String> vars = new HashMap<>();
    vars.put("$a", "0x23d419");
    vars.put("$b", "@filter(uid(0x758e7a))");
    String queryFormat = String.format(query, "0x23d419", "dff");

    DgraphProto.Response res = dClient.getDgraphClient().newTransaction()
        .query(queryFormat)
        //.queryWithVars(query, vars)
    ;
    logger.info("query:" + query);
    // 获取时间
    // res.getLatency()
    Util.formatPrintJson(res.getJson().toStringUtf8());
    parseLatency(res);
  }
}
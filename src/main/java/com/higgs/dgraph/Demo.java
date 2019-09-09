package com.higgs.dgraph;


import com.higgs.client.dgrpah.DgraphClient;
import com.higgs.dgraph.node.EntityNode;
import com.higgs.dgraph.node.Label;
import com.higgs.dgraph.node.NodeUtil;
import com.higgs.dgraph.node.Person;
import com.higgs.utils.FileUtils;
import com.higgs.utils.TimeUtil;
import com.higgs.utils.Util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.dgraph.bigchange.DgraphProto;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;


/**
 * User: JerryYou
 *
 * Date: 2018-05-08
 *
 * Copyright (c) 2018 devops
 *
 * <<licensetext>>
 */

public class Demo {

  // produ client ip : 172.20.0.8
  // test client ip : 172.20.0.68
  private static final Logger logger = LoggerFactory.getLogger(Demo.class);

  private static DClient dClient = new DClient(Config.addressList);

  public Demo() {
    dClient = new DClient(Config.TEST_VM_HOSTNAME);
  }

  public Demo(DClient dClient) {
    this.dClient = dClient;
  }

  public List<Person> CheckOutEntities(List<Person> persons) {
    for (Person person : persons) {
      String uid = dClient.QueryById(person.getId(), "hadoop.com.higgs.dgraph.node.People", "getExistUid");
      if (!"".equals(uid)) {
        person.setUid(uid);
      }
    }
    return persons;
  }

  public List<Person> searchUid(List<Person> persons) {
    for (Person person : persons) {
      String uid = dClient.QueryById(person.getId(), "hadoop.com.higgs.dgraph.node.People", "getExistUid");
      if (!"".equals(uid)) {
        person.setUid(uid);
      }
    }
    return persons;
  }

  public void parseLatency(DgraphProto.Response res) {
    long processTime = res.getLatency().getProcessingNs();
    Util.println("latency:", res.getLatency().toString());
    logger.info("consume:" + TimeUtil.consumeTime(processTime / 1000/ 1000));
  }
  public void QueryCount() throws IOException {
    System.out.println("querying ....");
    String query = new String(Files.
        readAllBytes(Paths.get("src/main/resources/query/count_company.query")));
    DgraphProto.Response res = dClient.getDgraphClient().newTransaction().query(query);
    // 获取时间
    System.out.println(res.getJson().toStringUtf8());
  }

  public DgraphProto.Assigned feedEntities(String entities) {
    DgraphProto.Assigned assignedList = dClient.multiplyEdgesMutation(entities);
    return assignedList;
  }


  // pass test
  public void edgeConnect() {
    String edgeConnect = "<0x118c> <friend> \"schoolType\" .";
    feedEntities(edgeConnect);
  }

  public void dropSchema() {
    dClient.dropSchema();
  }

  public void init() {
    long value = Long.parseLong("0x1780e".substring(2), 16);
    String hexValue = Long.toHexString(98951);
    System.out.println(value + ", 0x" + hexValue);
    dClient.dropSchema();
    dClient.alterSchema(Config.schema);
    initLeaseLabel();
    initCompanyLabel();
    initSchoolLabel();
    initMajorLabel();
    initIndustryLabel();
  }

  public void alterSchema() {
    logger.info("alter schema ... ");
    dClient.alterSchema(Config.updateSchema);
  }

  public void alterUpsertScheam() {
    dClient.alterSchema(Config.checkSchema);
  }


  public void initIndustryLabel() {
    Label label = new Label();
    label.setLabel_name("行业类型");
    label.setUnique_ids(Arrays.asList("行业类型"));
    // 0x118e
    label.setUid("0x04");
    Map<String,  List<String>> uid = NodeUtil.insertEntity(dClient , Arrays.asList(label));
    FileUtils.saveFile("src/main/resources/industry_label_uid_map.txt", uid);
  }

  public void initMajorLabel() {
    Label label = new Label();
    label.setLabel_name("专业类型");
    label.setUnique_ids(Arrays.asList("专业类型"));
    // 0x118d
    label.setUid("0x03");
    Map<String,  List<String>> uid = NodeUtil.insertEntity(dClient , Arrays.asList(label));
    FileUtils.saveFile("src/main/resources/major_label_uid_map.txt", uid);
  }

  public void initSchoolLabel() {
    Label label = new Label();
    label.setLabel_name("学校类型");
    label.setUnique_ids(Arrays.asList("学校类型"));
    // 0x118c
    label.setUid("0x02");
    Map<String,  List<String>> uid = NodeUtil.insertEntity(dClient, Arrays.asList(label));
    FileUtils.saveFile("src/main/resources/school_abel_uid_map.txt", uid);
  }

  public void initCompanyLabel() {
    Label label = new Label();
    label.setLabel_name("公司类型");
    label.setUnique_ids(Arrays.asList("公司类型"));
    // "公司类型": "0x118b"
    label.setUid("0x01");
    Map<String,  List<String>> uid = NodeUtil.insertEntity(dClient, Arrays.asList(label));
    FileUtils.saveFile("src/main/resources/company_label_uid_map.txt", uid);
  }

  public void deleteEdge() {
    DgraphClient.Transaction txn = dClient.getDgraphClient().newTransaction();
    txn.mutate(DgraphClient.deleteEdges(DgraphProto.Mutation.newBuilder().setCommitNow(true).build()
        , "0x118e", "label_name"));

  }

  public void initDegreeUid() {
    EntityNode entityNode = new EntityNode();
    String name = "高中";
    entityNode.setUnique_id(name);
    entityNode.setName(name);
    EntityNode entityNode1 = new EntityNode();
    name = "本科";
    entityNode1.setUnique_id(name);
    entityNode1.setName(name);
    name = "硕士";
    EntityNode entityNode2 = new EntityNode();
    entityNode2.setUnique_id(name);
    entityNode2.setName(name);
    Map<String, List<String>> uid = NodeUtil.insertEntity(dClient, Arrays.asList(entityNode,
        entityNode1, entityNode2));
    FileUtils.saveFile("src/main/resources/degree_uid_map.txt", uid);

  }

  public void initLeaseLabel() {
    Label labellease = new Label();
    labellease.setLabel_name("lease类型");
    labellease.setUnique_ids(Arrays.asList("lease类型"));
    labellease.setUid("0x00");
    Map<String, List<String>> uid = NodeUtil.insertEntity(dClient, Arrays.asList(labellease));
    FileUtils.saveFile("src/main/resources/lease_label_uid_map.txt", uid);
  }


  @Deprecated
  public void QueryTest() {
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
  @Deprecated
  public void QueryDemo() {
    // Query
    String query = "";
    try {
      query = new String(Files.readAllBytes(Paths.get
          ("src/main/resources/query/test.query")));

    } catch (IOException e) {
      e.printStackTrace();
    }
    System.out.println("querying ....\n" + query);
    Map<String, String> vars = new HashMap<>();
    vars.put("$a", "0x998a17");
    vars.put("$b", "false");
    String queryFormat = String.format(query);
    DgraphProto.Response res = dClient.getDgraphClient().newTransaction()
        //.query(queryFormat)
        .queryWithVars(query, vars)
        ;
    // 获取时间
    // res.getLatency()
    Util.formatPrintJson(res.getJson().toStringUtf8());
    parseLatency(res);
  }

  public static void main(String[] arg) {
    DClient dClient = new DClient(Config.TEST_HOSTNAME);
    Demo demo = new Demo(dClient);
    // demo.dropSchema();
    demo.QueryTest();
    // demo.QueryDemo();
    // demo.init();
    // demo.deleteEdge();
    // demo.edgeConnect();
    // demo.alterSchema();
    // demo.alterUpsertScheam();
    // demo.initDegreeUid();
    System.out.println("finished");
  }
}

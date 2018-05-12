package dgraph;


import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import dgraph.node.Label;
import dgraph.node.NodeUtil;
import dgraph.node.Person;
import io.dgraph.DgraphProto;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import utils.FileUtils;
import utils.util;

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
  private DClient dClient;

  public Demo() {
    dClient = new DClient(Config.TEST_VM_HOSTNAME);
  }

  public Demo(DClient dClient) {
    this.dClient = dClient;
  }

  public List<Person> CheckOutEntities(List<Person> persons) {
    for (Person person : persons) {
      String uid = dClient.QueryById(person.getId(), "hadoop.dgraph.node.People", "getExistUid");
      if (!"".equals(uid)) {
        person.setUid(uid);
      }
    }
    return persons;
  }

  public List<Person> searchUid(List<Person> persons) {
    for (Person person : persons) {
      String uid = dClient.QueryById(person.getId(), "hadoop.dgraph.node.People", "getExistUid");
      if (!"".equals(uid)) {
        person.setUid(uid);
      }
    }
    return persons;
  }

  public void QueryDemo() {

    // Query
    String query = "query all($a: string) {\n" + " count(func: uid($a)) {\n" + " ~has_label { " +
        "count(uid) } \n" + "  }\n" + "}";
    // System.out.println("Query => \n" + query);
    Map<String, String> vars = Collections.singletonMap("$a", "0x118b");
    DgraphProto.Response res = dClient.getDgraphClient().newTransaction().queryWithVars(query,
        vars);
    System.out.println("querying ....");
    // 获取时间
    // res.getLatency()
    System.out.println(res.getJson().toStringUtf8());
    util.println("latency:", res.getLatency().toString());
  }

  public DgraphProto.Assigned feedEntities(String entities) {
    DgraphProto.Assigned assignedList = dClient.mutiplyEdgesMutation(entities);
    return assignedList;
  }

  // pass test
  public void edgeConnect() {
    String edgeConnect = "<0x118c> <friend> \"schoolType\" .";
    feedEntities(edgeConnect);
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
    dClient.alterSchema(Config.updateSchema);
  }


  public void initIndustryLabel() {
    Label label = new Label();
    label.setLabel_name("行业类型");
    // 0x118e
    label.setUid("0x118e");
    Map<String, String> uid = NodeUtil.putEntity(dClient, null, Arrays.asList(label), "", 0);
    FileUtils.saveFile("src/main/resources/industry_label_uid_map.txt", uid);
  }

  public void initMajorLabel() {
    Label label = new Label();
    label.setLabel_name("专业类型");
    // 0x118d
    label.setUid("0x118d");
    Map<String, String> uid = NodeUtil.putEntity(dClient, null, Arrays.asList(label), "", 0);
    FileUtils.saveFile("src/main/resources/major_label_uid_map.txt", uid);
  }

  public void initSchoolLabel() {
    Label label = new Label();
    label.setLabel_name("学校类型");
    // 0x118c
    label.setUid("0x118c");
    Map<String, String> uid = NodeUtil.putEntity(dClient, null, Arrays.asList(label), "", 0);
    FileUtils.saveFile("src/main/resources/school_abel_uid_map.txt", uid);
  }

  public void initCompanyLabel() {
    Label label = new Label();
    label.setLabel_name("公司类型");
    // "公司类型": "0x118b"
    label.setUid("0x118b");
    Map<String, String> uid = NodeUtil.putEntity(dClient, null, Arrays.asList(label), "", 0);
    FileUtils.saveFile("src/main/resources/company_label_uid_map.txt", uid);
  }

  public void initLeaseLabel() {
    Label labellease = new Label();
    labellease.setLabel_name("lease类型");
    labellease.setUid("0x00");
    Map<String, String> uid = NodeUtil.putEntity(dClient, null, Arrays.asList(labellease), "", 0);
    FileUtils.saveFile("src/main/resources/lease_label_uid_map.txt", uid);
  }

  public static void main(String[] arg) {
    DClient dClient = new DClient(Config.TEST_HOSTNAME);
    Demo demo = new Demo(dClient);
    // demo.init();
    // demo.QueryDemo();
    // demo.edgeConnect();
    demo.alterSchema();
    System.out.println("finished");

  }
}

package dgraph;


import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import client.dgrpah.DgraphClient;
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
    System.out.println("querying ....");
    Map<String, String> vars = Collections.singletonMap("$a", "0x118b");
    DgraphProto.Response res = dClient.getDgraphClient().newTransaction().queryWithVars(query,
        vars);
    // 获取时间
    // res.getLatency()
    System.out.println(res.getJson().toStringUtf8());
    util.println("latency:", res.getLatency().toString());
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


  public void initIndustryLabel() {
    Label label = new Label();
    label.setLabel_name("行业类型");
    label.setUnique_ids(Arrays.asList("行业类型"));
    // 0x118e
    label.setUid("0x04");
    Map<String,  List<String>> uid = NodeUtil.putEntity(dClient , Arrays.asList(label));
    FileUtils.saveFile("src/main/resources/industry_label_uid_map.txt", uid);
  }

  public void initMajorLabel() {
    Label label = new Label();
    label.setLabel_name("专业类型");
    label.setUnique_ids(Arrays.asList("专业类型"));
    // 0x118d
    label.setUid("0x03");
    Map<String,  List<String>> uid = NodeUtil.putEntity(dClient , Arrays.asList(label));
    FileUtils.saveFile("src/main/resources/major_label_uid_map.txt", uid);
  }

  public void initSchoolLabel() {
    Label label = new Label();
    label.setLabel_name("学校类型");
    label.setUnique_ids(Arrays.asList("学校类型"));
    // 0x118c
    label.setUid("0x02");
    Map<String,  List<String>> uid = NodeUtil.putEntity(dClient, Arrays.asList(label));
    FileUtils.saveFile("src/main/resources/school_abel_uid_map.txt", uid);
  }

  public void initCompanyLabel() {
    Label label = new Label();
    label.setLabel_name("公司类型");
    label.setUnique_ids(Arrays.asList("公司类型"));
    // "公司类型": "0x118b"
    label.setUid("0x01");
    Map<String,  List<String>> uid = NodeUtil.putEntity(dClient, Arrays.asList(label));
    FileUtils.saveFile("src/main/resources/company_label_uid_map.txt", uid);
  }

  public void deleteEdge() {
    DgraphClient.Transaction txn = dClient.getDgraphClient().newTransaction();
    txn.mutate(DgraphClient.deleteEdges(DgraphProto.Mutation.newBuilder().setCommitNow(true).build()
        , "0x118e", "label_name"));

  }

  public void initLeaseLabel() {
    Label labellease = new Label();
    labellease.setLabel_name("lease类型");
    labellease.setUnique_ids(Arrays.asList("lease类型"));
    labellease.setUid("0x00");
    Map<String, List<String>> uid = NodeUtil.putEntity(dClient, Arrays.asList(labellease));
    FileUtils.saveFile("src/main/resources/lease_label_uid_map.txt", uid);
  }

  public static void main(String[] arg) {
    DClient dClient = new DClient(Config.TEST_HOSTNAME);
    Demo demo = new Demo(dClient);
    // demo.init();
    demo.deleteEdge();
    // demo.QueryDemo();
    // demo.edgeConnect();
    // demo.alterSchema();
    System.out.println("finished");
  }
}

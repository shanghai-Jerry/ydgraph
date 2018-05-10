package dgraph;


import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import client.dgrpah.DgraphClient;
import dgraph.node.Label;
import dgraph.node.NodeUtil;
import dgraph.node.People;
import dgraph.node.Person;
import io.dgraph.DgraphProto;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import utils.FileUtils;

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
    String query =
        "query all($a: string) {\n" + " count(func: eq(type, $a)) {\n" + " number:count(uid)\n" +
            "  }\n" + "}";
    System.out.println("Query => \n" + query);
    Map<String, String> vars = Collections.singletonMap("$a", "公司");
    DgraphProto.Response res = dClient.getDgraphClient().newTransaction().queryWithVars(query, vars);
    System.out.println("querying ....");
    System.out.println(res.getJson().toStringUtf8());
    System.out.println("finished");

  }

  public DgraphProto.Assigned feedEntities(String entities) {
    DgraphProto.Assigned assignedList = dClient.mutiplyEdgeMutation(entities);
    return assignedList;
  }

  // pass test
  public void edgeConnect() {
    String edgeConnect = "<0xd60> <friend> <0xd57> .";
    feedEntities(edgeConnect);
  }

  public void init() {
    long value = Long.parseLong("0x1780e".substring(2), 16);
    String hexValue = Long.toHexString(98951);
    System.out.println(value + ", 0x" + hexValue);
    dClient.dropSchema();
    dClient.alterSchema(Config.updateSchema);
  }

  public void initIndustryLabel() {
    Label label = new Label();
    label.setLabel_name("行业类型");
    // 0x118e
    label.setUid("0x118e");
    Map<String, String> uid = NodeUtil.putEntity(dClient, null, Arrays.asList(label), "", 0);
    FileUtils.saveFile("src/main/resources/industry_abel_uid_map.txt", uid);
  }

  public void initMajorLabel() {
    Label label = new Label();
    label.setLabel_name("专业类型");
    // 0x118d
    label.setUid("0x118d");
    Map<String, String> uid = NodeUtil.putEntity(dClient, null, Arrays.asList(label), "", 0);
    FileUtils.saveFile("src/main/resources/major_abel_uid_map.txt", uid);
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
    Label labellease = new Label();
    labellease.setLabel_name("lease类型");
    labellease.setUid("0x0");
    label.setLabel_name("公司类型");
    // "公司类型": "0x118b"
    label.setUid("0x118b");
    Map<String, String> uid = NodeUtil.putEntity(dClient, null, Arrays.asList(label, labellease), "", 0);
    FileUtils.saveFile("src/main/resources/company_label_uid_map.txt", uid);
  }

  public static  void main(String []arg) {
    DClient dClient = new DClient(Config.TEST_HOSTNAME);
    Demo demo = new Demo(dClient);
    // demo.QueryDemo();
    // demo.edgeConnect();
    demo.init();
    demo.initCompanyLabel();
    demo.initSchoolLabel();
    demo.initMajorLabel();
    demo.initIndustryLabel();
    System.out.println("finished");

  }
}

package dgraph;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import client.dgrpah.DgraphClient;
import dgraph.node.Person;
import io.dgraph.DgraphProto;
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
  private DClient dClient;

  public Demo() {
    dClient = new DClient(Config.TEST_HOSTNAME, Config.TEST_PORT);
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

  public DgraphProto.Assigned feedEntities(List<String> entities) {
    DgraphProto.Assigned assignedList;
    DgraphClient.Transaction txn = dClient.getDgraphClient().newTransaction();
    try {
      assignedList = dClient.mutiplyMutation(txn, entities);
      txn.commit();
    } finally {
      txn.discard();
    }
    return assignedList;
  }

  public void batchJsonPut() {
    this.dClient.alterSchema(Config.updateSchema);
    List<String>  personList = new ArrayList<String>();
    List<Person> persons = new ArrayList<Person>();
    // 先入实体
    Person person1 = new Person("P1-new", "youcj", 25);
    person1.setGender(1);
    Person person3 = new Person("P3","Gehy", 24);
    person3.setGender(2);
    Person person2 = new Person("P2", "youys", 1);
    Person person4 = new Person("P4", "daughter", 1);
    person4.setGender(2);
    person2.setGender(1);
    persons.add(person1);
    persons.add(person2);
    persons.add(person3);
    persons.add(person4);
    this.searchUid(persons);
    personList.add(person1.toString());
    personList.add(person2.toString());
    personList.add(person3.toString());
    personList.add(person4.toString());
    this.feedEntities(personList);
    // 后入实体之前的关系
    List<Person> pesonOnesfriends = new ArrayList<Person>();
    List<Person> pesonttwosfriends = new ArrayList<Person>();
    pesonOnesfriends.add(person3);
    pesonOnesfriends.add(person4);
    pesonOnesfriends.add(person2);
    pesonttwosfriends.add(person3);
    person2.setFriend(pesonttwosfriends);
    person1.setFriend(pesonOnesfriends);
    persons.clear();
    persons.add(person1);
    persons.add(person2);
    persons.add(person3);
    persons.add(person4);
    this.searchUid(persons);
    personList.clear();
    // 所有实体必须验证是否存在dgraph中，先判断uid是否有了
    personList.add(person1.toString());
    personList.add(person2.toString());
    personList.add(person3.toString());
    personList.add(person4.toString());
    logger.info("object obj:" + person1.toString());
    // 验证是否存在dgraph中，先判断uid是否有了
    this.searchUid(persons);
    DgraphProto.Assigned assigned = this.feedEntities(personList);
    Map<String, String> map = assigned.getUidsMap();
    Set<Map.Entry<String, String>> entrySet=  map.entrySet();
    Iterator<Map.Entry<String, String>> iterator = entrySet.iterator();
    while(iterator.hasNext()) {
      Map.Entry<String, String> entry = iterator.next();
      String key = entry.getKey();
      String value = entry.getValue();
      logger.info("Key:" + key + ", value:" + value);
    }
  }

  public void edgeConnect() {
    List<String> edgeConnect = new ArrayList<String>();
    edgeConnect.add("\"0xe5a5\" <friend> \"0xe5a6\" .");
    feedEntities(edgeConnect);
  }

  public static  void main(String []arg) {
    Demo demo = new Demo();
    long value = Long.parseLong("0x1780e".substring(2), 16);
    String hexValue = Long.toHexString(98951);
    System.out.println(value + ", 0x" + hexValue);
    demo.dClient.dropSchema();
    demo.dClient.alterSchema(Config.updateSchema);
    System.out.println("finished");
  }
}

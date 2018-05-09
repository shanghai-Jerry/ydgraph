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

  public DgraphProto.Assigned feedEntities(String entities) {
    DgraphProto.Assigned assignedList = dClient.mutiplyEdgeMutation(entities);
    return assignedList;
  }

  // pass test
  public void edgeConnect() {
    String edgeConnect = "<0xd60> <friend> <0xd57> .";
    feedEntities(edgeConnect);
  }

  public static  void main(String []arg) {
    DClient dClient = new DClient(Config.TEST_VM_HOSTNAME);
    Demo demo = new Demo(dClient);
    long value = Long.parseLong("0x1780e".substring(2), 16);
    String hexValue = Long.toHexString(98951);
    System.out.println(value + ", 0x" + hexValue);
    demo.dClient.dropSchema();
    demo.dClient.alterSchema(Config.updateSchema);
    // demo.edgeConnect();
    System.out.println("finished");

  }
}

package dgraph;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import dgraph.node.Person;
import io.dgraph.DgraphProto;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

public class Demo {

  // produ client ip : 172.20.0.8
  // test client ip : 172.20.0.68
  private static final String TEST_HOSTNAME = "172.20.0.68";
  private static final int TEST_PORT = 9080;
  private static final Logger logger = LoggerFactory.getLogger(Demo.class);
  private DClient dClient;

  public Demo() {
    dClient = new DClient(TEST_HOSTNAME, TEST_PORT);
  }

  public List<Person> CheckOutEntities(List<Person> persons) {
    for (Person person : persons) {
      String uid = dClient.QueryById(person.getId(), "dgraph.node.People", "getExistUid");
      if (!"".equals(uid)) {
        person.setUid(uid);
      }
    }
    return persons;
  }

  public List<DgraphProto.Assigned> feedEntities(List<String> entities) {
    List<DgraphProto.Assigned> assignedList;
    io.dgraph.DgraphClient.Transaction txn = dClient.getDgraphClient().newTransaction();
    try {
      assignedList = dClient.mutiplyMutation(txn, entities);
      txn.commit();
    } finally {
      txn.discard();
    }
    return assignedList;
  }
  public static  void main(String []args) {
    Demo demo = new Demo();
    // demo.dClient.dropSchema();
    demo.dClient.alterSchema(Config.updateSchema);
    List<String>  personList = new ArrayList<String>();
    List<Person> persons = new ArrayList<Person>();
    Person person1 = new Person("P1-new", "youcj", 25);
    person1.setGender(1);
    Person person3 = new Person("P3","Gehy", 24);
    person3.setGender(2);
    Person person2 = new Person("P2", "youys", 1);
    person2.setGender(1);
    persons.add(person1);
    persons.add(person2);
    persons.add(person3);
    // 所有实体必须验证是否存在dgraph中，先判断uid是否有了
    demo.CheckOutEntities(persons);
    List<Person> friends = new ArrayList<Person>();
    friends.add(person3);
    friends.add(person2);
    person1.setFriend(friends);
    personList.add(person1.toString());
    List<DgraphProto.Assigned> assignedList = demo.feedEntities(personList);
    for (DgraphProto.Assigned assigned : assignedList) {
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

  }
}

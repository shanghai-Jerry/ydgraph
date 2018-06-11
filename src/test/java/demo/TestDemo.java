package demo;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import com.higgs.dgraph.Config;
import com.higgs.dgraph.DClient;
import com.higgs.dgraph.Demo;
import com.higgs.dgraph.node.NodeUtil;
import com.higgs.dgraph.node.Person;
import com.higgs.dgraph.node.School;

public class TestDemo {

  DClient dClient = new DClient(Config.TEST_VM_HOSTNAME);

  Demo demo = new Demo(dClient);

  private void  getFriends(List<Person> personList) {
    for (Person person : personList) {
      NodeUtil.putEntityUidWithNames(person.getFriends(), new HashMap<String, List<String>>());
      System.out.println("entity:" + new Gson().toJson(personList.get(0)));
    }
  }

  private void getSchools(List<Person> personList) {

  }

  private void testJsonPutRetUid() {
    Person alice = new Person();
    alice.setName("Alice");
    alice.setAge(26);
    alice.setMarried(true);
    alice.setLocation("Riley Street");

    School school = new School();
    school.setName("Crown Public School");
    List<School> schools = new ArrayList<>();
    schools.add(school);
    alice.setSchools(schools);

    List<Person> friends = new ArrayList<>();
    Person bob = new Person();
    bob.setName("Bob");
    bob.setAge(24);
    friends.add(bob);
    Person charlie = new Person();
    charlie.setName("Charlie");
    charlie.setAge(29);
    friends.add(charlie);
    alice.setFriends(friends);

    getFriends(Arrays.asList(alice));

    // Map<String, List<String>> uidMap = NodeUtil.putEntity(dClient, Arrays.asList(alice));
    // FileUtils.saveFile("src/main/resources/person_uid_map.txt", uidMap);
  }
  public static void main(String[] args) throws Exception {
    TestDemo testDemo = new TestDemo();
    // testDemo.demo.init();
    // testDemo.testJsonPutRetUid();

  }

}

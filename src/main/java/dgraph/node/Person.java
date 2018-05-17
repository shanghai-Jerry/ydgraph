package dgraph.node;

import com.google.gson.Gson;

import java.util.List;

/**
 * 测试实体类
 */
public class Person {
  String uid;
  String id;
  String name;
  List<Person> friend;
  List<Person> isExist;
  int gender;

  int age;
  // ...
  String otherAttri;

  public Person() {

  }

  public Person(String id, String name, int age) {
    this.age = age;
    this.id = id;
    this.name = name;
  }

  public String getUid() {
    return uid;
  }

  public List<Person> getFriend() {
    return friend;
  }

  public void setFriend(List<Person> friend) {
    this.friend = friend;
  }

  public void setUid(String uid) {
    this.uid = uid;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public int getGender() {
    return gender;
  }

  public void setGender(int gender) {
    this.gender = gender;
  }

  public List<Person> getIsExist() {
    return isExist;
  }

  public void setIsExist(List<Person> isExist) {
    this.isExist = isExist;
  }

  public String getExistUid() {
    if (isExist.size() == 1) {
      return isExist.get(0).getUid();
    }
    return "";
  }

  @Override
  public String toString() {
    return new Gson().toJson(this);
  }
}

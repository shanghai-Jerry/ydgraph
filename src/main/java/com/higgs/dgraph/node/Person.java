package com.higgs.dgraph.node;

import com.google.gson.Gson;

import java.util.List;

/**
 * 测试实体类
 */
public class Person  extends  EntityNode {
  String id;
  String location;
  List<Person> friends;
  List<Person> isExist;
  List<School> schools;
  int gender;
  boolean married;

  int age;
  // ...
  String otherAttri;

  public Person() { }

  public List<School> getSchools() {
    return schools;
  }

  public void setSchools(List<School> schools) {
    this.schools = schools;
  }

  public boolean isMarried() {
    return married;
  }

  public void setMarried(boolean married) {
    this.married = married;
  }

  public String getLocation() {
    return location;
  }

  public String getOtherAttri() {
    return otherAttri;
  }

  public int getAge() {
    return age;
  }

  public void setAge(int age) {
    this.age = age;
  }

  public void setOtherAttri(String otherAttri) {
    this.otherAttri = otherAttri;
  }

  public void setLocation(String location) {
    this.location = location;
  }

  public Person(String id, String name, int age) {
    this.setAge(age);
    this.setId(id);
    this.setName(name);
  }

  public List<Person> getFriends() {
    return friends;
  }

  public void setFriends(List<Person> friend) {
    this.friends = friend;
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

package com.higgs.dgraph.node;

import java.util.List;

public class People {

  private List<Person> isExist;
  private List<Person> all;

  public People() {}

  public List<Person> getIsExist() {
    return isExist;
  }

  public void setIsExist(List<Person> isExist) {
    this.isExist = isExist;
  }

  public List<Person> getAll() {
    return all;
  }

  public void setAll(List<Person> all) {
    this.all = all;
  }

  public String getExistUid() {
    if (isExist.size() == 1) {
      return isExist.get(0).getUid();
    }
    return "";
  }

}

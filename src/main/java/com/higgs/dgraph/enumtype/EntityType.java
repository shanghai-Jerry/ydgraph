package com.higgs.dgraph.enumtype;

/**
 * User: JerryYou
 *
 * Date: 2018-06-29
 *
 * Copyright (c) 2018 devops
 *
 * <<licensetext>>
 */
public enum EntityType {
  SCHOOL("学校", 1), COMPANY("公司", 2), INDUSTRY("行业", 3), CANDIDATE("候选人",4),
  COMPANY_DEPT("公司部门", 5), MAJOR("专业", 6), AGE("年龄",7), DEGREE("学历", 8), GENDER("性别", 9),
  SENIORITY("工作年限", 10), SALARY("薪资", 11)
  ;
  private String name;
  private int index;
  // 构造方法
  private EntityType(String name, int index) {
    this.name = name;
    this.index = index;
  }
  // get set 方法
  public String getName() {
    return name;
  }
  public void setName(String name) {
    this.name = name;
  }
  public int getIndex() {
    return index;
  }
  public void setIndex(int index) {
    this.index = index;
  }
}

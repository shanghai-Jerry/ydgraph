package com.higgs.grakn;


import org.apache.commons.lang.StringUtils;

/**
 * User: JerryYou
 *
 * Date: 2019-08-22
 *
 * Copyright (c) 2018 devops
 *
 * <<licensetext>>
 */
public class CompanyEntity {
  String name;
  String uniqueId;
  StringBuilder stringBuilder = new StringBuilder();

  public CompanyEntity(String name) {
    this.name = name;
    this.uniqueId = Schema.getMD5(name);
  }

  public CompanyEntity isA(String var) {
    stringBuilder.append("$" + uniqueId);
    stringBuilder.append(" isa ");
    stringBuilder.append(var);
    return this;
  }

  public CompanyEntity has(String name, String[] vars) {
    stringBuilder.append(",has " + name);
    stringBuilder.append(" \""+StringUtils.join(vars, ",")+"\"");
    return this;
  }

  @Override
  public String toString() {
    return stringBuilder.toString() + ";";
  }


}

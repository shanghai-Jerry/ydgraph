package com.higgs.dgraph.node;

import java.util.ArrayList;
import java.util.List;

/**
 * User: JerryYou
 *
 * Date: 2018-05-22
 *
 * Copyright (c) 2018 devops
 *
 * <<licensetext>>
 */
public class Candidate extends EntityNode {

  List<Company> candidate_company;

  List<DeptName> candidate_company_dept = new ArrayList<>();

  String gender;

  public List<DeptName> getCandidate_company_dept() {
    return candidate_company_dept;
  }

  public void setCandidate_company_dept(List<DeptName> candidate_company_dept) {
    this.candidate_company_dept = candidate_company_dept;
  }

  public String getGender() {
    return gender;
  }

  public void setGender(String gender) {
    this.gender = gender;
  }

  public List<Company> getCandidate_company() {
    return candidate_company;
  }

  public void setCandidate_company(List<Company> candidate_company) {
    this.candidate_company = candidate_company;
  }
}


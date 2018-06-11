package com.higgs.dgraph.node;

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

  String gender;

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


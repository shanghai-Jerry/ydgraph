package com.higgs.dgraph.node;

import java.util.List;

public class Company extends EntityNode {

  private String location;
  private String legal_person;
  private String establish_at;
  private List<Industry> industry;

  public String getLocation() {
    return location;
  }

  public void setLocation(String location) {
    this.location = location;
  }

  public String getLegal_person() {
    return legal_person;
  }

  public void setLegal_person(String legal_person) {
    this.legal_person = legal_person;
  }

  public String getEstablish_at() {
    return establish_at;
  }

  public void setEstablish_at(String establish_at) {
    this.establish_at = establish_at;
  }

  public List<Industry> getIndustry() {
    return industry;
  }

  public void setIndustry(List<Industry> industry) {
    this.industry = industry;
  }
}

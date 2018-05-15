package dgraph.node;

import java.util.List;

/**
 * User: JerryYou
 *
 * Date: 2018-05-10
 *
 * Copyright (c) 2018 devops
 *
 * <<licensetext>>
 */
public class Label extends EntityNode {

  List<School> school;

  Company company;

  Industry industry;

  public List<School> getSchool() {
    return school;
  }

  public void setSchool(List<School> school) {
    this.school = school;
  }

  public Company getCompany() {
    return company;
  }

  public void setCompany(Company company) {
    this.company = company;
  }

  public Industry getIndustry() {
    return industry;
  }

  public void setIndustry(Industry industry) {
    this.industry = industry;
  }
}

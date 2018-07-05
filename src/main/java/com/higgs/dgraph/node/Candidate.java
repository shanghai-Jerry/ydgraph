package com.higgs.dgraph.node;

import com.higgs.dgraph.enumtype.EntityType;
import com.higgs.utils.TimeUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

  private static Map<String, Integer> genderMap = new HashMap<>();

  static  {
    genderMap.put("男", 0);
    genderMap.put("女", 1);
  }

  List<Company> candidate_company = new ArrayList<>();

  List<DeptName> candidate_company_dept = new ArrayList<>();

  private  String gender;

  private String birthday;

  private int degree;

  private String started_work_at;

  private GenderNode gender_node = new GenderNode();

  private AgeNode age_node = new AgeNode();

  private DegreeNode degree_node = new DegreeNode();

  // 年薪
  private List<SalaryNode> annual_salary = new ArrayList<>();

  // 月薪
  private List<SalaryNode> monthly_salary  = new ArrayList<>();

  private SeniorityNode seniorty_node = new SeniorityNode();

  public String getStarted_work_at() {
    return started_work_at;
  }

  public void setStarted_work_at(String started_work_at) {
    this.started_work_at = started_work_at;
  }

  public String getBirthday() {
    return birthday;
  }

  public void setBirthday(String birthday) {
    this.birthday = birthday;
  }

  public int getDegree() {
    return degree;
  }

  public void setDegree(int degree) {
    this.degree = degree;
  }

  public GenderNode getGender_node() {
    return gender_node;
  }

  public void setGender_node(GenderNode gender_node) {
    this.gender_node = gender_node;
  }

  public AgeNode getAge_node() {
    return age_node;
  }

  public void setAge_node(AgeNode age_node) {
    this.age_node = age_node;
  }

  public DegreeNode getDegree_node() {
    return degree_node;
  }

  public void setDegree_node(DegreeNode degree_node) {
    this.degree_node = degree_node;
  }

  public List<SalaryNode> getAnnual_salary() {
    return annual_salary;
  }

  public void setAnnual_salary(List<SalaryNode> annual_salary) {
    this.annual_salary = annual_salary;
  }

  public List<SalaryNode> getMonthly_salary() {
    return monthly_salary;
  }

  public void setMonthly_salary(List<SalaryNode> monthly_salary) {
    this.monthly_salary = monthly_salary;
  }

  public SeniorityNode getSeniorty_node() {
    return seniorty_node;
  }

  public void setSeniorty_node(SeniorityNode seniorty_node) {
    this.seniorty_node = seniorty_node;
  }

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
    this.setGenderNode();
  }

  public List<Company> getCandidate_company() {
    return candidate_company;
  }

  public void setCandidate_company(List<Company> candidate_company) {
    this.candidate_company = candidate_company;
  }

  private int getAge() {
    String currentDate = TimeUtil.getDataFormator().format(new Date());
    int currentYear = Integer.parseInt(currentDate.substring(0, currentDate.indexOf("-")));
    String howOld = this.birthday;
    if (howOld != null && !"".equals(howOld) && howOld.contains("-") && howOld.length() <= 60) {
      int birthdayYear = Integer.parseInt(howOld.substring(0, howOld.indexOf("-")));
      int years = currentYear - birthdayYear;
      return years;
    } else {
      return 0;
    }
  }

  private int getSeniorty() {
    String currentDate = TimeUtil.getDataFormator().format(new Date());
    int currentYear = Integer.parseInt(currentDate.substring(0, currentDate.indexOf("-")));
    String startWorkAt = this.started_work_at;
    if (startWorkAt != null && !"".equals(startWorkAt) && startWorkAt.contains("-") &&
        startWorkAt.length() <= 60) {
      int workYear = Integer.parseInt(startWorkAt.substring(0, startWorkAt.indexOf("-")));
      int years = currentYear - workYear;
      return years;
    } else {
      return 0;
    }
  }

  private void setAgeNode() {
    AgeNode ageNode = new AgeNode();
    int ageRange = -1;
    int age = getAge();
    if (0 < age && age < 20) {
      ageRange = 0;
    } else if (20 <= age && age < 25) {
      ageRange = 1;
    } else if (25 <= age && age < 30) {
      ageRange = 2;
    } else if (30 <= age && age < 35) {
      ageRange = 3;
    } else if (35 <= age) {
      ageRange = 4;
    }
    if (ageRange < 0) {
      this.setAge_node(ageNode);
      return;
    }
    NodeUtil.getEntityNode(ageNode, ageRange, EntityType.AGE.getIndex());
    this.setAge_node(ageNode);
  }
  private void setDegreeNode() {
    DegreeNode degreeNode = new DegreeNode();
    NodeUtil.getEntityNode(degreeNode, this.degree, EntityType.DEGREE.getIndex());
    this.setDegree_node(degreeNode);
  }

  private void setGenderNode() {
    GenderNode genderNode = new GenderNode();
    String gender = this.gender;
    int genderRange = -1;
    if (gender != null) {
      genderRange = genderMap.getOrDefault(gender, -1);
    }
    if (genderRange < 0) {
      this.setGender_node(genderNode);
      return;
    }
    NodeUtil.getEntityNode(genderNode, genderRange, EntityType.GENDER.getIndex());
    this.setGender_node(genderNode);
  }

  private void setSeniortyNode() {
    SeniorityNode seniorityNode = new SeniorityNode();
    int seniortyRange = -1;
    int seniorty = getSeniorty();
    if (seniorty < 1) {
      seniortyRange = 0;
    } else if (1 == seniorty) {
      seniortyRange = 1;
    } else if (2 == seniorty) {
      seniortyRange = 2;
    } else if (3 <= seniorty && seniorty < 5) {
      seniortyRange = 3;
    } else if ( 5 <= seniorty && seniorty < 10) {
      seniortyRange = 4;
    } else if (10 <= seniorty) {
      seniortyRange = 5;
    }
    if (seniortyRange < 0) {
      this.setSeniorty_node(seniorityNode);
      return;
    }
    NodeUtil.getEntityNode(seniorityNode, seniortyRange, EntityType.SENIORITY.getIndex());
    this.setSeniorty_node(seniorityNode);
  }

  public SalaryNode getMonthlySalary(double salary) {
    SalaryNode salaryNode = new SalaryNode();
    int salaryRange = -1;
    if (salary < 3000) {
      salaryRange = 0;
    } else if (3000 <= salary && salary < 7000) {
      salaryRange = 1;
    } else if (7000 <= salary && salary < 15000) {
      salaryRange = 2;
    } else if (15000 <= salary && salary < 20000) {
      salaryRange = 3;
    } else if (20000 <= salary && salary < 30000) {
      salaryRange = 4;
    } else if (30000 <= salary && salary < 50000) {
      salaryRange = 5;
    } else if (50000 <= salary) {
      salaryRange = 6;
    }
    NodeUtil.getEntityNode(salaryNode, salaryRange, EntityType.SALARY.getIndex());
    return salaryNode;
  }

  public SalaryNode getAnnualySalary(double salary) {
    SalaryNode salaryNode = new SalaryNode();
    int salaryRange = -1;
    int base = 10000;
    if (salary < 10 * base) {
      salaryRange = 0;
    } else if (10 * base <= salary && salary < 20 * base) {
      salaryRange = 1;
    } else if (20 * base <= salary && salary < 30 * base) {
      salaryRange = 2;
    } else if (30 * base <= salary && salary < 50 * base) {
      salaryRange = 3;
    } else if (50 * base <= salary && salary < 100 * base) {
      salaryRange = 4;
    } else if (100 * base <= salary) {
      salaryRange = 5;
    }
    NodeUtil.getEntityNode(salaryNode, salaryRange, EntityType.ANNUAL.getIndex());
    return salaryNode;
  }
}


package dgraph;

/**
 * User: JerryYou
 *
 * Date: 2018-05-08
 *
 * Copyright (c) 2018 devops
 *
 * <<licensetext>>
 */
public class StartMain {
  public static void main(String[] args) {

    SchoolToDgraph schoolToDgraph = new SchoolToDgraph();
    String dict = "src/main/resources/industry_dump_dict.txt";
    int needCheck = 0;
    IndustryToDgraph industryToDgraph = new IndustryToDgraph();
    industryToDgraph.initWithJson(dict, needCheck);
    String schoolPath = "/Users/devops/Documents/知识图谱/school/school_dump_dict.txt";
    schoolToDgraph.initWithJson(schoolPath, needCheck);
    String majorPath = "/Users/devops/workspace/gitlab/idmg/resume_extractor/src/cc/major_dict.txt";
    MajorToDgraph majorToDgraph = new MajorToDgraph();
    majorToDgraph.initWithJson(majorPath, needCheck);
    System.out.println("finished");
  }

}

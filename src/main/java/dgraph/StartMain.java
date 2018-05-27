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

  public void withJson() {
    SchoolToDgraph schoolToDgraph = new SchoolToDgraph();
    String dict = "src/main/resources/industry_dump_dict.txt";
    int needCheck = 0;
    IndustryToDgraph industryToDgraph = new IndustryToDgraph();
    industryToDgraph.initWithJson(dict);
    String schoolPath = "src/main/resources/school_dump_dict.txt";
    schoolToDgraph.initWithJson(schoolPath);
    String majorPath = "src/main/resources/major_dict.txt";
    MajorToDgraph majorToDgraph = new MajorToDgraph();
    majorToDgraph.initWithJson(majorPath);
    System.out.println("finished");
  }
  public static void main(String[] args) {
    DClient dClient = new DClient(Config.TEST_HOSTNAME);
    // 行业
    String dict = "src/main/resources/industry_dump_dict.txt";
    IndustryToDgraph industryToDgraph = new IndustryToDgraph(dClient);
    // with rdf
    industryToDgraph.initWithRdf(dict);

    // 专业
    String dictPath = "src/main/resources/major_dict.txt";
    MajorToDgraph majorToDgraph = new MajorToDgraph(dClient);
    majorToDgraph.initWithRdf(dictPath);

    // 学校
    SchoolToDgraph schoolToDgraph = new SchoolToDgraph(dClient);
    String schoolPath = "src/main/resources/school_dump_dict.txt";
    schoolToDgraph.initWithRdf(schoolPath);
  }

}

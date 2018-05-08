package dgraph;

public class StartMain {
  public static void main(String[] args) {

    SchoolToDgraph schoolToDgraph = new SchoolToDgraph();
    String schoolPath = "/Users/devops/Documents/知识图谱/school/school_dump_dict.txt";
    schoolToDgraph.initWithJson(schoolPath);
    String majorPath = "/Users/devops/workspace/gitlab/idmg/resume_extractor/src/cc/major_dict.txt";
    MajorToDgraph majorToDgraph = new MajorToDgraph();
    majorToDgraph.initWithJson(majorPath);
    System.out.println("finished");
  }

}

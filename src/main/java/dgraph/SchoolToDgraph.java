package dgraph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import client.EntityIdClient;
import dgraph.node.NodeUtil;
import dgraph.node.School;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import utils.FileUtils;

public class SchoolToDgraph {

  private static final Logger logger = LoggerFactory.getLogger(SchoolToDgraph.class);
  private DClient dClient;
  private EntityIdClient entityIdClient;

  public SchoolToDgraph() {
    dClient = new DClient(Config.TEST_HOSTNAME, Config.TEST_PORT);
    entityIdClient = new EntityIdClient(Config.EntityId_Host, Config.EntityIdService_PORT);
  }

  public void getSchool(List<String> dictLines, List<School> schools) {
    List<String> distinctSchoolName = new ArrayList<String>();
    for (String line : dictLines) {
      List<String> names = new ArrayList<String>();
      School school = new School();
      String[] lineSplits = line.split("\t");
      if (lineSplits.length != 7) {
        System.out.println("line:" + line + ",line length:" + lineSplits.length);
      }
      String name = lineSplits[3];
      String alias = lineSplits[5];
      if (!distinctSchoolName.contains(name)) {
        names.add(name);
        school.setName(name);
        school.setEngName(lineSplits[4]);
        school.setAlias(alias);
        school.setType("学校");
        school.setNames(names);
        schools.add(school);
      } else {
        logger.info("dup school name:" + name);
      }
      distinctSchoolName.add(name);
    }
  }

  /**
   * 分别获取是新增实体list还是需更新的实体list
   * @param schools
   * @param dputList
   * @param duputList
   */
  public void getList(List<School> schools, List<School> dputList, List<School> duputList) {
    List<List<String>> reqs = new ArrayList<List<String>>();
    Map<String, String> uidMap = new HashMap<String, String>();
    String type = "";
    for (School school : schools) {
      if ("".equals(type)) {
        type = school.getType();
      }
      List<String> names = new ArrayList<String>();
      names.add(school.getName());
      reqs.add(names);
    }
    entityIdClient.checkEntityList(reqs, uidMap, type);
    for (School school : schools) {
      if (uidMap.containsKey(school.getName())) {
        school.setUid(uidMap.get(school.getName()));
        duputList.add(school);
      } else {
        dputList.add(school);
      }
    }
  }


  /**
   * 初始化实体
   * @param filePath
   */
  public void init(String filePath) {
    List<String> dictLines = new ArrayList<String>();
    List<School> schools = new ArrayList<School>();
    Map<String, String> uidMaps = new HashMap<String, String>();
    FileUtils.readFiles(filePath, dictLines);
    getSchool(dictLines, schools);
    long startTime = System.currentTimeMillis();
    System.out.println("get all schools :" + schools.size());
    List<School> schoolList = new ArrayList<School>();
    List<School> updateSchoolList = new ArrayList<School>();
    getList(schools, schoolList, updateSchoolList);
    System.out.println("get separate list: :" + schoolList.size() +
        ", " + updateSchoolList.size());
    // insert
    NodeUtil.insertEntity(dClient, schoolList, uidMaps);
    entityIdClient.putFeedEntity(uidMaps, "学校");
    // update
    NodeUtil.updateEntityNew(dClient, updateSchoolList);

    long endStart = System.currentTimeMillis();
    System.out.println("spend time:" + (endStart - startTime) + " ms");
  }

  /**
   * 初始化实体以json的方式
   * @param filePath
   */
  public void initWithJson(String filePath, int needCheck) {
    String type = "学校";
    List<String> dictLines = new ArrayList<String>();
    List<School> schools = new ArrayList<School>();
    FileUtils.readFiles(filePath, dictLines);
    getSchool(dictLines, schools);
    System.out.println("get all schools :" + schools.size());
    NodeUtil.putEntity(dClient, entityIdClient, schools, type, needCheck);
  }

  public static void main(String[] args) {
    SchoolToDgraph schoolToDgraph = new SchoolToDgraph();
    List<School> schools = new ArrayList<School>();
    String dictPath = "/Users/devops/Documents/知识图谱/school/school_dump_dict.txt";
    int needCheck = 1;
    // schoolToDgraph.init(dictPath);
    schoolToDgraph.initWithJson(dictPath, needCheck);
  }
}

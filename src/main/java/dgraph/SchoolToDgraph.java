package dgraph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import client.EntityIdClient;
import dgraph.node.Label;
import dgraph.node.NodeUtil;
import dgraph.node.School;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import utils.FileUtils;

/**
 * User: JerryYou
 *
 * Date: 2018-05-08
 *
 * Copyright (c) 2018 devops
 *
 * <<licensetext>>
 */
public class SchoolToDgraph {

  private static final Logger logger = LoggerFactory.getLogger(SchoolToDgraph.class);
  private DClient dClient;
  private EntityIdClient entityIdClient;

  private List<School> schools = new ArrayList<>();

  public SchoolToDgraph() {
    dClient = new DClient(Config.addressList);
    entityIdClient = new EntityIdClient(Config.EntityId_Host, Config.EntityIdService_PORT);
  }

  public SchoolToDgraph(DClient dClient) {
    this.dClient = dClient;
    entityIdClient = new EntityIdClient(Config.EntityId_Host, Config.EntityIdService_PORT);
  }

  public void getSchool(List<String> dictLines, List<School> schools) {
    List<String> distinctSchoolName = new ArrayList<String>();
    for (String line : dictLines) {
      School school = new School();
      String[] lineSplits = line.split("\t");
      if (lineSplits.length != 7) {
        System.out.println("line:" + line + ",line length:" + lineSplits.length);
      }
      String name = lineSplits[3];
      String alias = lineSplits[5];
      if (!distinctSchoolName.contains(name)) {
        school.setName(name);
        school.setEng_name(lineSplits[4]);
        school.setAlias(alias);
        school.setType("学校");
        Label has_label = new Label();
        has_label.setUid("0x118c");
        school.setHas_label(has_label);
        schools.add(school);
      } else {
        logger.info("dup school name:" + name);
      }
      distinctSchoolName.add(name);
    }
  }

  /**
   * 分别获取是新增实体list还是需更新的实体list
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
   */
  public void init(String filePath) {
    List<String> dictLines = new ArrayList<String>();
    FileUtils.readFiles(filePath, dictLines);
    getSchool(dictLines, schools);
  }

  public void initWithRDFMode() {
    long startTime = System.currentTimeMillis();
    System.out.println("get all schools :" + schools.size());
    List<School> schoolList = new ArrayList<School>();
    List<School> updateSchoolList = new ArrayList<School>();
    getList(schools, schoolList, updateSchoolList);
    System.out.println("get separate list: :" + schoolList.size() + ", " + updateSchoolList.size());
    // insert
    Map<String, String> uidMaps = NodeUtil.insertEntity(dClient, schoolList);
    long endStart = System.currentTimeMillis();
    System.out.println("spend time:" + (endStart - startTime) + " ms");
  }

  /**
   * 初始化实体以json的方式
   */
  public void initWithJson(String filePath, int needCheck) {
    String type = "学校";
    List<String> dictLines = new ArrayList<String>();
    List<School> schools = new ArrayList<School>();
    FileUtils.readFiles(filePath, dictLines);
    getSchool(dictLines, schools);
    System.out.println("get all schools :" + schools.size());
    Map<String, String> uidMap = NodeUtil.putEntity(dClient, entityIdClient, schools, type,
        needCheck);
    FileUtils.saveFile("src/main/resources/school_uid_map.txt", uidMap);
    entityIdClient.putFeedEntity(uidMap, type);
  }

  public static void main(String[] args) {
    SchoolToDgraph schoolToDgraph = new SchoolToDgraph();
    List<School> schools = new ArrayList<School>();
    String dictPath = "src/main/resources/school_dump_dict.txt";
    int needCheck = 0;
    schoolToDgraph.initWithJson(dictPath, needCheck);
  }
}

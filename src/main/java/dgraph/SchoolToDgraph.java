package dgraph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import client.EntityIdClient;
import dgraph.node.Label;
import dgraph.node.Major;
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
        schools.add(school);
      } else {
        logger.info("dup school name:" + name);
      }
      distinctSchoolName.add(name);
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

  public void initWithRdf() {
    // .. todo
  }

  public List<Label> getLabeledSchool(List<School> schools) {
    List<Label> labelList = new ArrayList<>();
    for (School school : schools) {
      Label label = new Label();
      label.setUid("0x118c");
      label.setSchool(school);
      labelList.add(label);
    }
    return labelList;
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
    Map<String,  List<String>> uidMap = NodeUtil.putEntity(dClient, schools);
    FileUtils.saveFile("src/main/resources/school_uid_map.txt", uidMap);
    entityIdClient.putFeedEntityWithNames(uidMap, type);
    NodeUtil.putEntity(dClient, getLabeledSchool(schools));
  }

  public static void main(String[] args) {
    SchoolToDgraph schoolToDgraph = new SchoolToDgraph();
    List<School> schools = new ArrayList<School>();
    String dictPath = "src/main/resources/school_dump_dict.txt";
    int needCheck = 0;
    schoolToDgraph.initWithJson(dictPath, needCheck);
  }
}

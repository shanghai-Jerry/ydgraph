package com.higgs.dgraph;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.higgs.client.EntityIdClient;
import com.higgs.dgraph.node.Label;
import com.higgs.dgraph.node.NodeUtil;
import com.higgs.dgraph.node.School;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import com.higgs.utils.FileUtils;

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
    entityIdClient = new EntityIdClient(Config.ENTITY_ID_HOST, Config.ENTITY_ID_SERVICE_PORT);
  }

  public SchoolToDgraph(DClient dClient, EntityIdClient entityIdClient) {
    this.dClient = dClient;
    this.entityIdClient = entityIdClient;
  }

  public void getSchool(List<String> dictLines, List<School> schools) {
    List<String> distinctSchoolName = new ArrayList<String>();
    for (String line : dictLines) {
      School school = new School();
      String[] lineSplits = line.split("\t");
      if (lineSplits.length != 5) {
        System.out.println("line:" + line + ",line length:" + lineSplits.length);
      }
      String code = lineSplits[3];
      String name = lineSplits[4];
      if (!distinctSchoolName.contains(name)) {
        school.setName(name);
        // school.setEng_name(engName);
        // school.setAlias(alias);
        school.setUnique_id(code);
        school.setCode(Integer.parseInt(code.trim()));
        school.setUnique_ids(Arrays.asList(code, name));
        school.setType("学校");
        schools.add(school);
        this.schools.add(school);
        distinctSchoolName.add(name);
      } else {
        logger.info("dup school name:" + name);
      }
    }
  }


  /**
   * 初始化实体
   */
  private void init(String filePath) {
    List<String> dictLines = new ArrayList<String>();
    FileUtils.readFiles(filePath, dictLines);
    getSchool(dictLines, schools);
  }

  public void initWithRdf(String filePath) {
    // .. todo
    String type = "学校";
    init(filePath);
    Map<String,  List<String>> uidMap  = NodeUtil.insertEntity(dClient, this.schools);
    FileUtils.saveFile("src/main/resources/school_uid_map.txt", uidMap);
    entityIdClient.putFeedEntityWithUidNamesMap(uidMap, type);
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
  public void initWithJson(String filePath) {
    String type = "学校";
    List<String> dictLines = new ArrayList<String>();
    List<School> schools = new ArrayList<School>();
    FileUtils.readFiles(filePath, dictLines);
    getSchool(dictLines, schools);
    System.out.println("get all schools :" + schools.size());
    Map<String,  List<String>> uidMap = NodeUtil.putEntity(dClient, schools);
    FileUtils.saveFile("src/main/resources/school_uid_map.txt", uidMap);
    entityIdClient.putFeedEntityWithUidNamesMap(uidMap, type);
    // NodeUtil.putEntity(dClient, getLabeledSchool(schools));
  }
  public void generateRDF() {
    List<String> entityNquads = NodeUtil.getEntityNquads(this.schools, new ArrayList<>());
    FileUtils.saveFile("./school_rdf.txt",  entityNquads, false);
  }

  public static void main(String[] args) {
    DClient dClient = new DClient(Config.TEST_HOSTNAME);
    EntityIdClient client = new EntityIdClient(Config.ENTITY_ID_HOST, Config.ENTITY_ID_SERVICE_PORT_TEST);
    SchoolToDgraph schoolToDgraph = new SchoolToDgraph(dClient, client);
    String dictPath = "src/main/resources/school_dump_dict.txt";
    // schoolToDgraph.initWithJson(dictPath);
    schoolToDgraph.initWithRdf(dictPath);
    // schoolToDgraph.init(dictPath);
  }
}

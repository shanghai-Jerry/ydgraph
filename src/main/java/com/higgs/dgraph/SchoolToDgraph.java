package com.higgs.dgraph;


import com.higgs.client.EntityIdClient;
import com.higgs.dgraph.enumtype.EntityType;
import com.higgs.dgraph.node.NodeUtil;
import com.higgs.dgraph.node.School;
import com.higgs.utils.FileUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

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

  public void getSchool(List<String> dictLines, List<School> schools, Boolean isRDF) {
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
        if (isRDF) {
          school.setUnique_id(EntityType.SCHOOL.getName() + ":" + NodeUtil.generateEntityUniqueId
              (name));
        } else {
          school.setUnique_id(name);
        }
        school.setCode(Integer.parseInt(code.trim()));
        school.setUnique_ids(Arrays.asList(code, name));
        school.setType(EntityType.SCHOOL.getName());
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
  public void init(String filePath, boolean isRDF) {
    List<String> dictLines = new ArrayList<String>();
    FileUtils.readFiles(filePath, dictLines);
    getSchool(dictLines, schools, isRDF);
  }

  public void initWithRdf(String filePath, boolean isRDF) {
    String type = "学校";
    init(filePath, isRDF);
    Map<String,  List<String>> uidMap  = NodeUtil.insertEntity(dClient, this.schools);
    entityIdClient.putFeedEntityWithUidNamesMap(uidMap, type);
  }

  public void generateRDF(String out) {
    List<String> entityNquads = NodeUtil.getEntityNquads(this.schools, new ArrayList<>());
    FileUtils.saveFile(out +"/school_rdf.txt",  entityNquads, false);
  }

  /**
   * 初始化实体以json的方式
   */
  public void initWithJson(String filePath) {
    String type = "学校";
    List<String> dictLines = new ArrayList<String>();
    List<School> schools = new ArrayList<School>();
    FileUtils.readFiles(filePath, dictLines);
    getSchool(dictLines, schools, true);
    System.out.println("get all schools :" + schools.size());
    Map<String,  List<String>> uidMap = NodeUtil.putEntity(dClient, schools);
    FileUtils.saveFile("src/main/resources/school_uid_map.txt", uidMap);
    entityIdClient.putFeedEntityWithUidNamesMap(uidMap, type);
    // NodeUtil.putEntity(dClient, getLabeledSchool(schools));
  }

  public static void main(String[] args) {
    if (args.length < 1) {
      System.err.println("Usage : <Industry_dict_path>");
      System.exit(-1);
    }
    String dict = args[0];
    SchoolToDgraph schoolToDgraph = new SchoolToDgraph();
    schoolToDgraph.initWithRdf(dict, true);
  }
}

package com.higgs.dgraph;


import com.higgs.client.EntityIdClient;
import com.higgs.dgraph.node.Major;
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
public class MajorToDgraph {
  private static final Logger logger = LoggerFactory.getLogger(MajorToDgraph.class);
  private DClient dClient;
  private EntityIdClient entityIdClient;
  private List<Major> majors = new ArrayList<>();

  public MajorToDgraph() {
    dClient = new DClient(Config.addressList);
    entityIdClient = new EntityIdClient(Config.ENTITY_ID_HOST, Config.ENTITY_ID_SERVICE_PORT);
  }

  public MajorToDgraph(DClient dClient, EntityIdClient entityIdClient) {
    this.dClient = dClient;
    this.entityIdClient = entityIdClient;
  }

  public void getMajor(List<String> dictLines, List<Major> majors, boolean isRdf) {
    for (String line : dictLines) {
      List<String> names = new ArrayList<String>();
      Major major = new Major();
      String[] lineSplits = line.split("\t");
      if (lineSplits.length != 2) {
        System.out.println("line:" + line + ",line length:" + lineSplits.length);
      }
      String name = lineSplits[1];
      String code = lineSplits[0].trim();
      if (!names.contains(name)) {
        int codeInt = Integer.parseInt(code);
        major.setName(name);
        major.setCode(codeInt);
        major.setType("专业");
        if (isRdf) {
          major.setUnique_id("专业" + ":" + NodeUtil.generateEntityUniqueId(name));
          major.setUnique_ids(Arrays.asList("专业" + ":" +NodeUtil.generateEntityUniqueId(code), "专业" + ":" +NodeUtil.generateEntityUniqueId(name)));
        } else {
          major.setUnique_id(name);
          major.setUnique_ids(Arrays.asList(code, name));
        }
        names.add(name);
        if (codeInt == 0) {
          continue;
        }
        majors.add(major);
      }
    }
  }

  public void init(String dictPath, boolean isRdf) {
    List<String> dictLines = new ArrayList<String>();
    FileUtils.readFiles(dictPath, dictLines);
    getMajor(dictLines, this.majors, isRdf);
  }


  public void initWithRdf(String dictPath, boolean isRdf) {
    String type = "专业";
    init(dictPath, isRdf);
    Map<String, List<String>> uidMap = NodeUtil.insertEntity(dClient, majors);
    entityIdClient.putFeedEntityWithUidNamesMap(uidMap, type);
  }

  public void generateRDF(String out) {
    List<String> entityNquads = NodeUtil.getEntityNquads(this.majors, new ArrayList<>());
    FileUtils.saveFile(out + "/major_rdf.txt", entityNquads, false);
  }

  public void initWithJson(String filePath) {
    String type = "专业";
    List<String> dictLines = new ArrayList<String>();
    List<Major> majors = new ArrayList<>();
    FileUtils.readFiles(filePath, dictLines);
    getMajor(dictLines, majors, true);
    System.out.println("get all majors :" + majors.size());
    Map<String,  List<String>> uidMap = NodeUtil.putEntity(dClient, majors);
    FileUtils.saveFile("src/main/resources/major_uid_map.txt", uidMap);
    entityIdClient.putFeedEntityWithUidNamesMap(uidMap, type);
    // NodeUtil.putEntity(dClient, getLabeledSchool(schools));
  }


  public static void main(String[] args) {
    String dict = "src/main/resources/dict/major_dict.txt";
    MajorToDgraph majorToDgraph = new MajorToDgraph();
    majorToDgraph.initWithRdf(dict, true);
  }
}

package com.higgs.dgraph;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.higgs.client.EntityIdClient;

import com.higgs.dgraph.node.Label;
import com.higgs.dgraph.node.Major;
import com.higgs.dgraph.node.NodeUtil;
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
public class MajorToDgraph {
  private static final Logger logger = LoggerFactory.getLogger(MajorToDgraph.class);
  private DClient dClient;
  private EntityIdClient entityIdClient;
  private List<Major> majors = new ArrayList<>();

  public MajorToDgraph() {
    dClient = new DClient(Config.TEST_HOSTNAME);
    entityIdClient = new EntityIdClient(Config.ENTITY_ID_HOST, Config.ENTITY_ID_SERVICE_PORT);
  }

  public MajorToDgraph(DClient dClient, EntityIdClient entityIdClient) {
    this.dClient = dClient;
    this.entityIdClient = entityIdClient;
  }

  public void getMajor(List<String> dictLines, List<Major> majors) {
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
        major.setUnique_id(name);
        major.setUnique_ids(Arrays.asList(code, name));
        names.add(name);
        if (codeInt == 0) {
          continue;
        }
        majors.add(major);
      }
    }
  }

  public void init(String dictPath) {
    List<String> dictLines = new ArrayList<String>();
    List<Major> majors = new ArrayList<Major>();
    FileUtils.readFiles(dictPath, dictLines);
    getMajor(dictLines, this.majors);
  }

  public List<Label> getLabeledMajor(List<Major> majors) {
    List<Label> labelList = new ArrayList<>();
    for (Major major : majors) {
      Label label = new Label();
      label.setUid("0x118d");
      label.setMajor(major);
      labelList.add(label);
    }
    return labelList;
  }

  public void initWithRdf(String dictPath) {
    // .. todo
    String type = "专业";
    init(dictPath);
    Map<String,  List<String>> uidMap = NodeUtil.insertEntity(dClient, majors);
    FileUtils.saveFile("src/main/resources/major_uid_map.txt", uidMap);
    entityIdClient.putFeedEntityWithUidNamesMap(uidMap, type);
  }

  /**
   * this way is better and faster than NQuad, you'd better try this muc h more.
   */
  public void initWithJson(String dictPath) {
    String type = "专业";
    List<String> dictLines = new ArrayList<String>();
    List<Major> majors = new ArrayList<Major>();
    FileUtils.readFiles(dictPath, dictLines);
    getMajor(dictLines, majors);
    System.out.println("get all majors :" + majors.size());
    Map<String,  List<String>> uidMap = NodeUtil.putEntity(dClient, majors);
    FileUtils.saveFile("src/main/resources/major_uid_map.txt", uidMap);
    entityIdClient.putFeedEntityWithUidNamesMap(uidMap, type);
    // NodeUtil.putEntity(dClient, getLabeledMajor(majors));
  }

  public void generateRDF() {
    List<String> entityNquads = NodeUtil.getEntityNquads(this.majors, new ArrayList<>());
    FileUtils.saveFile("./major_rdf.txt",  entityNquads, false);
  }

  public static void main(String[] args) {
    String dictPath = "src/main/resources/major_dict.txt";
    DClient dClient = new DClient(Config.TEST_HOSTNAME);
    EntityIdClient client = new EntityIdClient(Config.ENTITY_ID_HOST, Config.ENTITY_ID_SERVICE_PORT_TEST);
    MajorToDgraph majorToDgraph = new MajorToDgraph(dClient, client);
    // majorToDgraph.initWithJson(dictPath);
    // majorToDgraph.initWithRdf(dictPath);
    majorToDgraph.initWithRdf(dictPath);
  }
}

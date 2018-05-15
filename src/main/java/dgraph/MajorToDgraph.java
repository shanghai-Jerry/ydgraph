package dgraph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import client.EntityIdClient;
import dgraph.node.Industry;
import dgraph.node.Label;
import dgraph.node.Major;
import dgraph.node.NodeUtil;
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
public class MajorToDgraph {
  private static final Logger logger = LoggerFactory.getLogger(MajorToDgraph.class);
  private DClient dClient;
  private EntityIdClient entityIdClient;

  public MajorToDgraph() {
    dClient = new DClient(Config.TEST_HOSTNAME);
    entityIdClient = new EntityIdClient(Config.EntityId_Host, Config.EntityIdService_PORT);
  }

  public MajorToDgraph(DClient dClient) {
    this.dClient = dClient;
    entityIdClient = new EntityIdClient(Config.EntityId_Host, Config.EntityIdService_PORT);
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
      String code = lineSplits[0];
      if (!names.contains(name)) {
        major.setName(name);
        major.setCode(Integer.parseInt(code));
        major.setType("专业");
        names.add(name);
        majors.add(major);
      }
    }
  }

  public void getList(List<Major> majors, List<Major> insertList, List<Major> updateList) {
    List<List<String>> reqs = new ArrayList<List<String>>();
    Map<String, String> uidMap = new HashMap<String, String>();
    String type = "";
    for (Major entityNode : majors) {
      if ("".equals(type)) {
        type = entityNode.getType();
      }
      List<String> names = new ArrayList<String>();
      names.add(entityNode.getName());
      reqs.add(names);
    }
    entityIdClient.checkEntityList(reqs, uidMap, type);
    for (Major entityNode : majors) {
      if (uidMap.containsKey(entityNode.getName())) {
        entityNode.setUid(uidMap.get(entityNode.getName()));
        updateList.add(entityNode);
      } else {
        insertList.add(entityNode);
      }
    }
  }

  public Map<String, String> init(String dictPath, int update) {
    List<String> dictLines = new ArrayList<String>();
    List<Major> majors = new ArrayList<Major>();
    FileUtils.readFiles(dictPath, dictLines);
    getMajor(dictLines, majors);
    Map<String, String> uidMaps = new HashMap<String, String>();
    long startTime = System.currentTimeMillis();
    System.out.println("get all majors :" + majors.size());
    if (update > 0) {
      NodeUtil.updateEntity(dClient, majors);
    } else {
      uidMaps = NodeUtil.insertEntity(dClient, majors);
      // entityIdClient.putFeedEntity(uidMaps,  "学校");
    }
    long endStart = System.currentTimeMillis();
    System.out.println("spend time:" + (endStart - startTime) + " ms");
    return uidMaps;
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

  /**
   * this way is better and faster than NQuad, you'd better try this muc h more.
   */
  public void initWithJson(String dictPath, int needCheck) {
    String type = "专业";
    List<String> dictLines = new ArrayList<String>();
    List<Major> majors = new ArrayList<Major>();
    FileUtils.readFiles(dictPath, dictLines);
    getMajor(dictLines, majors);
    System.out.println("get all majors :" + majors.size());
    Map<String, String> uidMap = NodeUtil.putEntity(dClient, majors);
    FileUtils.saveFile("src/main/resources/major_uid_map.txt", uidMap);
    entityIdClient.putFeedEntity(uidMap, type);
    NodeUtil.putEntity(dClient, getLabeledMajor(majors));
  }

  public static void main(String[] args) {
    String dictPath = "src/main/resources/major_dict.txt";
    MajorToDgraph majorToDgraph = new MajorToDgraph();
    int needCheck = 0;
    majorToDgraph.initWithJson(dictPath, needCheck);
  }
}

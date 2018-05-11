package dgraph;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import client.EntityIdClient;
import dgraph.node.Industry;
import dgraph.node.Label;
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
public class IndustryToDgraph {

  private static final Logger logger = LoggerFactory.getLogger(IndustryToDgraph.class);
  private DClient dClient;
  private EntityIdClient entityIdClient;

  private List<Industry> industries = new ArrayList<>();

  public IndustryToDgraph() {
    dClient = new DClient(Config.TEST_HOSTNAME);
    entityIdClient = new EntityIdClient(Config.EntityId_Host, Config.EntityIdService_PORT);
  }

  public IndustryToDgraph(DClient dClient) {
    this.dClient = dClient;
    entityIdClient = new EntityIdClient(Config.EntityId_Host, Config.EntityIdService_PORT);
  }

  public List<Industry> getParentIndustry(List<Industry> industries) {
    List<Industry> parents = new ArrayList<>();
    for (Industry industry : industries) {
      Industry parentIndustry = industry.getParent_industry();
      parents.add(parentIndustry);
    }
    return parents;
  }


  public List<Industry> getDistinctParentIndustry(List<Industry> industries) {
    List<Industry> parents = new ArrayList<>();
    List<String> uniqueKey = new ArrayList<>();
    for (Industry industry : industries) {
      Industry parentIndustry = industry.getParent_industry();
      String name = parentIndustry.getName();
      if (!uniqueKey.contains(name)) {
        parents.add(parentIndustry);
        uniqueKey.add(name);
      }
    }
    return parents;
  }

  public void getIndustry(List<String> dictLines, List<Industry> industries) {
    String type = "行业";
    for (String line : dictLines) {
      Industry industry = new Industry();
      Industry partentIndustry = new Industry();
      String[] lineSplits = line.split("\t");
      if (lineSplits.length != 5) {
        System.out.println("line:" + line + ",line length:" + lineSplits.length);
      }
      String pName = lineSplits[0];
      String pCode = lineSplits[1];
      String name = lineSplits[2];
      String code = lineSplits[3];
      industry.setType(type);
      industry.setUnique_id(name);
      industry.setCode(Integer.parseInt(code));
      industry.setName(name);
      Label has_label = new Label();
      has_label.setUid("0x118e");
      industry.setHas_label(has_label);
      partentIndustry.setName(pName);
      partentIndustry.setType(type);
      partentIndustry.setUnique_id(pName);
      partentIndustry.setCode(Integer.parseInt(pCode));
      industry.setParent_industry(partentIndustry);
      industries.add(industry);
    }
  }

  public void initIndustry(String dictPath) {
    List<String> dictLines = new ArrayList<>();
    FileUtils.readFiles(dictPath, dictLines);
    getIndustry(dictLines, industries);
  }

  public void linkIndustry(Map<String, String> parentIndustryMap, Map<String, String> industryMap) {
    for (Industry industry : industries) {
      // check parent industry id;
      Industry industry1 = industry.getParent_industry();
      if (parentIndustryMap.containsKey(industry1.getName())) {
        industry1.setUid(parentIndustryMap.get(industry1.getName()));
      }
      if (industryMap.containsKey(industry.getName())) {
        industry.setUid(industryMap.get(industry.getName()));
      }
    }
    logger.info("industry:" + new Gson().toJson(industries.get(0)));
    // NodeUtil.addEntityEdge(dClient, industries);
  }

  public Map<String, String> initIndustry(Map<String, String> parentIndustry, int update) {
    Map<String, String> uidMaps = new HashMap<String, String>();
    if (update > 0) {
      NodeUtil.updateEntity(dClient, industries);
    } else {
      uidMaps = NodeUtil.insertEntity(dClient, industries);
    }
    return uidMaps;
  }

  public Map<String, String> initParentIndustry(int update) {
    Map<String, String> uidMaps = new HashMap<String, String>();
    logger.info("parent industries size:" + industries.size());
    List<Industry> parentsIndustry = getDistinctParentIndustry(industries);
    if (update > 0) {
      NodeUtil.updateEntity(dClient, parentsIndustry);
    } else {
      uidMaps = NodeUtil.insertEntity(dClient, parentsIndustry);
    }
    return uidMaps;
  }
  // need test
  public void initWithRdf(String dictPath, int needCheck) {
    initIndustry(dictPath);
    // 入库parentIndstry
    Map<String, String> parentMap = initParentIndustry(needCheck);
    FileUtils.saveFile("src/main/resources/parent_industry_uid_map.txt", parentMap);
    NodeUtil.putEntityUid(getParentIndustry(industries), parentMap);
    logger.info("industry:" + new Gson().toJson(industries.get(0)));
    logger.info("industry:" + new Gson().toJson(industries.get(1)));
    logger.info("industry:" + new Gson().toJson(industries.get(2)));
    logger.info("industry:" + new Gson().toJson(industries.get(3)));
    // 入库子industry 和 之前的关系
    // Map<String, String> uidMap = initIndustry(parentMap, needCheck);
    // FileUtils.saveFile("src/main/resources/industry_uid_map.txt", uidMap);
    // link entity - add edge
    // linkIndustry(parentMap, uidMap);
    // logger.info("industry:" + new Gson().toJson(industries.get(0)));
    Map<String, String> uidMap = NodeUtil.insertEntity(dClient, this.industries);
    FileUtils.saveFile("src/main/resources/industry_uid_map.txt", uidMap);
  }

  public void initWithJson(String dictPath, int needCheck) {
    String type = "行业";
    List<String> dictLines = new ArrayList<>();
    List<Industry> industries = new ArrayList<>();
    FileUtils.readFiles(dictPath, dictLines);
    getIndustry(dictLines, industries);
    logger.info("industries size:" + industries.size());
    long startTime = System.currentTimeMillis();
    List<Industry> parentsIndustry = getDistinctParentIndustry(industries);
    Map<String, String> parentUidMap = NodeUtil.putEntity(dClient, entityIdClient,
        parentsIndustry, type, needCheck);
    FileUtils.saveFile("src/main/resources/parent_industry_uid_map.txt", parentUidMap);
    NodeUtil.putEntityUid(getParentIndustry(industries), parentUidMap);
    Map<String, String> uidMap = NodeUtil.putEntity(dClient, entityIdClient, industries, type,
        needCheck);
    FileUtils.saveFile("src/main/resources/industry_uid_map.txt", uidMap);
    logger.info("industry:" + new Gson().toJson(industries.get(0)));
    long endStart = System.currentTimeMillis();
    System.out.println("spend time:" + (endStart - startTime) + " ms");
  }

  public static void main(String[] args) {
    String dict = "src/main/resources/industry_dump_dict.txt";
    int needCheck = 0;
    IndustryToDgraph industryToDgraph = new IndustryToDgraph();
    industryToDgraph.initWithJson(dict, needCheck);
  }
}

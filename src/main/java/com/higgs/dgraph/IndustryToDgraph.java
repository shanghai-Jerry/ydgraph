package com.higgs.dgraph;

import com.google.gson.Gson;


import com.higgs.client.EntityIdClient;
import com.higgs.dgraph.node.Industry;
import com.higgs.dgraph.node.Label;
import com.higgs.dgraph.node.NodeUtil;
import com.higgs.utils.FileUtils;

import java.util.ArrayList;
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
public class IndustryToDgraph {

  private static final Logger logger = LoggerFactory.getLogger(IndustryToDgraph.class);
  private DClient dClient;
  private EntityIdClient entityIdClient;

  private List<Industry> industries = new ArrayList<>();

  public IndustryToDgraph() {
    dClient = new DClient(Config.addressList);
    entityIdClient = new EntityIdClient(Config.ENTITY_ID_HOST, Config.ENTITY_ID_SERVICE_PORT);
  }

  public IndustryToDgraph(DClient dClient, EntityIdClient entityIdClient) {
    this.dClient = dClient;
    this.entityIdClient = entityIdClient;
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

  public void getIndustry(List<String> dictLines, List<Industry> industries, Boolean isRDF) {
    String type = "行业";
    for (String line : dictLines) {
      Industry industry = new Industry();
      Industry partentIndustry = new Industry();
      String[] lineSplits = line.split("\t");
      if (lineSplits.length != 4) {
        System.out.println("line:" + line + ",line length:" + lineSplits.length);
        continue;
      }
      String pName = lineSplits[0];
      String pCode = lineSplits[1];
      String name = lineSplits[2];
      String code = lineSplits[3];
      industry.setType(type);
      List<String> industryNames = new ArrayList<>();
      industryNames.add(code);
      industryNames.add(name);
      if (isRDF) {
        industry.setUnique_id(type + ":" + NodeUtil.generateEntityUniqueId(name));
      } else {
        industry.setUnique_id(name);
      }

      industry.setUnique_ids(industryNames);
      industry.setCode(Integer.parseInt(code));
      industry.setName(name);
      partentIndustry.setName(pName);
      partentIndustry.setType(type);
      List<String> parentIndustryNames = new ArrayList<>();
      parentIndustryNames.add(pName);
      parentIndustryNames.add(pCode);
      partentIndustry.setUnique_ids(parentIndustryNames);
      if (isRDF) {
        partentIndustry.setUnique_id(type + ":" + NodeUtil.generateEntityUniqueId(pName));
      } else {
        partentIndustry.setUnique_id(pName);
      }
      partentIndustry.setCode(Integer.parseInt(pCode));
      partentIndustry.setName(pName);
      industry.setParent_industry(partentIndustry);
      industries.add(industry);
    }
  }

  public void initIndustry(String dictPath, boolean isRDF) {
    List<String> dictLines = new ArrayList<>();
    FileUtils.readFiles(dictPath, dictLines);
    getIndustry(dictLines, industries, isRDF);
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

  //
  public void initWithRdf(String dictPath, boolean isRDF) {
    String type = "行业";
    initIndustry(dictPath, isRDF);
    // 入库parentIndstry
    Map<String, List<String>> parentMap = NodeUtil.insertEntity(dClient,
        getDistinctParentIndustry(industries));
    NodeUtil.putEntityUidWithNames(getParentIndustry(this.industries), parentMap);
    entityIdClient.putFeedEntityWithUidNamesMap(parentMap, type);
    // 入库子industry 和 之前的关系
    Map<String, List<String>> uidMap = NodeUtil.insertEntity(dClient, this.industries);
    entityIdClient.putFeedEntityWithUidNamesMap(uidMap, type);
  }

  public void generateRDF(String out) {
    List<String> entityNquads = NodeUtil.getEntityNquads(this.industries, new ArrayList<>());
    FileUtils.saveFile(out + "/industry_rdf.txt", entityNquads, false);
  }

  public List<Label> getLabeledIndustry(List<Industry> industries) {
    List<Label> labelList = new ArrayList<>();
    for (Industry industry : industries) {
      Label label = new Label();
      label.setUid("0x118e");
      label.setLabel_name("行业类型");
      label.setIndustry(industry);
      labelList.add(label);
    }
    return labelList;
  }

  public void initWithJson(String dictPath) {
    String type = "行业";
    List<String> dictLines = new ArrayList<>();
    List<Industry> industries = new ArrayList<>();
    FileUtils.readFiles(dictPath, dictLines);
    getIndustry(dictLines, industries, true);
    logger.info("industries size:" + industries.size());
    long startTime = System.currentTimeMillis();
    List<Industry> parentsIndustry = getDistinctParentIndustry(industries);
    Map<String, List<String>> parentUidMap = NodeUtil.putEntity(dClient, parentsIndustry);
    entityIdClient.putFeedEntityWithUidNamesMap(parentUidMap, type);
    FileUtils.saveFile("src/main/resources/parent_industry_uid_map.txt", parentUidMap);
    Map<String, List<String>> uidMap = NodeUtil.putEntity(dClient, industries);
    FileUtils.saveFile("src/main/resources/industry_uid_map.txt", uidMap);
    entityIdClient.putFeedEntityWithUidNamesMap(uidMap, type);
    logger.info("industry:" + new Gson().toJson(industries.get(0)));
    Map<String, List<String>> labelMap = NodeUtil.putEntity(dClient, getLabeledIndustry(industries));
    FileUtils.saveFile("src/main/resources/industry_label_uid_map.txt", labelMap);
    long endStart = System.currentTimeMillis();

    System.out.println("spend time:" + (endStart - startTime) + " ms");
  }

  public static void main(String[] args) {
    if (args.length < 1) {
      System.err.println("Usage : <Industry_dict_path>");
      System.exit(-1);
    }
    String dict = args[0];
    IndustryToDgraph industryToDgraph = new IndustryToDgraph();
    industryToDgraph.initWithRdf(dict, true);
  }
}

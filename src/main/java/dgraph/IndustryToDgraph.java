package dgraph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import client.EntityIdClient;
import com.google.gson.Gson;
import dgraph.node.Industry;
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

  public IndustryToDgraph() {
    dClient = new DClient(Config.TEST_HOSTNAME);
    entityIdClient = new EntityIdClient(Config.EntityId_Host, Config.EntityIdService_PORT);
  }

  public List<Industry> getParentIndustry(List<Industry> industries) {
    List<Industry> parents = new ArrayList<>();
    for (Industry industry: industries) {
      Industry parentIndustry  = industry.getParent_industry();
      parents.add(parentIndustry);
    }
    return parents;
  }


  public List<Industry> getDistinctParentIndustry(List<Industry> industries) {
    List<Industry> parents = new ArrayList<>();
    List<String> uniqueKey = new ArrayList<>();
    for (Industry industry: industries) {
      Industry parentIndustry  = industry.getParent_industry();
      String name = parentIndustry.getName();
      if (!uniqueKey.contains(name)) {
        parents.add(parentIndustry);
        uniqueKey.add(name);
      }
    }
    return parents;
  }

  public void getIndustry(List<String> dictLines, List<Industry> industries) {
    for (String line : dictLines) {
      List<String> pNames = new ArrayList<String>();
      List<String> names = new ArrayList<>();
      Industry industry = new Industry();
      Industry partentIndustry = new Industry();
      String[] lineSplits = line.split("\t");
      if (lineSplits.length != 5) {
        System.out.println("line:" + line + ",line length:" + lineSplits.length);
      }
      String pName = lineSplits[0];
      String pCode = lineSplits[1];
      pNames.add(pName);
      pNames.add(pCode);
      String name = lineSplits[2];
      String code = lineSplits[3];
      names.add(name);
      names.add(code);
      industry.setNames(names);
      industry.setCode(Integer.parseInt(code));
      industry.setName(name);
      partentIndustry.setName(pName);
      partentIndustry.setCode(Integer.parseInt(pCode));
      partentIndustry.setNames(pNames);
      industry.setParent_industry(partentIndustry);
      industries.add(industry);
    }
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
    Map<String, String> parentUidMap = NodeUtil.putEntity(dClient, entityIdClient, parentsIndustry,
        type, needCheck);
    NodeUtil.putEntityUid(getParentIndustry(industries), parentUidMap);
    NodeUtil.putEntity(dClient, entityIdClient, industries, type, needCheck);
    logger.info("industry:" + new Gson().toJson(industries.get(0)));
    long endStart = System.currentTimeMillis();
    System.out.println("spend time:" + (endStart - startTime) + " ms");
  }

  public static  void main(String []args) {
    String dict = "src/main/resources/industry_dump_dict.txt";
    int needCheck = 0;
    IndustryToDgraph industryToDgraph = new IndustryToDgraph();
    industryToDgraph.initWithJson(dict, needCheck);
  }
}

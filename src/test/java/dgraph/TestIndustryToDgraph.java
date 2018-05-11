package dgraph;

import java.util.Map;

import utils.FileUtils;

/**
 * User: JerryYou
 *
 * Date: 2018-05-09
 *
 * Copyright (c) 2018 devops
 *
 * <<licensetext>>
 */
public class TestIndustryToDgraph {

  public static void main(String[] args) {
    String dict = "src/main/resources/industry_dump_dict.txt";
    int needCheck = 0;
    IndustryToDgraph industryToDgraph = new IndustryToDgraph(new DClient(Config.TEST_VM_HOSTNAME));

    industryToDgraph.init(dict);
    // 入库parentIndstry
    Map<String, String> parentMap = industryToDgraph.initParentIndustry(needCheck);
    FileUtils.saveFile("src/main/resources/parent_industry_uid_map.txt", parentMap);
    // 入库子industry 和 之前的关系
    Map<String, String> uidMap = industryToDgraph.initIndustry(parentMap, needCheck);
    FileUtils.saveFile("src/main/resources/industry_uid_map.txt", uidMap);
    // link entity
    industryToDgraph.linkIndustry(parentMap, uidMap);
  }

}

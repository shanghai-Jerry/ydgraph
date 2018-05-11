package dgraph;

import java.util.Map;

import javax.xml.soap.Node;

import dgraph.node.NodeUtil;
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

  IndustryToDgraph industryToDgraph = new IndustryToDgraph(new DClient(Config.TEST_VM_HOSTNAME));

  public void test_one(String dict, int needCheck) {
    industryToDgraph.initWithRdf(dict, needCheck);
  }

  public void test_two(int needCheck) {
  }

  public static void main(String[] args) {
    TestIndustryToDgraph testIndustryToDgraph = new TestIndustryToDgraph();
    String dict = "src/main/resources/industry_dump_dict.txt";
    int needCheck = 0;
    testIndustryToDgraph.test_one(dict, needCheck);

  }

}

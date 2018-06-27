package com.higgs.dgraph;

import com.higgs.client.EntityIdClient;

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

  IndustryToDgraph industryToDgraph = new IndustryToDgraph(new DClient(Config.TEST_VM_HOSTNAME),
      new EntityIdClient(Config.ENTITY_ID_HOST, Config.ENTITY_ID_SERVICE_PORT_TEST));

  public void test_one(String dict, int needCheck) {
    industryToDgraph.initWithRdf(dict);
  }
  public static void main(String[] args) {
    TestIndustryToDgraph testIndustryToDgraph = new TestIndustryToDgraph();
    String dict = "src/main/resources/industry_dump_dict.txt";
    int needCheck = 0;
    testIndustryToDgraph.test_one(dict, needCheck);

  }

}

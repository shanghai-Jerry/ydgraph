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
public class TestMajorToDgraph {

  public static void main(String[] args) {
    String dictPath = "src/main/resources/major_dict.txt";
    MajorToDgraph majorToDgraph = new MajorToDgraph(new DClient(Config.TEST_VM_HOSTNAME),
        new EntityIdClient(Config.ENTITY_ID_HOST, Config.ENTITY_ID_SERVICE_PORT_TEST));
    int update = 0;
    majorToDgraph.init(dictPath);
    // FileUtils.saveFile("src/main/resources/final_major_uid_map.txt", uidMaps);
  }
}



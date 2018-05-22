package dgraph;

import java.util.List;
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
public class TestMajorToDgraph {

  public static void main(String[] args) {
    String dictPath = "src/main/resources/major_dict.txt";
    MajorToDgraph majorToDgraph = new MajorToDgraph(new DClient(Config.TEST_VM_HOSTNAME));
    int update = 0;
    majorToDgraph.init(dictPath);
    // FileUtils.saveFile("src/main/resources/final_major_uid_map.txt", uidMaps);
  }
}



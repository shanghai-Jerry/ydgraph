package com.higgs.dgraph;

import com.higgs.dgraph.node.DeptName;
import com.higgs.dgraph.node.NodeUtil;
import com.higgs.utils.FileUtils;

import java.util.ArrayList;
import java.util.List;

import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

/**
 * User: JerryYou
 *
 * Date: 2018-06-25
 *
 * Copyright (c) 2018 devops
 *
 * <<licensetext>>
 */
public class TestDeptName {

  private static final Logger logger = LoggerFactory.getLogger(TestDeptName.class);

  DClient dClient = new DClient(Config.TEST_VM_HOSTNAME);

  private void insertDeptName(String dir) {
    List<String> dict = new ArrayList<>();
    List<DeptName> deptNameList = new ArrayList<>();
    FileUtils.readFile(dir, dict);
    int batch = 0;
    for (String line : dict) {
      String [] ls  = line.split("\t");
      if (ls.length != 2) {
        logger.info("Error split:" + line);
        continue;
      }
      JsonObject jsonObject = new JsonObject(ls[0]);
      DeptName deptName = new DeptName();
      String company = jsonObject.getString("normedName", "");
      String dept = jsonObject.getString("deptName", "");
      deptName.setUnique_id(company);
      deptName.setName(dept);
      deptNameList.add(deptName);
      batch++;
      if (batch >= 50) {
        NodeUtil.insertEntity(dClient, deptNameList);
        deptNameList.clear();
        batch = 0;
      }
    }

  }

  public static void main(String[] args) {
    String path = args[0];

  }
}

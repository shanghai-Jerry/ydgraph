package com.higgs.etl;

import com.higgs.utils.FileUtils;

import java.util.ArrayList;
import java.util.List;

import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

/**
 * User: JerryYou
 *
 * Date: 2018-06-28
 *
 * Copyright (c) 2018 devops
 *
 * <<licensetext>>
 */
public class JobDict {
  private static Logger logger = LoggerFactory.getLogger(JobDict.class);
  public static void main(String[] arg) {

    String file = "/Users/devops/Documents/职位归一化/job_dict_180628.txt";
    int count = 0;
    List<String> dict = new ArrayList<>();
    FileUtils.readFile(file, dict);
    for (String line : dict) {
      String[] sp = line.split("\t");
      if (sp.length == 5) {
        count++;
      } else {
        logger.info("length error::" + line);
      }
    }
    logger.info("count:" + count);

  }
}

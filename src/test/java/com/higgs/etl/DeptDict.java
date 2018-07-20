package com.higgs.etl;

import com.higgs.utils.FileUtils;
import com.lieluobo.kaka.KakaTokenizer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

/**
 * User: JerryYou
 *
 * Date: 2018-07-20
 *
 * Copyright (c) 2018 devops
 *
 * <<licensetext>>
 */
public class DeptDict {

  // bazel build kaka/proto:libtokenizer.so
  static {
    System.load("/Users/devops/workspace/gitlab/dept_norm/libtokenizer.so");
  }
  private KakaTokenizer tokenizer = null;

  public KakaTokenizer getTokenizer() {
    return tokenizer;
  }

  public void setTokenizer(KakaTokenizer tokenizer) {
    this.tokenizer = tokenizer;
  }

  public void deptNameSegment(String filePath) {

  }

  public void gatheringDeptDict() {
    String dept = "/Users/devops/Documents/知识图谱/dept_name_resume.txt";
    List<String> dict = new ArrayList<>();
    FileUtils.readFile(dept, dict);
    Set<String> keys = new HashSet<>();
    List<String> result = new ArrayList<>();
    List<String> filter = Arrays.asList("分公司", "办公室", "集团", "公司", "其他", "兼职", "初中", "未填写", "元/月");
    for (String line : dict) {
      boolean needF = false;
      String []sp = line.split("\\u0001");
      if (sp.length == 2) {
        String key = sp[0];
        for (String f : filter) {
          if (key.contains(f)) {
            needF = true;
            break;
          }
        }
        if (needF) {
          continue;
        }
        int frq = Integer.parseInt(sp[1]);
        if (frq >= 1000) {
          String formatKey = key.toLowerCase()
              .replace("部", "")
              .replace("部门", "")
              .replace("中心", "")
              .replace("科", "")
              .replace("门","")
              .replace("一", "")
              .replace("二", "")
              .trim();
          if (formatKey.length() >=2) {
            keys.add(formatKey);
          }
        }
      }
    }
    int index = 0;
    for (String key : keys) {
      List<String> diff = Arrays.asList("办", "处", "院");
      boolean need = true;
      for(String end : diff) {
        if (key.endsWith(end)) {
          need = false;
        }
      }
      if (need) {
        String rep = index + "\t" + key + "部" + "\t" + key;
        result.add(rep);
      } else {
        String rep = index + "\t" + key + "\t" + key;
        result.add(rep);
      }
      index++;
    }
    FileUtils.saveFile("/Users/devops/Documents/部门归一化/dept_dict_enhance_s.txt", result, false);
  }
  public static void main(String[] arg) {
    Logger logger = LoggerFactory.getLogger(DeptDict.class);
    DeptDict deptDict = new DeptDict();
    KakaTokenizer.initConfig("/var/local/kakaseg/conf.json");
    deptDict.setTokenizer(KakaTokenizer.newInstance());
    ArrayList<String> segs = deptDict.tokenizer.tokenizeString("战略投资部门", false);
    for (String seg : segs)  {
      logger.info("seg => ");
      logger.info(seg);
    }
  }
}

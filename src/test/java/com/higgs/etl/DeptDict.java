package com.higgs.etl;

import com.higgs.utils.FileUtils;
import com.higgs.utils.Util;
import com.lieluobo.kaka.KakaTokenizer;
import com.lieluobo.kaka.WordT;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import io.vertx.core.json.JsonObject;
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

  Logger logger = LoggerFactory.getLogger(DeptDict.class);

  // bazel build kaka/proto:libtokenizer.so
  // In Mac: use java -Djava.library.path=/data/dept_norm  -jar xxx.jar has problem
  // but In Linux, it's ok
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

  private String filePath = "src/main/resources/dict/dept_name_resume.txt";

  public void gatheringDeptDict() {
    String dept = "src/main/resources/dict/dept_name_resume.txt";
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
    FileUtils.saveFile("src/main/resources/dict/dept_dict_enhance_s.txt", result, false);
  }

  Pattern pattern = Pattern.compile("^\\d++$");
  private boolean checkSuffix(String seg) {
    List<String> suffixs = Arrays.asList("部门", "部");
    // List<String> suffix = Arrays.asList("办", "处", "院", "部门", "部", "中心", "科", "门");
    String finalSeg = seg;
    for (String suffix : suffixs) {
      if (finalSeg.endsWith(suffix)) {
         finalSeg = finalSeg.replace(suffix, "");
      }
    }
    if (finalSeg.length() <= 1) {
      return false;
    }
    return true && !pattern.matcher(finalSeg).find();
  }

  public String deleteSuffix(String key) {
    List<String> suffixs = Arrays.asList("部门", "部");
    String finalSeg = key;
    for (String suffix : suffixs) {
      if (finalSeg.endsWith(suffix)) {
        finalSeg = finalSeg.replace(suffix, "");
      }
    }
    return finalSeg;
  }

  public void deptNameSegment() {
    List<String> dict = new ArrayList<>();
    FileUtils.readFile(this.filePath, dict);
    Map<String, Integer> dictMap = new HashMap<>();
    for (String line : dict) {
      String []sp = line.split("\\u0001");
      if (sp.length == 2) {
        String key = sp[0].toLowerCase();
        int frq = Integer.parseInt(sp[1]);
        if (frq >= 200) {
          // keys.add(line);
          List<String> segs = this.tokenizer.tokenizeString(key, false);
          for (String seg : segs) {
            if (!checkSuffix(seg)) {
              continue;
            }
            seg = deleteSuffix(seg);
            if (dictMap.containsKey(seg)) {
              int number = dictMap.get(seg);
              dictMap.put(seg, number + frq);
            } else {
              dictMap.put(seg, frq);
            }
          }
        }
      } else  {
        logger.info("error length => " + line);
      }
    }
    Map<String, Integer> sorted = Util.sortMapByValue(dictMap);
    FileUtils.saveFileWith("src/main/resources/dict/dept_dict_enhance.txt", sorted);
    generateDict(sorted);
  }

  private void generateDict(Map<String, Integer> map) {
    List<String> dict = new ArrayList<>();
    List<String> suffix = Arrays.asList("办", "处", "院", "中心", "会", "科", "室", "层", "部");
    Set<Map.Entry<String, Integer>> entrySet = map.entrySet();
    Iterator<Map.Entry<String, Integer>> iterator = entrySet.iterator();
    while (iterator.hasNext()) {
      Map.Entry<String, Integer> entry = iterator.next();
      String key = entry.getKey();
      String value = entry.getValue().toString();
      boolean needAddSuffix = true;
      for (String s : suffix) {
        if (key.endsWith(s)) {
          needAddSuffix = false;
          break;
        }
      }
      if (needAddSuffix) {
        dict.add(value + "\t" + key + "部" + "\t" + key);
      } else {
        dict.add(value + "\t" + key + "\t" + key);
      }
    }
    FileUtils.saveFiles("src/main/resources/dict/dept_dict.txt", dict);
  }

  private String splitor(List<String> keys, String longestKey) {
    Set<String> set = new HashSet<>(keys);
    List<String> distintKeys = new ArrayList<>(set);
    StringBuilder stringBuilder = new StringBuilder();
    boolean isInit = false;
    if (!distintKeys.contains(longestKey)) {
      stringBuilder.append(longestKey);
      isInit = true;
    }
    int length = distintKeys.size();
    for (int i = 0; i < length; i++) {
      if (!isInit) {
        stringBuilder.append(distintKeys.get(i));
      } else {
        stringBuilder.append(" " + distintKeys.get(i));
      }
    }
    return stringBuilder.toString();
  }

  private void getDeptDupKeyNames() {
    String file = "/Users/devops/workspace/gitlab/dept_norm/dept_dict.txt";
    List<String> dict = new ArrayList<>();
    FileUtils.readFile(file, dict);
    Map<String, String> map = new HashMap<>();
    List<String> finalDict = new ArrayList<>();
    for (String line : dict) {
      String [] sp = line.split("\t");
      String dept = sp[2];
      String[] keys = dept.split(" ");
      for (String key : keys) {
        String finalKey = "200" + "\t" + key + "部\t" + key;
        if (map.containsKey(key) && !finalDict.contains(finalKey)) {
          finalDict.add(finalKey);
        } else {
          map.put(key, key);
        }
      }
    }
    FileUtils.saveFiles("/Users/devops/workspace/gitlab/dept_norm/dept_dict_dupkey.txt", finalDict);
  }

  private void getDeptKeyNames() {
    String file = "/Users/devops/workspace/gitlab/dept_norm/dept_dict_combine.txt";
    List<String> dict = new ArrayList<>();
    FileUtils.readFile(file, dict);
    List<String> distinct = new ArrayList<>();
    List<String> finalDict = new ArrayList<>();
    for (String line : dict) {
      String [] sp = line.split("\t");
      String dept = sp[1];
      if (distinct.contains(dept)) {
        continue;
      }
      distinct.add(dept);
      String frq = sp[0];
      String longestKey = dept.replace("部门", "").replace("部", "");
      List<String> keys = this.tokenizer.tokenizeString(longestKey, true);
      finalDict.add(frq + "\t" + dept+ "\t" + splitor(keys, longestKey));
    }
    FileUtils.saveFiles("/Users/devops/workspace/gitlab/dept_norm/dept_dict.txt", finalDict);
  }

  public void sortWithPosition(ArrayList<WordT> segs) {
    Collections.sort(segs, (a, b) -> -(a.getStart() - b.getStart()));
  }

  public void kakaSegment() {

    List<String> segs = this.tokenizer.tokenizeString("总经理办公室", false);
    logger.info("seg => " + segs);
    ArrayList<WordT> segsWithPos = this.tokenizer.tokenizeWithPosition("战略投资管理部门");
    logger.info("segsWithPos => " + segsWithPos);
  }
  public static void main(String[] arg) {
    Logger logger = LoggerFactory.getLogger(DeptDict.class);
    DeptDict deptDict = new DeptDict();
    KakaTokenizer.initConfig("/var/local/kakaseg/conf.json");
    deptDict.setTokenizer(KakaTokenizer.newInstance());
    // deptDict.deptNameSegment();
    // deptDict.kakaSegment();
    // deptDict.getDeptKeyNames();
    // deptDict.getDeptDupKeyNames();
  }
}

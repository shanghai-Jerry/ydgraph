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

  HashMap<String, String> map = new HashMap<>();
  HashMap<String, String> mappingMap = new HashMap<>();

  public void setTokenizer(KakaTokenizer tokenizer) {
    this.tokenizer = tokenizer;
  }

  private String filePath = "src/main/resources/dict/dept_name_resume.txt";

  private void init(String dictPath, String mappingDict) {
    List<String> dict = new ArrayList<>();
    List<String> mapping = new ArrayList<>();
    FileUtils.readFile(dictPath, dict);
    FileUtils.readFile(mappingDict, mapping);
    int dupCount = 0;
    for(String line : dict) {
      String []sp = line.split("\t");
      if (sp.length != 3) {
        logger.info("line length not equal 3 => " + line);
        continue;
      }
      String match = sp[1];
      String []items = sp[2].split(" ");
      int itemIndex = 0;
      for (String item : items) {
        if (!"".equals(item)) {
          if (map.containsKey(item)) {
            if (itemIndex == 0) {
              map.put(item, match);
            } else {
              // logger.info("this key is contained: " + item + " => " + map.get(item));
              dupCount = dupCount + 1;
            }
          } else {
            map.put(item, match);
          }
        }
        itemIndex = itemIndex + 1;
      }
    }
    // for mapping
    for (String line : mapping) {
      String []sp = line.split("\t");
      if (sp.length != 2) {
        logger.info("mapping line length not equal 2 => " + line);
        continue;
      }
      String org = sp[0];
      String normed = sp[1];
      if (mappingMap.containsKey(org)) {
        // logger.info("this dept is contained, real mapping: " + org + " => " + mappingMap.get
        // (org));
      } else {
        mappingMap.put(org, normed);
      }
    }
    logger.info("dup key: " + dupCount);
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
        isInit = true;
      } else {
        stringBuilder.append(" " + distintKeys.get(i));
      }
    }
    return stringBuilder.toString();
  }


  private void getKeyMappingMap() {
    String file = "src/main/resources/dict/dept_norm/track/dept_dict_20180806.txt";
    Map<String, List<String>> map = new HashMap<>();
    List<String> dict = new ArrayList<>();
    FileUtils.readFile(file, dict);
    List<String> finalDict = new ArrayList<>();
    List<String> disDict = new ArrayList<>();
    for (String line : dict) {
      String[] sp = line.split("\t");
      String dept = sp[1].trim();
      String key = sp[3].trim();
      if (disDict.contains(dept)) {
        continue;
      }
      disDict.add(dept);
      finalDict.add(dept + "\t" + key);
    }
    FileUtils.saveFiles("src/main/resources/dict/dept_norm/final/dept_dict_mapping_v2.txt",
        finalDict);
  }

  private void combineMapingBasicV2() {
    String file = "src/main/resources/dict/dept_norm/final/dept_dict_mapping_v2.txt";
    String v1 = "src/main/resources/dict/dept_norm/final/dept_dict_v1.txt";
    List<String> dict = new ArrayList<>();
    FileUtils.readFile(file, dict);
    List<String> finalDict = new ArrayList<>();
    List<String> disDict = new ArrayList<>();
    for (String line : dict) {
      String[] sp = line.split("\t");
      String dept = sp[0].trim();
      String key = sp[1].trim();
      if (disDict.contains(dept)) {
        continue;
      }
      disDict.add(dept);
      // finalDict.add(dept + "\t" + key);
    }
    dict.clear();
    FileUtils.readFile(v1, dict);
    int count = 0;
    for (String line : dict) {
      String[] sp = line.split("\t");
      String dept = sp[1].trim();
      String key = sp[1].trim();
      if (disDict.contains(dept)) {
        finalDict.add(line);
        continue;
      }
      logger.info("dont contain in v2:" + line);
      count++;
    }
    logger.info("count:" + count);
    FileUtils.saveFiles("src/main/resources/dict/dept_norm/final/dept_dict_v2.txt",finalDict);
  }

  public boolean filteKey(String name) {
    List<String> keys = Arrays.asList("事业", "计划", "发展", "系统");
    if (keys.contains(name)) {
      return true;
    }
    return false;
  }

  public void genreateOnlyOneDictMap() {
    String file = "src/main/resources/dict/dept_norm/final/dept_dict_mapping_v2.txt";
    String v1 = "src/main/resources/dict/dept_norm/final/dept_dict_v2.txt";
    List<String> dict = new ArrayList<>();
    FileUtils.readFile(file, dict);
    List<String> finalDict = new ArrayList<>();
    Map<String, String> map = new HashMap<>();
    Map<String, List<String>> dictMap = new HashMap<>();
    List<String> disDict = new ArrayList<>();
    for (String line : dict) {
      String[] sp = line.split("\t");
      String dept = sp[0].trim();
      String key = sp[1].trim();
      if (disDict.contains(dept)) {
        continue;
      }
      disDict.add(dept);
      map.put(dept, key);
      // finalDict.add(dept + "\t" + key);
    }
    dict.clear();
    FileUtils.readFile(v1, dict);
    int count = 0;
    for (String line : dict) {
      String[] sp = line.split("\t");
      String dept = sp[1].trim();
      String key = sp[2].trim();
      if (map.containsKey(dept)) {
        String v = map.get(dept);
        if (dictMap.containsKey(v)) {
          List<String> v2 = dictMap.get(v);
          Set<String> hash = new HashSet<>(v2);
          for (String item : key.split(" ")) {
            if (filteKey(item)) {
              continue;
            }
            hash.add(item);
          }
          dictMap.put(v, new ArrayList<>(hash));
        } else {
          List<String> v2 = new ArrayList<>();
          Set<String> hash = new HashSet<>(v2);
          for (String item : key.split(" ")) {
            if (filteKey(item)) {
              continue;
            }
            hash.add(item);
          }
          dictMap.put(v, new ArrayList<>(hash));
        }
      } else {
        //
        if (dictMap.containsKey(dept)) {
          List<String> v2 = dictMap.get(dept);
          Set<String> hash = new HashSet<>(v2);
          for (String item : key.split(" ")) {
            if (filteKey(item)) {
              continue;
            }
            hash.add(item);
          }
          dictMap.put(dept, new ArrayList<>(hash));
        } else {
          List<String> v2 = new ArrayList<>();
          Set<String> hash = new HashSet<>(v2);
          for (String item : key.split(" ")) {
            if (filteKey(item)) {
              continue;
            }
            hash.add(item);
          }
          dictMap.put(dept, new ArrayList<>(hash));
        }
      }
      count++;
    }
    logger.info("count:" + count);
    FileUtils.saveFile("src/main/resources/dict/dept_norm/final/dept_dict.txt",dictMap);

  }


  public void sortWithPosition(ArrayList<WordT> segs) {
    Collections.sort(segs, (a, b) -> -(a.getStart() - b.getStart()));
  }

  public void kakaSegment() {

    List<String> segs = this.tokenizer.tokenizeString("董事长部", false);
    logger.info("seg => " + segs);
    ArrayList<WordT> segsWithPos = this.tokenizer.tokenizeWithPosition("战略投资管理部门");
    logger.info("segsWithPos => " + segsWithPos);
  }
  public static void main(String[] arg) {
    Logger logger = LoggerFactory.getLogger(DeptDict.class);
    DeptDict deptDict = new DeptDict();
    KakaTokenizer.initConfig("/var/local/kakaseg/conf.json");
    deptDict.setTokenizer(KakaTokenizer.newInstance());
    deptDict.kakaSegment();
    // deptDict.getKeyMappingMap();
    // deptDict.combineMapingBasicV2();
    deptDict.genreateOnlyOneDictMap();
    // deptDict.init("/Users/devops/workspace/hbase-demo/src/main/resources/dict/dept_norm/final/dept_dict_v1.txt","/Users/devops/workspace/hbase-demo/src/main/resources/dict/dept_norm/final/dept_dict_mapping_v1.txt");
  }
}

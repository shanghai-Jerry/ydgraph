package com.higgs.dgraph.kb_system;

import com.csvreader.CsvReader;
import com.higgs.dgraph.kb_system.variable.Variable;
import com.higgs.utils.FileUtils;

import org.apache.commons.collections.map.HashedMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * User: JerryYou
 *
 * Date: 2019-08-30
 *
 * Copyright (c) 2018 devops
 *
 * <<licensetext>>
 */
public class EntityTypeAttributeFormat {

  static Logger logger = LoggerFactory.getLogger(EntityTypeAttributeFormat.class);

  public String valuesFormat(String [] values) {
    return  "\"" + values[0] +"\","+ "\"" + values[1] +"\"," + "\"" + values[2] +"\","+"\"" +
        values[3] +"\"";
  }

  public void convertEmbedding(String src, String entitys,String des, boolean isName) {
    List<String> dict = new ArrayList<>();
    Map<String, String> idMap = new HashedMap();
    FileUtils.readFile(entitys, dict);
    int errorCount = 0;
    for (String item : dict) {
      int index = item.indexOf(",");
      if (index == -1) {
        errorCount ++;
        continue;
      }
      String key = item.substring(0, index);
      String value = item.substring(index + 1);
      if (!idMap.containsKey(key)) {
        idMap.put(key, value);
      } else  {
        logger.info("duplicate key => " + key);
      }
    }
    logger.info("errorCount  => " + errorCount);
    dict.clear();
    errorCount = 0;
    List<String> allList = new ArrayList<>();
    FileUtils.readFile(src, dict);

    for (String item : dict) {
      item = item.replaceAll("\t", " ");
      int index = item.indexOf(" ");
      if (index == -1) {
        errorCount ++;
        continue;
      }
      String key = item.substring(0, index);
      String value = item.substring(index + " ".length());
      if (idMap.containsKey(key)) {
        // 将实体中的空格替换掉
        String ret;
        if (isName) {
          ret = idMap.get(key).replaceAll(" ", ",");
        } else {
          ret = key;
        }
        allList.add(ret + " " + value);
      } else  {
        allList.add(item);
      }
    }
    logger.info("errorCount  => " + errorCount);
    logger.info("total:" + dict.size() + ", get:" + allList.size());
    FileUtils.saveFile(des, allList, true);
  }

  public boolean filterRelType(int type) {
    List<Integer> filters = Arrays.asList(31,19,10);
    if (filters.contains(type)) {
      return false;
    }
    return true;
  }

  public void convertRelationMapping(String src, String entitys,String des) {
    List<String> dict = new ArrayList<>();
    Map<String, String> idMap = new HashedMap();
    FileUtils.readFile(entitys, dict);
    int errorCount = 0;
    for (String item : dict) {
      int index = item.indexOf(",");
      if (index == -1) {
        errorCount ++;
        continue;
      }
      String value = item.substring(0, index);
      String key = item.substring(index + 1);
      if (!idMap.containsKey(key)) {
        idMap.put(key, value);
      } else  {
        logger.info("duplicate key => " + key);
      }
    }
    logger.info("errorCount  => " + errorCount);
    List<String> all_relations = new ArrayList<>();
    try {
      CsvReader csvReader = new CsvReader(KbParseData.getReader(src) );
      while(csvReader.readRecord()) {
        String [] values = csvReader.getValues();
        if (values.length != 4) {
          continue;
        }
        String lhs = values[0];
        String rhs = values[1];
        int type = Integer.parseInt(values[2]);
        if (idMap.containsKey(lhs) && idMap.containsKey(rhs) && filterRelType(type)) {
          all_relations.add(idMap.get(lhs) + "\t" + Variable.relationPairs.get(type).getOutRel()
              + "\t" + idMap.get(rhs));
          // reverse relations but keep relation type
          all_relations.add(idMap.get(rhs) + "\t" + Variable.relationPairs.get(type).getOutRel()
              + "\t" + idMap.get(lhs));
        }
      }
      logger.info("totalSize  => " + all_relations.size());
    } catch (FileNotFoundException e) {
      logger.info("[NoFile] => " + e.getMessage());
    } catch (IOException e) {
      logger.info("[IOExp] => " + e.getMessage());
    }
    FileUtils.saveFile(des, all_relations, true);

  }

  public void convertRelationPaths(String path, String entitys, String file) {
    List<String> dict = new ArrayList<>();
    Map<String, String> idMap = new HashedMap();
    FileUtils.readFile(entitys, dict);
    int errorCount = 0;
    for (String item : dict) {
      int index = item.indexOf(",");
      if (index == -1) {
        errorCount ++;
        continue;
      }
      String value = item.substring(0, index);
      String key = item.substring(index + 1);
      if (!idMap.containsKey(key)) {
        idMap.put(key, value);
      } else  {
        logger.info("duplicate key => " + key);
      }
    }
    logger.info("errorCount  => " + errorCount);
    // 关系input
    List<List<String>> relations = new ArrayList<>();
    List<String> all_relations = new ArrayList<>();

    try {
      CsvReader csvReader = new CsvReader(KbParseData.getReader(path + "/" + file) );
      for (int i = 0; i  <= 42; i++) {
        relations.add(new ArrayList<>());
      }
      while(csvReader.readRecord()) {
        String [] values = csvReader.getValues();
        if (values.length != 4) {
          continue;
        }
        int type = Integer.parseInt(values[2]);
        String lhs = values[0];
        String rhs = values[1];
        List<String> relationsIndex = relations.get(type);
        if (idMap.containsKey(lhs) && idMap.containsKey(rhs) && filterRelType(type)) {
          String one = idMap.get(lhs) + "\t" + Variable.relationPairs.get(type).getOutRel()
              + "\t" + idMap.get(rhs);
          // reverse relations but keep relation type
          String reverseOne = idMap.get(rhs) + "\t" + Variable.relationPairs.get(type).getOutRel()
              + "\t" + idMap.get(lhs);
          all_relations.add(one);
          all_relations.add(reverseOne);
          relationsIndex.add(one);
          relationsIndex.add(reverseOne);
          relations.set(type,relationsIndex);
        }
      }
    } catch (FileNotFoundException e) {
      logger.info("[NoFile] =>" + e.getMessage());
    } catch (IOException e) {
      logger.info("[IOExp] =>" + e.getMessage());
    }
    for (int i = 0; i  <= 42; i++) {
      String dir = path + "/edge_" + String.valueOf(i);
      new File(dir).mkdir();
      FileUtils.saveFile( dir +"/relation_" + String.valueOf(i) + ".csv",
          relations.get(i), false);
    }

  }

  public void convertRelation(String path, String file) {
    // 关系input
    List<List<String>> relations = new ArrayList<>();
    List<String> all_relations = new ArrayList<>();
    Map<String, Boolean> distinct = new HashedMap();

    try {
      CsvReader csvReader = new CsvReader(KbParseData.getReader(path + "/" + file) );
      for (int i = 0; i  <= 42; i++) {
        relations.add(new ArrayList<>());
      }
      while(csvReader.readRecord()) {
        String [] values = csvReader.getValues();
        if (values.length != 4) {
          continue;
        }
        int type = Integer.parseInt(values[2]);
        List<String> relationsIndex = relations.get(type);
        String key = Variable.getRelVarValue(values[2], values[0], values[1]);
        String key2 = Variable.getRelVarValue(values[2], values[1], values[0]);
        if (!distinct.containsKey(key)) {
          relationsIndex.add(valuesFormat(values));
          relations.set(type,relationsIndex);
          distinct.put(key, true);
          distinct.put(key2, true);
          all_relations.add(valuesFormat(values));
        }
      }
    } catch (FileNotFoundException e) {
      logger.info("[NoFile] =>" + e.getMessage());
    } catch (IOException e) {
      logger.info("[IOExp] =>" + e.getMessage());
    }
    /*for (int i = 0; i  <= 42; i++) {
      FileUtils.saveFileToCsv(path + "/relation_" + String.valueOf(i) + ".csv", relations.get(i),
          false);
    }*/
    FileUtils.saveFileToCsv(path + "/relation_format.csv", all_relations, false);

  }

  public void convertEntityType(String path, String outPut) {
    Map<String, List<String>> typeMap = new HashMap<>();
    try {
      CsvReader csvReader = new CsvReader(KbParseData.getReader(path));
      while(csvReader.readRecord()) {
        String [] values = csvReader.getValues();
        if (values.length != 2) {
          continue;
        }
        String name = values[0];
        String type = values[1];
        List<String> types =  typeMap.getOrDefault(name, new ArrayList<>());
        types.add(Variable.entityTypeList.get(Integer.parseInt(type)));
        typeMap.put(name, types);
      }
    } catch (FileNotFoundException e) {
      logger.info("[NoFile] =>" + e.getMessage());
    } catch (IOException e) {
      logger.info("[IOExp] =>" + e.getMessage());
    }
    Set<Map.Entry<String, List<String>>> entrySet = typeMap.entrySet();
    Iterator<Map.Entry<String, List<String>>> iterator = entrySet.iterator();
    List<String> typesList = new ArrayList<>();
    while (iterator.hasNext()) {
      Map.Entry<String, List<String>> entry = iterator.next();
      String key = entry.getKey();
      List<String> values = entry.getValue();
      int i = 0;
      StringBuilder stringBuilder = new StringBuilder();
      for (String value : values) {
        if (i == 0) {
          stringBuilder.append(value);
        } else {
          stringBuilder.append("," + value);
        }
        i++;
      }
      typesList.add("\"" + key + "\",\"" + stringBuilder.toString() +"\",\"1\"");
    }
    FileUtils.saveFileToCsv(outPut, typesList, false);
  }

  public void entityFilterCorpType(String path, String filterPath, String outPut) {
    Set<String> corpTypes = new HashSet<>();
    try {
      CsvReader csvReader = new CsvReader(KbParseData.getReader(filterPath));
      while(csvReader.readRecord()) {
        String [] values = csvReader.getValues();
        if (values.length != 2) {
          continue;
        }
        String id = values[0];
        String name = values[1].toLowerCase();
        corpTypes.add(name);
      }
    } catch (FileNotFoundException e) {
      logger.info("[NoFile] =>" + e.getMessage());
    } catch (IOException e) {
      logger.info("[IOExp] =>" + e.getMessage());
    }
    List<String> entities = new ArrayList<>();
    try {
      CsvReader csvReader = new CsvReader(KbParseData.getReader(path));
      while(csvReader.readRecord()) {
        String [] values = csvReader.getValues();
        if (values.length != 2) {
          continue;
        }
        String id = values[0];
        String name = values[1].toLowerCase();
        if (!corpTypes.contains(name)) {
          entities.add("\"" + id + "\",\"" + name +"\"");
        }
      }
    } catch (FileNotFoundException e) {
      logger.info("[NoFile] =>" + e.getMessage());
    } catch (IOException e) {
      logger.info("[IOExp] =>" + e.getMessage());
    }
    FileUtils.saveFileToCsv(outPut, entities, false);
  }

  public void convertEmbedding(String vDir) {

    convertEmbedding(
        "/Users/devops/workspace/kb/kb_system/entity_embeddings.tsv",
        "/Users/devops/workspace/kb/kb_system/kb_entity.csv",
        "/Users/devops/workspace/kb/kb_system/"+vDir+"/entity_name_embeddings_format.tsv", true);
    convertEmbedding(
        "/Users/devops/workspace/kb/kb_system/entity_embeddings.tsv",
        "/Users/devops/workspace/kb/kb_system/kb_entity.csv",
        "/Users/devops/workspace/kb/kb_system/"+vDir+"/entity_id_embeddings_format.tsv", false);
  }

  public void convertRelationMapping() {
    convertRelationMapping(
        "/Users/devops/workspace/kb/kb_system/kb_relation_1567074526188.txt",
        "/Users/devops/workspace/kb/kb_system/kb_entity.csv",
        "/Users/devops/workspace/kb/kb_system/kb_relation_mapping.txt"
    );
  }

  public static void main(String[] args) {
    EntityTypeAttributeFormat entityTypeAttributeFormat = new EntityTypeAttributeFormat();
    // entityTypeAttributeFormat.convertRelationMapping();
    entityTypeAttributeFormat.convertEmbedding("v200");
    // entityTypeAttributeFormat.convertRelationPaths("/Users/devops/workspace/kb/kb_system","/Users/devops/workspace/kb/kb_system/kb_entity.csv","kb_relation_1567074526188.txt");

  }
}

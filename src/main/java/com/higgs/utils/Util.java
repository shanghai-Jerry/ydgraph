package com.higgs.utils;


import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import io.dgraph.DgraphProto;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

public class Util {

  /**
   * 使用 Map按value进行排序
   * @param oriMap
   * @return
   */
  public static Map<String, Integer> sortMapByValue(Map<String, Integer> oriMap) {
    if (oriMap == null || oriMap.isEmpty()) {
      return null;
    }
    Map<String, Integer> sortedMap = new LinkedHashMap<>();
    List<Map.Entry<String, Integer>> entryList = new ArrayList<>(
        oriMap.entrySet());
    Collections.sort(entryList, (o1, o2) -> -o1.getValue().compareTo(o2.getValue()));
    Iterator<Map.Entry<String, Integer>> iter = entryList.iterator();
    while (iter.hasNext()) {
      Map.Entry<String, Integer> tmpEntry = iter.next();
      sortedMap.put(tmpEntry.getKey(), tmpEntry.getValue());
    }
    return sortedMap;
  }

  private static Logger logger = LoggerFactory.getLogger(Util.class);

  public static void parseLatency(DgraphProto.Response res) {
    long processTime = res.getLatency().getProcessingNs();
    Util.println("latency:", res.getLatency().toString());
    logger.info("query took: \n" + TimeUtil.consumeTime(processTime / 1000/ 1000));
  }

  public static  void formatPrintJson(String json) {
    ObjectMapper mapper = new ObjectMapper();
    try {
      Object obj = mapper.readValue(json, Object.class);
      logger.info("json:\n" + mapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
  public static boolean checkPredicateValue(Object predicate) {
    if(predicate == null) {
      return false;
    }
    if (predicate instanceof Integer || predicate instanceof Long) {
      long value = Long.valueOf(predicate.toString());
      if (value == 0) {
        return false;
      }
    } else if (predicate instanceof String) {
      if ("".equals(predicate)) {
        return false;
      }
    } else if (predicate instanceof Double || predicate instanceof Float) {
      double value = Double.valueOf(predicate.toString());
      if (value == 0) {
        return false;
      }
    } else if (predicate instanceof Boolean) {
      return true;
    }

    return true;
  }
  public static void println(String key, Object object) {
    System.out.println(key + ":" + object);
  }
}

package com.higgs.utils;


import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

import io.dgraph.DgraphProto;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

public class Util {

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

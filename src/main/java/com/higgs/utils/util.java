package com.higgs.utils;


public class util {
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

package com.bigchange.algorithm.leetcode.problems;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * User: JerryYou
 *
 * Date: 2019-11-04
 *
 * Copyright (c) 2018 devops
 *
 * <<licensetext>>
 */
public class CalculateEntropy {

  public double calculateEntropy(int[] input) {
    int total = input.length;
    Map<Integer, Integer> countMap = new HashMap<>();
    double entropy = 0.0;
    for (int i = 0; i < total; i++) {
      int key = input[i];
      int value = countMap.getOrDefault(key, 0);
      countMap.put(key, value+1);
    }
    Set<Map.Entry<Integer, Integer>> entrySet = countMap.entrySet();
    Iterator<Map.Entry<Integer, Integer>> iterable =  entrySet.iterator();
    while (iterable.hasNext()) {
      Map.Entry<Integer, Integer> entry = iterable.next();
      int key = entry.getKey();
      int value = entry.getValue();
      double p = value * 1.0 / (total * 1.0);
      // log 换底公式
      entropy += - p * Math.log(p) / Math.log(2);
    }
    return entropy;
  }

}

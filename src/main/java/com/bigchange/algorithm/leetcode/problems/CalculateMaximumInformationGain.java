package com.bigchange.algorithm.leetcode.problems;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
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
public class CalculateMaximumInformationGain {

  public class PetalCombine {
    double value;
    String specie;

    public PetalCombine(double value, String specie) {
      this.value = value;
      this.specie = specie;
    }
  }

  public double calculate(List<PetalCombine> input) {
    int total = input.size();
    Map<String, Integer> countMap = new HashMap<>();
    double entropy = 0.0;
    for (int i = 0; i < total; i++) {
      String key = input.get(i).specie;
      int value = countMap.getOrDefault(key, 0);
      countMap.put(key, value+1);
    }
    Set<Map.Entry<String, Integer>> entrySet = countMap.entrySet();
    Iterator<Map.Entry<String, Integer>> iterable =  entrySet.iterator();
    while (iterable.hasNext()) {
      Map.Entry<String, Integer> entry = iterable.next();
      int value = entry.getValue();
      double p = value * 1.0 / (total * 1.0);
      // log 换底公式
      entropy += - p * Math.log(p) / Math.log(2);
    }
    return entropy;
  }


  public double calculateMaxInfoGain(List<Double> petal_length, List<String> species) {
    double ret = 0.0;
    int length = petal_length.size();
    List<PetalCombine> newPetalList = new ArrayList<>();
    for (int i = 0; i< petal_length.size(); i++) {
      newPetalList.add(new PetalCombine(petal_length.get(i), species.get(i)));
    }
    double g = calculate(newPetalList);
    // 先按petal_length排序，然后一个个划分分别计算infoGain
    Collections.sort(newPetalList, (o1, o2) -> o1.value < o2.value?1: -1);
    // how to split accoring to petal_length;
    for (int i = 1; i < length; i++) {
      double g1 = calculate(newPetalList.subList(0, i));
      double g2 = calculate(newPetalList.subList(i, length));
      double infoGain = g - g1 * (i * 1.0 / (length * 1.0)) - g2 * ( (length - i)* 1.0 / (length *
          1.0));
      if (infoGain > ret) {
        ret = infoGain;
      }
    }
    return  ret;
  }
}

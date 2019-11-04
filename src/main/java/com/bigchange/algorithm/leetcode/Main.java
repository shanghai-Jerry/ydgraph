package com.bigchange.algorithm.leetcode;

import com.bigchange.algorithm.leetcode.problems.CalculateMaximumInformationGain;
import com.higgs.utils.Util;

import java.util.Arrays;
import java.util.List;

/**
 * User: JerryYou
 *
 * Date: 2019-11-04
 *
 * Copyright (c) 2018 devops
 *
 * <<licensetext>>
 */
public class Main {

  public static  void main(String []args) {
    CalculateMaximumInformationGain calculateMaximumInformationGain = new
        CalculateMaximumInformationGain();
    List<Double> petal_length = Arrays.asList(0.5,2.3,1.0,1.5);
    List<String> species = Arrays.asList("setosa","versicolor","setosa","versicolor");
    double ret = calculateMaximumInformationGain.calculateMaxInfoGain(petal_length, species);
    Util.println("ret", ret);
  }
}

package com.bigchange.algorithm.leetcode.problems;

import java.util.HashMap;

/**
 * User: JerryYou
 *
 * Date: 2019-05-31
 *
 * Copyright (c) 2018 devops
 *
 * <<licensetext>>
 */
public class PerfectSquares {

  HashMap<Integer, Boolean> numbers = new HashMap<>();
  public int numSquares(int n) {

    int maxNumber = (int)Math.sqrt(n);
    int maxValue = (int)Math.pow(maxNumber, 2);

    return  maxNumber;

  }

}

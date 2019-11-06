package com.bigchange.algorithm.leetcode;

import com.bigchange.algorithm.leetcode.problems.SingleNumber136;
import com.higgs.utils.Util;

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
    SingleNumber136 singleNumber = new SingleNumber136();
    int [] nums = new int[]{4,1,2,1,2};
    int ret = singleNumber.singleNumber(nums);
    Util.println("ret", ret);
  }
}

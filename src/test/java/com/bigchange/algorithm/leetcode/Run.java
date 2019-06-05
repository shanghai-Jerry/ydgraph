package com.bigchange.algorithm.leetcode;


import com.bigchange.algorithm.leetcode.problems.TargetSum494;

/**
 * User: JerryYou
 *
 * Date: 2019-05-08
 *
 * Copyright (c) 2018 devops
 *
 * <<licensetext>>
 */
public  class Run {

  public static void println(Object o) {
    System.out.println(o);
  }

  public static void main(String[] args) {

    Run.println(new TargetSum494().findTargetSumWays(new int[]{1,1,1,1,1},3));

  }
}
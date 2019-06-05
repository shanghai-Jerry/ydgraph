package com.bigchange.algorithm.leetcode;


import com.bigchange.algorithm.leetcode.problems.PerfectSquares279;

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

    Run.println(new PerfectSquares279().numSquares(7));
  }
}

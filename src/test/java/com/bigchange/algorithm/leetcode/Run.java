package com.bigchange.algorithm.leetcode;


import com.bigchange.algorithm.leetcode.problems.P01Matrix;

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

    Run.println(new P01Matrix().updateMatrix(new int[][]{
        {0,0,1,0,1,1,1,0,1,1},
        {1,1,1,1,0,1,1,1,1,1}
    }));

  }
}
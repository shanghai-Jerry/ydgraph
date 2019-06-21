package com.bigchange.algorithm.leetcode;


import com.bigchange.algorithm.leetcode.problems.GenerateParentheses22;

import java.util.List;

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

    List<String> ret =  new GenerateParentheses22().generateParenthesis_backtracking(3);
    for (String item : ret) {
      System.out.println(item);
    }
  }
}


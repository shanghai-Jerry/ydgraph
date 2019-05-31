package com.bigchange.algorithm.leetcode;


import com.bigchange.algorithm.leetcode.problems.OpentheLock752;

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

    Run.println(new OpentheLock752().openLock(new String[]{"0201","0101","0102","1212","2002"},
        "0202"));
  }
}

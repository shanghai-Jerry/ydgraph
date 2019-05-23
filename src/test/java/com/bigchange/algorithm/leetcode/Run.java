package com.bigchange.algorithm.leetcode;


/**
 * User: JerryYou
 *
 * Date: 2019-05-08
 *
 * Copyright (c) 2018 devops
 *
 * <<licensetext>>
 */
public class Run {

  public void println(Object o) {
    System.out.println(o);
  }

  public static void main(String[] args) {
    Run run = new Run();
    run.println(new LastStoneWeightII1049().lastStoneWeightII(new int[]{2,7,4,1,8,1}));

  }
}

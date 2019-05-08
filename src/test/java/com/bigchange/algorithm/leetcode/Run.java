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
    PascalTriangle119 pascalTriangle119 = new PascalTriangle119();
    run.println(pascalTriangle119.generate(5));
  }
}

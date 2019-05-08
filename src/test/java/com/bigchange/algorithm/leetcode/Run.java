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
    PascalTriangle pascalTriangle = new PascalTriangle();
    run.println(pascalTriangle.generate(5));
  }
}

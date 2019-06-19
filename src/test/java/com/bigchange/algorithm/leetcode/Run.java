package com.bigchange.algorithm.leetcode;


import com.bigchange.algorithm.leetcode.problems.SudokuSolver37;

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
    String [] custom_input  = new String[]{
        "53..7....",
        "6..195...",
        ".98....6.",
        "8...6...3",
        "4..8.3..1",
        "7...2...6",
        ".6....28.",
        "...419..5",
        "....8..79" };
    new SudokuSolver37().setInputString(custom_input);
  }
}
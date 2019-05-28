package com.bigchange.algorithm.leetcode.problems;

/**
 * User: JerryYou
 *
 * Date: 2019-05-08
 *
 * Copyright (c) 2018 devops
 *
 * <<licensetext>>
 */
public class PowXn50 {

  public double myPow(double x, int n) {

    // return  Math.pow(x, n);

    if(n<0)
      return 1/x * myPow(1/x, -(n+1));
    if(n==0)
      return 1;
    if(n==2)
      return x*x;
    if(n%2==0)
      return myPow(myPow(x, n/2), 2);
    else
      return x*myPow( myPow(x, n/2), 2);
  }
}

package com.bigchange.algorithm.leetcode.problems;

import java.util.HashMap;

/**
 * User: JerryYou
 *
 * Date: 2019-05-31
 *
 * Copyright (c) 2018 devops
 *
 * <<licensetext>>
 */
public class PerfectSquares279 {

  // recursive is not efficient, dp is a good solution.
  public int numSquares(int n) {
    if(n == 0) {
      return 0;
    }
    Integer min = n;
    for(int i = 1; i <= Math.sqrt(n); i++) {
      int temp = numSquares(n - i*i);
      if(temp < min)
        min = temp;
    }
    return min+1;
  }


  // not iterator by h(i) + h(n - i) but h[n- i * i]  otherwise it's not efficient
  public int numSquares_dp(int n) {
    int [] h = new int[n+1];
    h[1] = 1;

    for (int i = 2; i <= n; i++) {
      int index = (int)Math.sqrt(i);
      int tmp = i;
      for (int j = index; j >=1; j--) {
        int v =  h[i - j * j];
        if (tmp > v) {
          tmp = v;
        }
      }
      h[i] = tmp + 1;
    }
    return  h[n];

  }

}

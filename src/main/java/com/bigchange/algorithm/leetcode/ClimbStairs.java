package com.bigchange.algorithm.leetcode;

import java.util.HashMap;
import java.util.Map;

/**
 * User: JerryYou
 *
 * Date: 2019-05-08
 *
 * Copyright (c) 2018 devops
 *
 * <<licensetext>>
 *
 *   You are climbing a stair case. It takes n steps to reach to the top.
 *
 *   Each time you can either climb 1 or 2 steps. In how many distinct ways can you climb to the
 *   top?
 */
public class ClimbStairs {

  // Memoization
  Map<Integer, Integer> hashTable = new HashMap<>();

  public int climbStairs(int n) {
    if (hashTable.containsKey(n)) {
      return hashTable.get(n);
    }
    int res;
    if (n < 2) {
      return n;
    } else if (n == 2) {
      return  2;
    } else {
      res = climbStairs(n-1) + climbStairs(n-2);
      hashTable.put(n, res);
    }
    return  res;
  }

  // Dynamic Programming
  public int climbStairsWithDP(int n) {
    if (n == 1) {
      return 1;
    }
    int[] dp = new int[n + 1];
    dp[1] = 1;
    dp[2] = 2;
    for (int i = 3; i <= n; i++) {
      dp[i] = dp[i - 1] + dp[i - 2];
    }
    return dp[n];
  }

  public int climbStairsOptSpace(int n) {
    if (n == 1) {
      return 1;
    }
    int first = 1;
    int second = 2;
    for (int i = 3; i <= n; i++) {
      int third = first + second;
      first = second;
      second = third;
    }
    return second;
  }
}

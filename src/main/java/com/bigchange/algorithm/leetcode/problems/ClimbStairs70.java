package com.bigchange.algorithm.leetcode.problems;

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
public class ClimbStairs70 {

  // Memoization
  Map<Integer, Integer> hashTable = new HashMap<>();

  // 递归：自顶向下的求解
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

  // 自底向上的求解过程： 边界， 最优子结构(就是往后退一步，能够得到的最好的结果)， 状态转移方程
  // Dynamic Programming
  public int climbStairsWithDP(int n) {
    if (n == 1) {
      return 1;
    }
    int[] dp = new int[n + 1];
    dp[1] = 1;
    dp[2] = 2;
    int a = 1;
    int b = 2;
    int temp = 0;
    if (n ==1)
      temp = a;
    else if (n== 2)
      temp=b;
    for (int i = 3; i <= n; i++) {
      temp = a + b;
      a = b;
      b = temp;
      dp[i] = dp[i - 1] + dp[i - 2];
    }
    // return temp // 可降低
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

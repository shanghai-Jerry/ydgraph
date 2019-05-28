package com.bigchange.algorithm.leetcode.problems;

/**
 * User: JerryYou
 *
 * Date: 2019-05-20
 *
 * Copyright (c) 2018 devops
 *
 * <<licensetext>>
 */
public class LastStoneWeightII1049 {

  // split the array into two groups, each groups has the min sum, then
  // minimumAnswer = Math.min(minimumAnswer , Math.abs(bSum - aSum))
  // bSum = total - aSum
  //  minimumAnswer = Math.min(minimumAnswer , Math.abs(total -aSum - aSum))
  public int lastStoneWeightII(int[] stones) {
    int MAX_SUM = 3000;
    // think about MAX_SUM just be 3000 or less, may be 1500, why ?
    int minimumAnswer = Integer.MAX_VALUE;
    int total = 0;
    Boolean[] dp = new Boolean[MAX_SUM];
    // dp[x] = true, means sum = x exist
    dp[0] = true;
    for (int i = 1; i < MAX_SUM; i++) {
      dp[i] = false;
    }
    for(int i = 0; i < stones.length; i++) {
      int w = stones[i];
      total += w;
      for (int j = MAX_SUM - 1; j>= w; j--) {
        // dp[j - w ] exist then dp[i]
        dp[j] = dp[j] | dp[j - w];
      }
    }
    // each possible aSum
    // usually way to get each aSum
    // only asum is much big then minimumAnswer is smaller enough
    for (int i = MAX_SUM - 1; i > 0 ; i--) {
      // sum i exist, then
      if (dp[i]) {
        int aSum = i;
        minimumAnswer = Math.min(minimumAnswer, Math.abs(total - aSum - aSum));
      }
    }
    return minimumAnswer;
  }
}

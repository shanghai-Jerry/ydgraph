package com.bigchange.algorithm.leetcode.problems;

/**
 * User: JerryYou
 *
 * Date: 2019-06-05
 *
 * Copyright (c) 2018 devops
 *
 * <<licensetext>>
 */
public class TargetSum494 {

  public int ways(int []nums, int i, int S, int operation, int count, int wayNumer) {
    // +
    if (operation == 0) {
      count = count + nums[i];
    } else { // -
      count = count - nums[i];
    }
    if (i + 1 >= nums.length) {
      if (count == S) {
        wayNumer++;
      }
      return wayNumer;
    } else {
      wayNumer = ways(nums, i + 1, S, 0, count, wayNumer);
      wayNumer = ways(nums, i + 1 , S, 1, count, wayNumer);
    }

    return wayNumer;
  }

  // 291ms
  public int findTargetSumWays_recursion(int[] nums, int S) {
    int count = 0;
    int waysNumber = 0;
    waysNumber = ways(nums, 0, S, 0, count, waysNumber);
    waysNumber = ways(nums, 0, S, 1, count, waysNumber);
    return  waysNumber;

  }

  // dp : 2ms
  /*
   This problem is equal to "find all ways to partition the array into two groups so that the difference of the sum of the two groups is Math.abs(S)".
   Assume the sum of the two subsets is s1 and s2 (s1 >= s2), the sum of the array is sum:
    s1 + s2 = sum;
    s1 - s2 = Math.abs(S)
    So, s1 = (sum + Math.abs(S)) / 2. Now what we need to do is finding all subsets whose sum is s1, which is a typical 0-1 knapsack problem.
   */
  public int findTargetSumWaysInDP(int[] nums, int S) {
    int sum = 0;
    S = Math.abs(S);
    for(int i = 0; i < nums.length; i++)
      sum += nums[i];
    // Invalid S, just return 0
    if( S > sum || (sum + S) % 2 != 0 )
      return 0;
    int dp[] = new int[(sum + S) / 2 + 1];
    dp[0] = 1;
    for(int i = 0; i < nums.length; i++) {
      // 计算所有可能的s1, 通过每次增加一个num[i]时更新可能的和j，最后得到和为s1的可能性
      for(int j = dp.length - 1; j >= nums[i]; j--) {
        dp[j] += dp[j - nums[i]];
      }
    }
    return dp[dp.length - 1];
  }

  // dp solution: 128 ms
  public int findTargetSumWays(int[] nums, int S) {
    int waysNumber = 0;
    int total = (int)(Math.pow(2,nums.length));
    int [] condition = new int[total];
    for (int i = 0;i < nums.length; i++) {
      int  item = nums[i];
      int number = (int)(Math.pow(2, i));
      for (int j = 0; j < number; j ++) {
        int tmp = condition[j];
        condition[j] = tmp + item;
        condition[j + number] = tmp - item;
        if (i == nums.length - 1) {
          if (tmp + item == S) {
            waysNumber ++;
          }
          if (tmp - item == S){
            waysNumber ++;
          }
        }
      }
    }
    return waysNumber;
  }
}

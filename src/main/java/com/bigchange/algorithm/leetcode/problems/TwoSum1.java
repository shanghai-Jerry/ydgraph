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
 *   Given nums = [2, 7, 11, 15], target = 9,
 *
 *   Because nums[0] + nums[1] = 2 + 7 = 9,
 *
 *   return [0, 1].
 *
 *   exactly one solution
 */

public class TwoSum1 {

  public int[] twoSum(int[] nums, int target) {
    // two element in array nums add equal target, return indices of these two element
    int[] result = new int[2];
    Map<Integer, Integer> hashTable = new HashMap<>();
    for (int i = 0; i< nums.length; i++) {
      int remain = target - nums[i];
      if (hashTable.containsKey(remain)) {
        result[0] = hashTable.get(remain);
        result[1] = i;
        return result;
      } else {
        hashTable.put(nums[i], i);
      }
    }
    return  result;
  }
}

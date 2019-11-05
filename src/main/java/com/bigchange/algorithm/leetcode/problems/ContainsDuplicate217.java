package com.bigchange.algorithm.leetcode.problems;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * User: JerryYou
 *
 * Date: 2019-11-05
 *
 * Copyright (c) 2018 devops
 *
 * <<licensetext>>
 */
public class ContainsDuplicate217 {

  // Time: O(n), Space: O(N)
  public boolean containsDuplicate(int[] nums) {
    // return quickWithSort(nums)
    Set<Integer> hashSet = new HashSet<>();
    for (int  i = 0; i < nums.length; i++) {
      int key = nums[i];
      if (hashSet.contains(key)) {
        return true;
      }
      hashSet.add(key);
    }
    return false;
  }

  // Time: O(nlogn), Space: O(1)
  private boolean quickWithSort(int []nums) {
    Arrays.sort(nums);
    for (int  i = 0; i < nums.length; i++) {
      if  (nums[i] == nums[i+1]) {
        return true;
      }
    }
    return  false;
  }
}

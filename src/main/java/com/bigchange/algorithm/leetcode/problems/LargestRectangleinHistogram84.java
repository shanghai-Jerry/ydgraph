package com.bigchange.algorithm.leetcode.problems;

/**
 * User: JerryYou
 *
 * Date: 2019-06-24
 *
 * Copyright (c) 2018 devops
 *
 * <<licensetext>>
 */
public class LargestRectangleinHistogram84 {

  // of course: time complexity is high, any efficient way?
  public int largestRectangleArea_custom(int[] heights) {
    int ret = 0;
    int min = 0;
    int sum = 0;
    for (int i = 0; i < heights.length; i++) {
      for (int j = i; j < heights.length; j++) {
        if (i == j) {
          min = heights[j];
        }
        int add_item = heights[j];
        if (add_item < min) {
          min = add_item;
        }
        sum = (j - i + 1) * min;
        if (ret < sum) {
          ret = sum;
        }
      }
    }
    return ret;
  }

}

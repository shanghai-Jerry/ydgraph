package com.bigchange.algorithm.leetcode.problems;

import java.util.Arrays;

/**
 * User: JerryYou
 *
 * Date: 2019-11-06
 *
 * Copyright (c) 2018 devops
 *
 * <<licensetext>>
 */
public class SingleNumber136 {

  public int singleNumber(int[] nums) {
    int bitwiseOperator = 0;
    for (int i = 0; i < nums.length; i++) {
      // 异或操作， 两数相同相互抵消
      // a⊕a=0, a⊕0=a, a⊕b⊕a=(a⊕a)⊕b=0⊕b=b
      bitwiseOperator = bitwiseOperator ^ nums[i];
    }
    return bitwiseOperator;
  }
  public int singleNumberWithSort(int[] nums) {
    Arrays.sort(nums);
    int len = nums.length;
    int pos = nums[0];
    int i = 1;
    while(i < len - 1) {
      if (pos ==  nums[i]) {
        pos = nums[i + 1];
        i = i + 2;
      } else {
        return  pos;
      }
    }
    return pos;
  }
}

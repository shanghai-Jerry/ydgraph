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
public class LastStoneWeight1046 {

  class Find {
    int value = 0;
    int index = -1;
    Find(int v, int i) {
      value = v;
      index = i;
    }
  }

  public Find findMaxValue(int [] stones, int []visit) {
    int maxValue = 0;
    int index = -1;
    for (int i = 0; i < stones.length; i++) {
      if (visit[i] == 1) {
          continue;
      }
      if (stones[i] > maxValue) {
        maxValue = stones[i];
        index = i;
      }
    }
    return new Find(maxValue, index);
  }
  public int lastStoneWeight(int[] stones) {
    int [] visit = new int[stones.length];
    while (true) {
      Find max = findMaxValue(stones, visit);
      if (max.index == -1) {
        return 0;
      }
      visit[max.index] = 1;

      Find sec_max = findMaxValue(stones, visit);
      if (sec_max.index == -1) {
        return max.value;
      }

      if (max.value == sec_max.value) {
        visit[max.index] = 1;
        visit[sec_max.index] = 1;
        stones[max.index] = 0;
        stones[sec_max.index] = 0;
      } else if (max.value > sec_max.value) {
        visit[max.index] = 0;
        visit[sec_max.index] = 1;
        stones[max.index] = max.value - sec_max.value;
        stones[sec_max.index] = 0;
      } else {
        visit[max.index] = 1;
        visit[sec_max.index] = 0;
        stones[max.index] = 0;
        stones[sec_max.index] = max.value - sec_max.value;;
      }
    }
  }
}

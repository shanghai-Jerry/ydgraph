package com.bigchange.algorithm.leetcode.problems;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

/**
 * User: JerryYou
 *
 * Date: 2019-06-04
 *
 * Copyright (c) 2018 devops
 *
 * <<licensetext>>
 */
public class DailyTemperatures739 {

  // stack的妙用
  public int[] dailyTemperatures(int[] T) {
    Deque<Integer> stack = new ArrayDeque<>();
    int[] ans = new int[T.length];
    for (int i = T.length - 1; i >= 0; i--) {
      while (stack.size() > 0 && T[stack.peek()] <= T[i])  {
        stack.pop();
      }
      ans[i] = stack.size() > 0 ? stack.peek() - i : 0;
      stack.push(i);
    }
    return ans;
  }

  // 15ms
  public int[] dailyTemperatures_1(int[] T) {
    int[] wait = new int[T.length];
    List<Integer> values = new ArrayList();
    for (int i = T.length -1 ; i >= 0; i--) {
      if (values.size() > 0) {
        for (int j = values.size() - 1; j >=0 ; j--) {
          if (T[values.get(j)] <= T[i]) {
            values.remove(j);
          }
        }
      }
      wait[i] = values.size() > 0 ? values.get(values.size() - 1) - i : 0;
      values.add(i);
    }
    return  wait;
  }

}

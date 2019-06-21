package com.bigchange.algorithm.leetcode.problems;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User: JerryYou
 *
 * Date: 2019-06-21
 *
 * Copyright (c) 2018 devops
 *
 * <<licensetext>>
 */
public class GenerateParentheses22 {

  class Pair {
    // 下标位置
    int index;
    // ( 的数量
    int num;

    public Pair(int index, int num) {
      this.index = index;
      this.num = num;
    }
  }
  // backtracking
  public List<String> generateParenthesis_backtracking(int n) {
    List<String> result = new ArrayList<>();
    if (n < 1)
      return result;
    // 注意：stack 在backtracking中需要记录的东西，在此时前面已有了多少个(
    ArrayDeque<Pair> candidates = new ArrayDeque<>();
    int i = 0, num = 0;
    // 可能的结果存在str中
    char [] str = new char[2 * n];
    while(true) {
      if (num == 0) {
        str[i++] = '(';
        num++;
      } else {
        // 最后一位
        if (i == (2*n-1)) {
          if (num == 1) {
            str[i] = ')';
            result.add(String.valueOf(str));
          }
          if (candidates.isEmpty())
            break;
          Pair rt = candidates.poll();
          i = rt.index;
          num = rt.num;
          while (num > n && !candidates.isEmpty()) {
            rt = candidates.poll();
            i = rt.index;
            num = rt.num;
          }
          str[i++] = '(';
          num++;
        } else {
          candidates.push(new Pair(i, num));
          str[i++] = ')';
          num--;
        }
      }
    }
    return result;
  }

  Map<Integer, List<String>> map = new HashMap<>();

  public List<String> generateParenthesis(int n) {
    map.put(1, Arrays.asList("()"));
    map.put(2, Arrays.asList("()()", "(())"));
    for (int i = 3; i <= n; i++) {
      List<String> answers = new ArrayList<>();
      for (int j = 1;  j <= i / 2 ;j++) {
        List<String> left_b = map.get(j);
        List<String> right = map.get(i - j);
        if (i - 1 == i - j) {
          for (String rh : right) {
            String tmp = "(" + rh + ")";
            if (!answers.contains(tmp)) {
              answers.add(tmp);
            }
            for (String lf : left_b) {
              String tmp_2 = lf + rh;
              String tmp_rev = rh + lf;
              if (!answers.contains(tmp_2)) {
                answers.add(tmp_2);
              }
              if (!answers.contains(tmp_rev)) {
                answers.add(tmp_rev);
              }
            }
          }
        } else {
          for (String lf : left_b) {
            for (String rh : right) {
              String tmp = lf + rh;
              String tmp_rev = rh + lf;
              if (!answers.contains(tmp)) {
                answers.add(tmp);
              }
              if (!answers.contains(tmp_rev)) {
                answers.add(tmp_rev);
              }
            }
          }
        }

      }
      map.put(i, answers);
    }
    return map.get(n);
  }

}

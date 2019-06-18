package com.bigchange.algorithm.hackerrank.problems;

import java.util.ArrayList;
import java.util.List;

public class BusStation {

  // Complete the solve function below.
  static int[] solve(int[] a) {
    int max = 0;
    int sum = 0;
    for (int i = 0; i < a.length; i++) {
      if (a[i] > max) {
        max = a[i];
      }
      sum += a[i];
    }

    boolean[] dp = new boolean[sum + 1];
    dp[0] = true;
    dp[sum] = true;
    int total_count = 0;
    // 按顺序计算可能的组合人数和
    for (int i = a.length - 1; i >= 0; i--) {
      int ai = a[i];
      total_count += ai;
      dp[total_count] = true;
    }
    // 先确定可能需要的最多公交车数量, 车不允许多
    int  num;
    if (sum % max == 0) {
      num = sum / max;
    } else {
      num = sum / max + 1;
    }
    List<Integer> carNums = new ArrayList<>();
    // 针对每个公交车数量, 得到每批应该装下的人gap，第k辆车装满人数后的dp[total + k * gap]是否都满足存在
    for (int k = 1; k <= num; k++) {
      int total;
      if (sum % k == 0) {
        int gap = sum / k;
        total = gap;
        boolean isTrue = true;
        while (total != sum) {
          if (dp[total]) {
            total += gap;
          } else {
            isTrue = false;
            break;
          }
        }
        if (isTrue) {
          carNums.add(k);
        }
      } else {
        continue;
      }
    }
    int [] ret = new int[carNums.size()];
    int index = carNums.size() - 1;
    for (Integer car : carNums) {
      ret[index] = sum/car;
      index--;
    }
    return ret;

  }



}
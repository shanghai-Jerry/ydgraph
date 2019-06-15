package com.bigchange.algorithm.hackerrank.problems;

import java.util.ArrayList;
import java.util.List;

public class BusStation {

  // Complete the solve function below.
  static int[] solve(int[] a) {
    int max = 0;
    int sum = 0;
    for (int i = 0; i< a.length; i++) {
      if (a[i] > max) {
        max = a[i];
      }
      sum += a[i];
    }

    boolean[] dp = new boolean[sum + 1];
    dp[0] = true;
    dp[sum] = true;
    int total_count = 0;
    for (int i = a.length - 1; i >= 0; i--) {
      int ai = a[i];
      total_count += ai;
      dp[total_count] = true;
    }
    int num = sum / max + 1;
    // System.out.println("sum:" + sum + ",num:" + num);
    List<Integer> carNums = new ArrayList<>();
    for (int k = 1; k <= num; k++) {
      int total;
      if (sum % k == 0) {
        int gap = sum / k;
        total = gap;
        boolean isTrue = true;
        while (total != sum) {
          // System.out.println("dp:" + total + "," + dp[total]);
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
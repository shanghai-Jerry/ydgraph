package com.bigchange.algorithm.hackerrank.problems;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class BusStation {

  private static final Scanner scanner = new Scanner(System.in);

  public static void main(String[] args) throws IOException {

    System.setProperty("OUTPUT_PATH", "./out_put.txt");

    BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(System.getProperty("OUTPUT_PATH")));

    int aCount = 100000;
    // scanner.skip("(\r\n|[\n\r\u2028\u2029\u0085])?");

    int[] a = new int[aCount];

    // String[] aItems = scanner.nextLine().split(" ");
    // scanner.skip("(\r\n|[\n\r\u2028\u2029\u0085])?");

    for (int aItr = 0; aItr < aCount; aItr++) {
      int aItem = Integer.parseInt("10000");
      a[aItr] = aItem;
    }

    int[] result = solve(a);

    for (int resultItr = 0; resultItr < result.length; resultItr++) {
      bufferedWriter.write(String.valueOf(result[resultItr]));

      if (resultItr != result.length - 1) {
        bufferedWriter.write(" ");
      }
    }

    bufferedWriter.newLine();

    bufferedWriter.close();

    scanner.close();
  }


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
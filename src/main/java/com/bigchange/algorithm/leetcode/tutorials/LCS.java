package com.bigchange.algorithm.leetcode.tutorials;

import static java.lang.Math.max;

/**
 * User: JerryYou
 *
 * Date: 2019-05-08
 *
 * Copyright (c) 2018 devops
 *
 * <<licensetext>>
 *
 *   最长公共子序列 与 最长公共子串
 *
 *   最长公共子序列: 不用连续
 *
 *   c[i][j]的定义，记录的LCS的长度值), 依据递归公式，
 *   简单来说：如果横竖（i,j）对应的两个元素相等，该格子的值 = c[i-1,j-1] + 1。
 *   如果不等，取c[i-1,j] 和 c[i,j-1]的最大值。
 *   i = 0 or j = 0, c[i][j] = 0
 *   for(i <- 0 to imax) {
 *     for (i <- 0 to jmax) {
 *       // cal c[i][j]
 *       // ...
 *     }
 *   }
 *
 *   最长公共子串: 严格要求子串的连续性
 *
 *
 */
public class LCS {

  public void printArray(int[][] c, int maxa, int maxb) {
    for (int i = 0; i < maxa; i++) {
      for (int j = 0; j < maxb; j++) {
        if (j == 0) {
          System.out.print(c[i][j]);
        } else {
          System.out.print(" " + c[i][j]);
        }
      }
      System.out.println("\n");
    }
  }
  // 最长公共子序列(lcs-子序列可以不连续.png)，如何回溯找到子序列是什么？
  public int maxLCS(String a, String b) {
    int maxa = a.length() + 1;
    int maxb = b.length() + 1;
    int c[][] = new int[maxa][maxb];
    for (int i = 1; i < maxa; i++) {
      for (int j = 1; j <maxb; j++) {
        char ac = a.charAt(i-1);
        char bc = b.charAt(j-1);
        if (ac == bc) {
          if (i - 1 < 0 || j - 1 < 0) {
            c[i][j] = 1;
          } else {
            c[i][j] = c[i-1][j-1] + 1;
          }
        } else {
          c[i][j] = max(c[i-1][j],c[i][j-1]);
        }
      }
    }
    printArray(c, maxa, maxb);
    return c[maxa-1][maxb-1];
  }
  // 最长公共字串(lcs2-子串连续的.png), 如何回溯找到子串是什么？
  public int maxLcs(String str1, String str2) {
    int len1 = str1.length();
    int len2 = str2.length();
    int result = 0;     // 记录最长公共子串长度
    int c[][] = new int[len1+1][len2+1];
    for (int i = 0; i <= len1; i++) {
      for( int j = 0; j <= len2; j++) {
        if(i == 0 || j == 0) {
          c[i][j] = 0;
        } else if (str1.charAt(i-1) == str2.charAt(j-1)) {
          c[i][j] = c[i-1][j-1] + 1;
          result = max(c[i][j], result);
        } else {
          c[i][j] = 0;
        }
      }
    }
    return result;
  }
}

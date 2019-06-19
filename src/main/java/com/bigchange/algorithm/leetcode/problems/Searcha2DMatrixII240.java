package com.bigchange.algorithm.leetcode.problems;

/**
 * User: JerryYou
 *
 * Date: 2019-06-17
 *
 * Copyright (c) 2018 devops
 *
 * <<licensetext>>
 */
public class Searcha2DMatrixII240 {

  // 从最右上角的地方开始
  public boolean searchMatrix(int[][] matrix, int target) {
    int m = matrix.length;
    if (m == 0)
      return false;
    int n = matrix[0].length;
    if (n == 0)
      return false;
    int i = 0;
    int j = n - 1;
    while (i < m && j >= 0) {
      if (matrix[i][j] == target)
        return true;
      else if (matrix[i][j] > target)
        j--;
      else
        i++;
    }
    return false;
  }

  // space less but more time
  public boolean searchMatrix_direction(int[][] matrix, int target) {
    int x = matrix.length;
    if (x == 0) {
      return false;
    }
    for (int i = 0; i < x; i++) {
      for (int j = 0; j < matrix[i].length; j++) {
        if (matrix[i][j] == target) {
          return true;
        }
      }
    }
    return  false;
  }


}

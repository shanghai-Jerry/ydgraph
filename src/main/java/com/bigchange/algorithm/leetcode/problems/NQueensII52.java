package com.bigchange.algorithm.leetcode.problems;

/**
 * User: JerryYou
 *
 * Date: 2019-06-18
 *
 * Copyright (c) 2018 devops
 *
 * <<licensetext>>
 */
public class NQueensII52 {

  int [][] matrix;
  int N;
  public int totalNQueens(int n) {
    matrix = new int[n][n];
    N = n;
    int [] trace = new int[n];
    int count = backtrack_opt(0, 0, trace);
    // int count = backtrack(0, 0);
    return count;
  }

  // 搜索，搜索到底然后慢慢原路返回，然后继续搜索,
  // 优化后: 记录row行前的其他row放置了queen的坐标， 可以通过这个判断是否attacked
  private int backtrack_opt(int row, int count, int[] trace) {

    for (int i = 0; i < N; i++) {
      if (!checkAttacked(row, i, trace)) {
        // 尝试继续搜索
        trace[row] = i;
        if (row + 1 == N) {
          count += 1;
        } else {
          count = backtrack_opt(row + 1, count, trace);
        }
      }
    }
    return  count;
  }

  private boolean checkAttacked(int row, int col, int [] trace) {
    for (int i = 0; i < row; i ++) {
      int pos = trace[i];
      int idx1 = row + col;
      int idx2 = row - col;

      if (pos == col || (pos + i) == idx1 || (i - pos) == idx2) {
        return true;
      }
    }
    return  false;
  }

  // 搜索，搜索到底然后慢慢原路返回，然后继续搜索
  private int backtrack(int row, int count) {

    for (int i = 0; i < N; i++) {
      if (!isAttacked(row, i)) {
        // 尝试继续搜索
        put_queen(row, i);
        if (row + 1 == N) {
          // System.out.println("Matrix:");
          // print_out();
          count += 1;
        } else {
          count = backtrack(row + 1, count);
        }
        // 原路返回，调整状态。寻找其他搜索可能
        remove_queen(row, i);
      }
    }
    return  count;
  }


  private void put_queen(int row, int column) {
    matrix[row][column] = 1;
  }

  private void remove_queen(int row, int column) {
    matrix[row][column] = 0;
  }


  private boolean isAttacked(int row, int column) {

    for (int i = 0; i < N; i++) {
      if (matrix[i][column] == 1) {
        return true;
      }
      if (matrix[row][i] == 1) {
        return true;
      }
    }

    for (int i=row, j=column; i >= 0 && j < N ; i--,j++) {
        if (matrix[i][j] == 1) {
          return true;
        }
    }

    for (int i=row, j=column; i < N  && j >= 0; i++,j--) {
      if (matrix[i][j] == 1) {
        return true;
      }
    }


    for (int i=row, j=column; i >= 0 && j >= 0 ; i--,j--) {
      if (matrix[i][j] == 1) {
        return true;
      }
    }

    for (int i=row, j=column; i < N  && j < N; i++,j++) {
      if (matrix[i][j] == 1) {
        return true;
      }
    }

    return false;
  }

  private void print_out() {
    for (int i = 0; i< N; i++) {
      for (int j = 0; j< N; j++) {
        System.out.print(matrix[i][j] + " ");
      }
      System.out.println("\n");
    }
  }

}

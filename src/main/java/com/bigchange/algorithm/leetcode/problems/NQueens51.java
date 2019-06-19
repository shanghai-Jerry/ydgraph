package com.bigchange.algorithm.leetcode.problems;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * User: JerryYou
 *
 * Date: 2019-06-18
 *
 * Copyright (c) 2018 devops
 *
 * <<licensetext>>
 */
public class NQueens51 {

  int [][] matrix;
  int N;
  List<List<String>> ret = new ArrayList<>();

  // backtracking ...
  public List<List<String>> solveNQueens_backtracking(int n) {
    matrix = new int[n][n];
    N = n;
    int [] trace = new int[n];
    List<List<String>> ret = new ArrayList<>();
    // backtrack(0);
    backtrack_opt(0,  trace, ret);
    return  ret;
  }

  // 主要是该位置是否可以放queen的检查速度优化
  private void backtrack_opt(int row, int[] trace, List<List<String>> ret) {

    for (int i = 0; i < N; i++) {
      if (!checkAttacked(row, i, trace)) {
        // 尝试继续搜索
        trace[row] = i;
        if (row + 1 == N) {
          // get answer
          List<String> answer = new ArrayList<>();
          char[] r = new char[N];
          for (int j = 0; j < N; j++) {
            Arrays.fill(r, '.');
            r[trace[j]] = 'Q';
            answer.add(new String(r));
          }
          ret.add(answer);
        } else {
          backtrack_opt(row + 1, trace, ret);
        }
      }
    }
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
  private void backtrack(int row) {
    for (int i = 0; i < N; i++) {
      if (!isAttacked(row, i)) {
        put_queen(row, i);
        if (row + 1 == N) {
          // 存在的情况
          getRet();
        } else {
          backtrack(row + 1);
        }
        remove_queen(row, i);
      }
    }
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

  private void getRet() {
    List<String> row = new ArrayList<>();
    for (int i = 0; i< N; i++) {
      String rowString = "";
      for (int j = 0; j< N; j++) {
        if (matrix[i][j] == 1) {
          rowString = rowString + "Q";
        } else {
          rowString = rowString + ".";
        }
      }
      row.add(rowString);
    }
    ret.add(row);
  }


  public List<List<String>> solveNQueens(int n) {
    List<List<String>> answers = new ArrayList<>();
    permutation(0, n, new int[n], answers);
    return answers;
  }

  /**
   * 进行全排列，在全排列的过程中进行剪枝。
   * @param row 当前排列的行
   * @param n 列的数量
   * @param trace，前序计算中已经排列好的 trace
   * @param answers，答案收集器
   */
  private void permutation(int row, int n, int[] trace, List<List<String>> answers) {
    for(int i = 0; i < n; i++) {
      if (check(row, i, trace)) {
        trace[row] = i;

        if (row != n - 1) {
          permutation(row + 1, n, trace, answers);
          continue;
        }

        List<String> answer = new ArrayList<>(trace.length);
        char[] r = new char[n];

        for (int j = 0; j < trace.length; j++) {
          Arrays.fill(r, '.');
          r[trace[j]] = 'Q';
          answer.add(new String(r));
        }

        answers.add(answer);
      }
    }
  }

  /**
   * 计算一个 position 是否是可能的答案。
   * @param row position 行
   * @param col position 列
   * @param trace 前面几行已经摆放好的皇后
   * 思路如下：前面的每个已经放置好的皇后，对其所在的 [行，列，45°和 -45°的两个斜线] 都有霸权，
   * 即：这些线条上都容不下其他皇后了。
   * 计算当前的 position(row, col) 是否和前面的几个皇后冲突的方式就是计算当前的 position 是否在其他皇后的
   * “行、列、45°对角线、-45°对角线” 这四个线条任意一个线条的霸权上。
   *  行、列都好判断，两个对角线怎么判断呢？有一个简单结论：如果两个坐标 position1， position2 在 45°对角线上冲突，那么
   *  (position1.row + position1.col == position2.row + position2.col) == true，反之亦然，互为充要。
   *  所以 45° 冲突的计算方式是 row + col， 那么 -45° 冲突的计算方式就是 row - col。
   *  这个简单的公式，直接省掉了很多内存，一个长度为 n 的 trace 数组，就可以对后续可能的位置快速校验合法性，
   *  从而达到高效剪枝的目的。
   */
  private boolean check(int row, int col, int[] trace) {
    for (int i = 0; i < row; i++) {
      int pos = trace[i];
      int idx1 = col + row;
      int idx2 = col - row;

      if (pos == col || (pos + i) == idx1 || (pos - i) == idx2) {
        return false;
      }
    }

    return true;
  }
}

package com.bigchange.algorithm.leetcode.problems;

import java.util.Deque;
import java.util.LinkedList;

/**
 * User: JerryYou
 *
 * Date: 2019-06-12
 *
 * Copyright (c) 2018 devops
 *
 * <<licensetext>>
 */
public class P01Matrix542 {

  public int[][] updateMatrix(int[][] matrix) {
    initMatrix(matrix);

    for(int r = 0; r < matrix.length; r++) {
      for(int c = 0; c < matrix[r].length; c++) {
        if(matrix[r][c] == 0)
          // 通过每个0的位置去更新其他的最小距离
          findShortestPaths(matrix, r, c);
      }
    }

    return matrix;
  }

  private void initMatrix(int[][] m) {
    for(int r = 0; r < m.length; r++)
      for(int c = 0; c < m[r].length; c++)
        if(m[r][c] != 0) m[r][c] = Integer.MAX_VALUE;
  }

  private static class RowColumn {
    RowColumn(int r, int c) {
      this.r = r;
      this.c = c;
    }
    int r;
    int c;
  }

  // BFS - 1044ms, as expected, easy to understand but not efficient enough
  private void findShortestPaths(int[][] m, int r, int c) {
    // Deque双向队列 (既可以用作queue又可以为stack)
    // 本例子用于queue（BFS）
    Deque<RowColumn> q = new LinkedList<>();
    int level = 0;
    q.add(new RowColumn(r,c));
    while(!q.isEmpty()) {
      level++;
      int size = q.size();
      for(int i = 0; i < size; i++) {
        RowColumn node = q.poll();
        // top
        if(node.r > 0 && m[node.r-1][node.c] > level) {
          m[node.r-1][node.c] = level;
          q.add(new RowColumn(node.r-1, node.c));
        }
        // down
        if(node.r < m.length-1 && m[node.r+1][node.c] > level) {
          m[node.r+1][node.c] = level;
          q.add(new RowColumn(node.r+1, node.c));
        }
        // left
        if(node.c > 0 && m[node.r][node.c-1] > level) {
          m[node.r][node.c-1] = level;
          q.add(new RowColumn(node.r, node.c-1));
        }
        // right
        if(node.c < m[r].length-1 && m[node.r][node.c+1] > level) {
          m[node.r][node.c+1] = level;
          q.add(new RowColumn(node.r, node.c+1));
        }
      }
    }
  }

}

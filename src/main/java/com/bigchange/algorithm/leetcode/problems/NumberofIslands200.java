package com.bigchange.algorithm.leetcode.problems;

/**
 * User: JerryYou
 *
 * Date: 2019-05-29
 *
 * Copyright (c) 2018 devops
 *
 * <<licensetext>>
 */
public class NumberofIslands200 {

  public int numIslands(char[][] grid) {
    int xLen = grid.length;
    int  count = 0;
    if (xLen == 0) {
      return count;
    }
    int yLen = grid[0].length;
    for(int i = 0; i< xLen; i++) {
      for (int j = 0; j < yLen;j ++) {
        if (grid[i][j] == '1') {
          count++;
          searchConnect(grid, i, j);
        }
      }
    }
    return count;
  }

  public void searchConnect(char [][]grid, int i, int j) {
    int yLen = grid[0].length;
    int xLen = grid.length;
    if (i < 0 || j < 0) {
      return;
    }
    if (i >= xLen || j>= yLen) {
      return;
    }
    if (grid[i][j] == '0') {
      return;
    }
    // search all connected islands
    grid[i][j] = '0';
    // top
    searchConnect(grid, i - 1, j);
    // down
    searchConnect(grid, i + 1, j);
    // left
    searchConnect(grid, i, j-1);
    // right
    searchConnect(grid, i, j +1);
  }

}

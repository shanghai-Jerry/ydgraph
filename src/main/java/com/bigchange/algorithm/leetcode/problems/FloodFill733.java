package com.bigchange.algorithm.leetcode.problems;

/**
 * User: JerryYou
 *
 * Date: 2019-06-11
 *
 * Copyright (c) 2018 devops
 *
 * <<licensetext>>
 */
public class FloodFill733 {

  public int[][] floodFill(int[][] image, int sr, int sc, int newColor) {
    int oldColor = image[sr][sc];
    searchConnect(image, sr, sc, oldColor, newColor);
    return  image;
  }


  public void searchConnect(int [][]image, int i, int j, int oldColor, int newColor) {
    int xLen = image.length;
    int yLen = image[0].length;
    if (i < 0 || j < 0) {
      return;
    }
    if (i >= xLen || j>= yLen) {
      return;
    }
    if (image[i][j] != oldColor) {
      return;
    }

    if (image[i][j] == newColor) {
      return;
    }

    image[i][j] = newColor;

    // search all connected islands
    // top
    searchConnect(image, i - 1, j, oldColor, newColor);
    // down
    searchConnect(image, i + 1, j, oldColor, newColor);
    // left
    searchConnect(image, i, j - 1, oldColor, newColor);
    // right
    searchConnect(image, i, j +1, oldColor, newColor);
  }

}

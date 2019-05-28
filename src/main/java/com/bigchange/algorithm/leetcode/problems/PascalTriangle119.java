package com.bigchange.algorithm.leetcode.problems;

import java.util.ArrayList;
import java.util.List;

/**
 * User: JerryYou
 *
 * Date: 2019-05-08
 *
 * Copyright (c) 2018 devops
 *
 * <<licensetext>>
 *
 *   f(i,j)=1  where j=1 or j=i
 *
 *   f(i,j)=f(i−1,j−1) + f(i−1,j)
 */
public class PascalTriangle119 {

  public List<List<Integer>> generate(int numRows) {
    List<List<Integer>> result = new ArrayList<>();
    for (int i = 0 ; i < numRows; i++) {
      List<Integer> inner = new ArrayList<>();
      for(int j = 0; j<= i; j++) {
        if (j == 0 || i == j) {
          inner.add(1);
        } else {
          List<Integer> outIndex = result.get(i-1);
          inner.add(outIndex.get(j-1) + outIndex.get(j));
        }
      }
      result.add(inner);
    }
    return result;
  }

  // get row
  public List<Integer> getRow(int rowIndex) {
    List<List<Integer>> result = new ArrayList<>();
    for (int i = 0 ; i <=rowIndex; i++) {
      List<Integer> inner = new ArrayList<>();
      for(int j = 0; j<= i; j++) {
        if (j == 0 || i == j) {
          inner.add(1);
        } else {
          List<Integer> outIndex = result.get(i-1);
          inner.add(outIndex.get(j-1) + outIndex.get(j));
        }
      }
      result.add(inner);
    }
    return result.get(rowIndex);
  }

  // get row optimize
  public List<Integer> getRowOptimize(int rowIndex) {
    List<Integer> result = new ArrayList<>();
    for (int i = 0 ; i <=rowIndex; i++) {
      List<Integer> inner = new ArrayList<>();
      for(int j = 0; j<= i; j++) {
        if (j == 0 || i == j) {
          inner.add(1);
        } else {
          inner.add(result.get(j-1) + result.get(j));
        }
      }
      result = inner;
    }
    return result;
  }
}

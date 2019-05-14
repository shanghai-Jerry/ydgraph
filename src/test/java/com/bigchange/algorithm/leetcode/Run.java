package com.bigchange.algorithm.leetcode;

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
 */
public class Run {

  public void println(Object o) {
    System.out.println(o);
  }

  public static void main(String[] args) {
    Run run = new Run();

    UniqueBinarySearchTreesII uniqueBinarySearchTreesII = new UniqueBinarySearchTreesII();

    List<TreeNode> trees = new ArrayList<>();
    for (TreeNode treeNode : trees) {
      run.println(treeNode.val);
    }

  }
}

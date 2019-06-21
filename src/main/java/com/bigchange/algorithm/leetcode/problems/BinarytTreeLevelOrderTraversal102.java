package com.bigchange.algorithm.leetcode.problems;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;

/**
 * User: JerryYou
 *
 * Date: 2019-06-21
 *
 * Copyright (c) 2018 devops
 *
 * <<licensetext>>
 */
public class BinarytTreeLevelOrderTraversal102 {

  public List<List<Integer>> levelOrder(TreeNode root) {
    List<List<Integer>> ret = new ArrayList<>();
    if (root == null) {
      return ret;
    }
    ArrayDeque<TreeNode> queque = new ArrayDeque();
    queque.addLast(root);

    while (!queque.isEmpty()) {
      List<Integer> levels = new ArrayList<>();
      int levelSize = queque.size();
      for (int i = 0; i < levelSize; i++) {
        TreeNode item = queque.pollFirst();
        levels.add(item.val);
        if (item.left != null) {
          queque.addLast(item.left);
        }
        if (item.right != null) {
          queque.addLast(item.right);
        }
      }
      ret.add(levels);
    }
    return ret;
  }
}

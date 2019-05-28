package com.bigchange.algorithm.leetcode.problems;

/**
 * User: JerryYou
 *
 * Date: 2019-05-08
 *
 * Copyright (c) 2018 devops
 *
 * <<licensetext>>
 *
 *   Given [3,9,20,null,null,15,7]
 *
 *     3
 *    / \
 *   9  20
 *     /  \
 *    15   7
 *
 *   return its depth = 3.
 */
public class MaximumDepthofBinaryTree104 {
  public class TreeNode {
    int val;
    TreeNode left;
    TreeNode right;

    TreeNode(int x) {
      val = x;
    }
  }
  public int maxDepth(TreeNode root) {
    if (root == null) {
      return 0;
    }
    int left = 1 + maxDepth(root.left);
    int right = 1 + maxDepth(root.right);
    int max = left > right ? left: right;
    return  max;
  }
}

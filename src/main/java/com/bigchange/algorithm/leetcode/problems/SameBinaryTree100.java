package com.bigchange.algorithm.leetcode.problems;

import java.util.ArrayDeque;

/**
 * User: JerryYou
 *
 * Date: 2019-06-21
 *
 * Copyright (c) 2018 devops
 *
 * <<licensetext>>
 */
public class SameBinaryTree100 {

  // recursion solution
  public boolean isSameTree(TreeNode p, TreeNode q) {
    // p and q are both null
    if (p == null && q == null)
      return true;
    // one of p and q is null
    if (q == null || p == null)
      return false;
    if (p.val != q.val)
      return false;
    return isSameTree(p.right, q.right) && isSameTree(p.left, q.left);
  }

  // iteration solution
  public boolean isSameTreeWithIteration(TreeNode p, TreeNode q) {
    if (p == null && q == null)
      return true;
    if (!check(p, q))
      return false;
    // init deques
    ArrayDeque<TreeNode> deqP = new ArrayDeque<>();
    ArrayDeque<TreeNode> deqQ = new ArrayDeque<>();
    deqP.addLast(p);
    deqQ.addLast(q);

    while (!deqP.isEmpty()) {
      p = deqP.removeFirst();
      q = deqQ.removeFirst();

      if (!check(p, q))
        return false;
      if (p != null) {
        // in Java nulls are not allowed in Deque
        if (!check(p.left, q.left))
          return false;
        if (p.left != null) {
          deqP.addLast(p.left);
          deqQ.addLast(q.left);
        }
        if (!check(p.right, q.right))
          return false;
        if (p.right != null) {
          deqP.addLast(p.right);
          deqQ.addLast(q.right);
        }
      }
    }
    return true;
  }
  public boolean check(TreeNode p, TreeNode q) {
    // p and q are null
    if (p == null && q == null)
      return true;
    // one of p and q is null
    if (q == null || p == null)
      return false;
    if (p.val != q.val)
      return false;
    return true;
  }
}

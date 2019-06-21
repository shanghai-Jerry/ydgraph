package com.bigchange.algorithm.leetcode.problems;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.Stack;

/**
 * User: JerryYou
 *
 * Date: 2019-06-06
 *
 * Copyright (c) 2018 devops
 *
 * <<licensetext>>
 */

public class BinaryTreeInorderTraversal94 {

  // 中序遍历

  // recursion
  List<Integer> list = new ArrayList<>();
  public List<Integer> inorderTraversal(TreeNode root) {
    if (root == null) {
      return  list;
    }
    inorderTraversal(root.left);
    list.add(root.val);
    inorderTraversal(root.right);
    return  list;
  }

  // hard to explanation
  public List <Integer> inorderTraversal_threaded_binary_tree(TreeNode root) {
    List < Integer > res = new ArrayList <> ();
    TreeNode curr = root;
    TreeNode pre;
    while (curr != null) {
      if (curr.left == null) {
        res.add(curr.val);
        curr = curr.right; // move to next right node
      } else { // has a left subtree
        pre = curr.left;
        while (pre.right != null) { // find rightmost
          pre = pre.right;
        }
        pre.right = curr; // put cur after the pre node
        TreeNode temp = curr; // store cur node
        curr = curr.left; // move cur to the top of the new tree
        temp.left = null; // original cur left be null, avoid infinite loops
      }
    }
    return res;
  }

  public List <Integer> inorderTraversal_stack(TreeNode root) {
    List < Integer > res = new ArrayList < > ();
    Stack < TreeNode > stack = new Stack < > ();
    TreeNode curr = root;
    while (curr != null || !stack.isEmpty()) {
      while (curr != null) {
        stack.push(curr);
        curr = curr.left;
      }
      curr = stack.pop();
      res.add(curr.val);
      curr = curr.right;
    }
    return res;
  }

  // 逻辑更严谨，更清晰： 当前node有left先进，需要将push后的节点重置为null，类似与已经visit的逻辑，不能重复访问
  public List<Integer> inorderTraversal_iteratively_2(TreeNode root) {
    List<Integer> ret = new LinkedList<>();
    Stack<TreeNode> stack = new Stack<>();
    if (root == null){
      return ret;
    }
    stack.push(root);
    while (!stack.isEmpty()){
      TreeNode tmp = stack.peek();
      if (tmp.left != null){
        stack.push(tmp.left);
        tmp.left = null;
      }else{
        ret.add(tmp.val);
        stack.pop();
        if (tmp.right != null){
          stack.push(tmp.right);
          tmp.right = null;
        }
      }
    }
    return ret;
  }

  // iteratively
  public List<Integer> inorderTraversal_iteratively(TreeNode root) {
    Stack<TreeNode> stack = new Stack<>();
    Set<TreeNode> visit = new HashSet<>();
    List<Integer> list = new ArrayList<>();
    if (root != null) {
      stack.add(root);
      visit.add(root);
    } else {
      return  list;
    }
    while (!stack.isEmpty()) {
      TreeNode node = stack.peek();
      boolean pop = true;
      if (node.right != null && !visit.contains(node.right)) {
        TreeNode mid = stack.pop();
        stack.add(node.right);
        visit.add(node.right);
        stack.add(mid);
        pop = false;
      }
      if (!visit.contains(node)) {
        stack.add(node);
        visit.add(node);
      }
      if (node.left != null && !visit.contains(node.left)) {
        stack.add(node.left);
        visit.add(node.left);
        pop = false;
      }
      if (pop) {
        TreeNode top=stack.pop();
        list.add(top.val);
      }
    }
    return  list;
  }

}

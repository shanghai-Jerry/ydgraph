package com.bigchange.algorithm.leetcode.problems;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * User: JerryYou
 *
 * Date: 2019-05-13
 *
 * Copyright (c) 2018 devops
 *
 * <<licensetext>>
 */
public class UniqueBinarySearchTreesII95 {

  public List<TreeNode> generateTrees(int n) {
    return  n != 0 ? GetTrees(1,n): new ArrayList<>();
    // return  generateSubtrees(1, n);
  }


  private List<TreeNode> generateSubtrees(int s, int e) {
    List<TreeNode> res = new LinkedList<TreeNode>();
    if (s > e) {
      res.add(null); // empty tree
      return res;
    }

    for (int i = s; i <= e; ++i) {
      List<TreeNode> leftSubtrees = generateSubtrees(s, i - 1);
      List<TreeNode> rightSubtrees = generateSubtrees(i + 1, e);

      for (TreeNode left : leftSubtrees) {
        for (TreeNode right : rightSubtrees) {
          TreeNode root = new TreeNode(i);
          root.left = left;
          root.right = right;
          res.add(root);
        }
      }
    }
    return res;
  }


  private List<TreeNode> GetTrees(int x,int y){
    List<TreeNode> res = new ArrayList<>();
    if(x>y)
      res.add(null);
    else if(x==y)
      res.add(new TreeNode(x));
    else
      for(int i=x;i<=y;i++){
        List<TreeNode> lefts = GetTrees(x,i-1);
        List<TreeNode> rights = GetTrees(i+1,y);
        for(TreeNode l:lefts)
          for(TreeNode r:rights){
            TreeNode root = new TreeNode(i);
            root.left = l;
            root.right = r;
            res.add(root);
          }
      }
    return res;
  }

}

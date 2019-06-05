package com.bigchange.algorithm.leetcode.problems;

import java.util.List;

/**
 * User: JerryYou
 *
 * Date: 2019-06-05
 *
 * Copyright (c) 2018 devops
 *
 * <<licensetext>>
 */
class Node {
  public int val;
  public List<Node> neighbors;

  public Node() {}

  public Node(int _val,List<Node> _neighbors) {
    val = _val;
    neighbors = _neighbors;
  }
}
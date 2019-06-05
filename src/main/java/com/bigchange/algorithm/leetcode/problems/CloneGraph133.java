package com.bigchange.algorithm.leetcode.problems;

import java.util.ArrayList;
import java.util.HashMap;
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
public class CloneGraph133 {

  // 存放已经访问的node地址，避免同一个node的val一样但是是不同的node，应该是同一个node的
  HashMap<Integer,Node> visit = new HashMap<>();
  public Node cloneGraph(Node node) {
    Node root = new Node(node.val, new ArrayList<>());
    if (node.neighbors.size() == 0) {
      return root;
    }
    if (visit.containsKey(node.val)) {
      return visit.get(node.val);
    }
    visit.put(node.val, root);
    List<Node> negs = new ArrayList<>();
    for (Node n : node.neighbors) {
      Node r = cloneGraph(n);
      negs.add(r);
    }
    root.neighbors = negs;
    return  root;
  }
}

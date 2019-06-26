package com.bigchange.algorithm.leetcode.problems;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

/**
 * User: JerryYou
 *
 * Date: 2019-06-25
 *
 * Copyright (c) 2018 devops
 *
 * <<licensetext>>
 */
public class TheSkylineProblem218 {

  private class Edge {
    int height;
    int x;
    boolean isStart;

    Edge(int x, int height, boolean isStart) {
      this.x = x;
      this.height = height;
      this.isStart = isStart;
    }
  }


  public List<List<Integer>> getSkyline(int[][] buildings) {
    List<List<Integer>> ret = new ArrayList<>();
    if (buildings == null || buildings.length == 0 || buildings[0].length == 0) {
      return ret;
    }
    List<Edge> edges = new ArrayList<>();
    for (int i = 0; i < buildings.length; i++) {
      edges.add(new Edge(buildings[i][0], buildings[i][2], true));
      edges.add(new Edge(buildings[i][1], buildings[i][2], false));
    }

    Collections.sort(edges, (e1, e2) -> {
      if (e1.x != e2.x) {
        return e1.x - e2.x;
      } else if (e1.isStart && e2.isStart) {
        return e2.height - e1.height;
      } else if (!e1.isStart && !e2.isStart) {
        return e1.height - e2.height;
      } else {
        return e1.isStart ? -1 : 1;
      }
    });

    PriorityQueue<Integer> pq = new PriorityQueue<>(10, Comparator.reverseOrder());
    for (Edge edge : edges) {
      if (edge.isStart) {
        if (pq.isEmpty() || edge.height > pq.peek()) {
          List<Integer> ans = new ArrayList<>();
          ans.add(edge.x);
          ans.add(edge.height);
          ret.add(ans);
        }
        pq.offer(edge.height);
      } else {
        pq.remove(edge.height);
        if (pq.isEmpty()) {
          List<Integer> ans = new ArrayList<>();
          ans.add(edge.x);
          ans.add(0);
          ret.add(ans);
        } else if (edge.height > pq.peek()) {
          List<Integer> ans = new ArrayList<>();
          ans.add(edge.x);
          ans.add(pq.peek());
          ret.add(ans);
        }
      }
    }

    return ret;
  }
}

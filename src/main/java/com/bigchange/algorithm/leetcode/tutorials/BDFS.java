package com.bigchange.algorithm.leetcode.tutorials;

/**
 * User: JerryYou
 *
 * Date: 2019-05-28
 *
 * Copyright (c) 2018 devops
 *
 * <<licensetext>>
 */
public class BDFS {

  /*
  // Return the length of the shortest path between root and target node.

  int BFS(Node root, Node target) {
    Queue<Node> queue;  // store all nodes which are waiting to be processed
    Set<Node> visited;  // store all the nodes that we've visited
    int step = 0;       // number of steps neeeded from root to current node
    // initialize
    add root to queue;
    add root to visited;
    // BFS
    while (queue is not empty) {
      step = step + 1;
      // iterate the nodes which are already in the queue
      int size = queue.size();
      for (int i = 0; i < size; ++i) {
        Node cur = the first node in queue;
        return step if cur is target;
        for (Node next : the neighbors of cur) {
          if (next is not in used) {
            add next to queue;
            add next to visited;
          }
          remove the first node from queue;
        }
      }
    }
    return -1;          // there is no path from root to target
  }


 // Return true if there is a path from cur to target.

  boolean DFS(Node cur, Node target, Set<Node> visited) {
    return true if cur is target;
    for (next : each neighbor of cur) {
      if (next is not in visited) {
        add next to visted;
        return true if DFS(next, target, visited) == true;
      }
    }
    return false;
  }

  // suffer from stack overflow with recursion, implement DFS using an explicit stack instead

  boolean DFS(int root, int target) {
    Set<Node> visited;
    Stack<Node> stack;
    add root to stack;
    while (s is not empty) {
        Node cur = the top element in stack;
        remove the cur from the stack;
        return true if cur is target;
        for (Node next : the neighbors of cur) {
            if (next is not in visited) {
                add next to visited;
                add next to stack;
            }
        }
    }
    return false;
   }
  */

}

package com.bigchange.algorithm.leetcode.problems;

import java.util.PriorityQueue;
import java.util.Queue;

/**
 * User: JerryYou
 *
 * Date: 2019-06-06
 *
 * Copyright (c) 2018 devops
 *
 * <<licensetext>>
 */
public class ImplementStackusingQueues225 {

  Queue<Integer> queue;
  Queue<Integer> queue_2;
  public ImplementStackusingQueues225() {
    queue_2 = new PriorityQueue<>();
    queue = new PriorityQueue<>();
  }

  /** Push element x onto stack. */
  public void push(int x) {
    queue.offer(x);
  }

  /** Removes the element on top of the stack and returns that element. */
  public int pop() {
    int ret = -1;
    if (queue.size() > 1) {
      while(queue.size() > 1) {
        queue_2.offer(queue.poll());
      }
      ret =  queue.poll();
      while (!queue_2.isEmpty()) {
        queue.offer(queue_2.poll());
      }
    } else if (queue.size() == 1) {
      ret =  queue.poll();
      while (!queue_2.isEmpty()) {
        queue.offer(queue_2.poll());
      }
    }
    return  ret;
  }

  /** Get the top element. */
  public int top() {
    int ret = -1;
    if (queue.size() > 1) {
      while(queue.size() > 1) {
        queue_2.offer(queue.poll());
      }
      ret =  queue.poll();
      while (!queue_2.isEmpty()) {
        queue.offer(queue_2.poll());
      }
      queue.offer(ret);
    } else if (queue.size() == 1) {
      ret =  queue.poll();
      while (!queue_2.isEmpty()) {
        queue.offer(queue_2.poll());
      }
      queue.offer(ret);
    }
    return  ret;
  }

  /** Returns whether the stack is empty. */
  public boolean empty() {
    return queue.isEmpty() && queue_2.isEmpty();
  }
}

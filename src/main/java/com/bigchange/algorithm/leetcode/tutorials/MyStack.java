package com.bigchange.algorithm.leetcode.tutorials;

import java.util.ArrayList;
import java.util.List;

/**
 * User: JerryYou
 *
 * Date: 2019-06-03
 *
 * Copyright (c) 2018 devops
 *
 * <<licensetext>>
 */

class MyStack {
  private List<Integer> data;               // store elements
  public MyStack() {
    data = new ArrayList<>();
  }
  /** Insert an element into the stack. */
  public void push(int x) {
    data.add(x);
  }
  /** Checks whether the queue is empty or not. */
  public boolean isEmpty() {
    return data.isEmpty();
  }
  /** Get the top item from the queue. */
  public int top() {
    return data.get(data.size() - 1);
  }
  /** Delete an element from the queue. Return true if the operation is successful. */
  public boolean pop() {
    if (isEmpty()) {
      return false;
    }
    data.remove(data.size() - 1);
    return true;
  }
}

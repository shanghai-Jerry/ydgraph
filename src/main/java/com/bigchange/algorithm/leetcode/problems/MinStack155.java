package com.bigchange.algorithm.leetcode.problems;

import java.util.Stack;

/**
 * User: JerryYou
 *
 * Date: 2019-06-03
 *
 * Copyright (c) 2018 devops
 *
 * <<licensetext>>
 */
public class MinStack155 {

  class StackItem {
    int min;
    int val;

    public StackItem(int min, int val) {
      this.min = min;
      this.val = val;
    }
  }

  Stack<StackItem> minStack;

  public MinStack155() {
    minStack = new Stack<>();
  }

  // every time push a item, we can exactly know the minimum, just mark it done.
  public void push(int x) {
    if (minStack.isEmpty()) {
      minStack.push(new StackItem(x, x));
    } else {
      int  min = minStack.peek().min > x ? x: minStack.peek().min;
      minStack.push(new StackItem(min, x));
    }
  }

  public void pop() {
    if (minStack.isEmpty()) {
      return;
    }
    minStack.pop();
  }

  public int top() {
    if (minStack.isEmpty()) {
      return  -1;
    }
    return  minStack.peek().val;
  }

  public int getMin() {
    return minStack.peek().min;
  }
}

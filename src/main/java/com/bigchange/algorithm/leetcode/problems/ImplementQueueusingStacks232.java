package com.bigchange.algorithm.leetcode.problems;

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
public class ImplementQueueusingStacks232 {

  Stack<Integer> stack;
  Stack<Integer> stack_2;
  public ImplementQueueusingStacks232() {
    stack = new Stack<>();
    stack_2 = new Stack<>();
  }

  /** Push element x to the back of queue. */
  public void push(int x) {
    stack.push(x);

  }

  /** Removes the element from in front of queue and returns that element. */
  public int pop() {
    if (!stack_2.isEmpty()) {
      return stack_2.pop();
    }
    while (!stack.isEmpty()) {
      stack_2.push(stack.pop());
    }

    if (stack_2.isEmpty()) {
      return  -1;
    }
    return stack_2.pop();

  }

  /** Get the front element. */
  public int peek() {
    if (!stack_2.isEmpty()) {
      return stack_2.peek();
    }
    while (!stack.isEmpty()) {
      stack_2.push(stack.pop());
    }

    if (stack_2.isEmpty()) {
      return  -1;
    }
    return stack_2.peek();

  }

  /** Returns whether the queue is empty. */
  public boolean empty() {
    return stack.isEmpty() && stack_2.isEmpty();
  }
}

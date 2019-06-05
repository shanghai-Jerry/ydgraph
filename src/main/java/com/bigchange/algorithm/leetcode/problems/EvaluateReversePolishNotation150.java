package com.bigchange.algorithm.leetcode.problems;

import java.util.Stack;

/**
 * User: JerryYou
 *
 * Date: 2019-06-05
 *
 * Copyright (c) 2018 devops
 *
 * <<licensetext>>
 */
public class EvaluateReversePolishNotation150 {

  public int evalRPN(String[] tokens) {
    Stack<Integer> stack = new Stack<>();
    int len = tokens.length;
    for (int i = 0; i < len; i++) {
      String c = tokens[i];
      if ("+".equals(c)) {
        int v = stack.pop() + stack.pop();
        stack.push(v);
      } else if ("-".equals(c)) {
        int v = -stack.pop() + stack.pop();
        stack.push(v);
      } else if ("*".equals(c)) {
        int v = stack.pop() * stack.pop();
        stack.push(v);
      } else if ("/".equals(c)) {
        int v1 = stack.pop();
        int v2 = stack.pop();
        if (v2 == 0) {
          stack.push(v2);
        } else {
          int v = v2 / v1;
          stack.push(v);
        }
      } else {
        stack.push(Integer.parseInt(c));
      }
    }
    return stack.peek();
  }
}

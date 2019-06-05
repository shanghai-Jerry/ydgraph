package com.bigchange.algorithm.leetcode.problems;

import java.util.Stack;

/**
 * User: JerryYou
 *
 * Date: 2019-06-04
 *
 * Copyright (c) 2018 devops
 *
 * <<licensetext>>
 */
public class ValidParentheses20 {

  public boolean isValid(String s) {
    Stack<Character> stack = new Stack<>();
    if (s.isEmpty()) {
      return true;
    }
    for (int i = 0 ; i< s.length(); i++) {
      char c = s.charAt(i);
      if (c == ']') {
        if (stack.isEmpty()) {
          return false;
        } else  {
          char top = stack.peek();
          if (top == '[') {
            stack.pop();
          } else {
            return false;
          }

        }
      } else if ( c == '}') {
        if (stack.isEmpty()) {
          return false;
        } else  {
          char top = stack.peek();
          if (top == '{') {
            stack.pop();
          } else {
            return false;
          }
        }
      } else if ( c == ')' ) {
        if (stack.isEmpty()) {
          return false;
        } else  {
          char top = stack.peek();
          if (top == '(') {
            stack.pop();
          } else {
            return false;
          }
        }
      } else {
        stack.push(c);
      }
    }

    return stack.isEmpty();
  }
}

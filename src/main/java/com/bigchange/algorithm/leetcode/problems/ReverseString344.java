package com.bigchange.algorithm.leetcode.problems;

/**
 * User: JerryYou
 *
 * Date: 2019-05-07
 *
 * Copyright (c) 2018 devops
 *
 * <<licensetext>>
 *
 * Input: ["h","e","l","l","o"]
 * Output: ["o","l","l","e","h"]
 */
public class ReverseString344 {

  public void reverseString(char[] s) {
    // recurse(0, s);
    reverse(s);
    // System.out.println(s);
  }

  private void reverse(char[] s) {
    for (int i=0,j = s.length -1; i < j; i++,j--) {
      char tmp = s[j];
      s[j] = s[i];
      s[i] = tmp;
    }
  }

  private void recurse(int index, char[] s) {
    if(index >= s.length/2) {
      return;
    }

    recurse(index+1, s);

    int j = s.length-1-index;

    char temp = s[index];
    s[index] = s[j];
    s[j] = temp;
  }
}


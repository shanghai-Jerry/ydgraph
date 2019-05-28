package com.bigchange.algorithm.leetcode.problems;

import java.util.ArrayList;

/**
 * User: JerryYou
 *
 * Date: 2019-05-20
 *
 * Copyright (c) 2018 devops
 *
 * <<licensetext>>
 */
public class RemoveAllAdjacentDuplicatesInString1047 {

  // good solution: clear your mind first
  public String removeDuplicates_2(String S) {
    if(S == null || S.length() < 2) return S;
    int index = 0;
    char[] chars = new char[S.length()];

    for(char c : S.toCharArray()) {
      if(index == 0 || chars[index - 1] != c) {
        chars[index++] = c;
      }
      else {
        index--;
      }
    }

    return new String(chars).substring(0, index);
  }


  public String removeDuplicates_1(String S) {
    // StringBuilder is not fast enough
    StringBuilder sb = new StringBuilder();
    for(char c : S.toCharArray()) {
      if(sb.length() == 0 || sb.charAt(sb.length()-1) != c)
        sb.append(c);
      else
        sb = sb.deleteCharAt(sb.length()-1);
    }
    return sb.toString();
  }

}

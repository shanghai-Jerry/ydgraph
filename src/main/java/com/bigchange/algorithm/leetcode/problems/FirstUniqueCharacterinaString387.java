package com.bigchange.algorithm.leetcode.problems;

import java.util.HashMap;
import java.util.Map;

/**
 * User: JerryYou
 *
 * Date: 2019-11-06
 *
 * Copyright (c) 2018 devops
 *
 * <<licensetext>>
 */
public class FirstUniqueCharacterinaString387 {

  public int firstUniqChar(String s) {
    Map<Character, Integer> count = new HashMap<>();
    char [] sarry = s.toCharArray();
    int  [] pos = new int[s.length()];
    for (int i = 0; i < sarry.length;i++) {
      char key = sarry[i];
      int value = count.getOrDefault(key, -1);
      if (value == -1) {
       pos[i] = 1;
       count.put(key, i);
      } else {
        pos[value] = 0;
      }
    }
    for (int i = 0; i < sarry.length;i++) {
      if (pos[i] == 1) {
        return  i;
      }
    }

    return -1;
  }
}

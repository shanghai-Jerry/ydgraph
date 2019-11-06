package com.bigchange.algorithm.leetcode.problems;

import java.util.HashMap;

/**
 * User: JerryYou
 *
 * Date: 2019-11-06
 *
 * Copyright (c) 2018 devops
 *
 * <<licensetext>>
 */
public class IsomorphicStrings205 {

  public boolean isIsomorphic(String s, String t) {

    HashMap<Character, Character> mapping = new HashMap<>();
    HashMap<Character, Character> mapping2 = new HashMap<>();

    if (s.length() != t.length()) {
      return  false;
    }

    char [] sarry = s.toCharArray();
    char [] tarry = t.toCharArray();

    for (int i = 0; i < sarry.length; i ++) {
      char schar = sarry[i];
      char tchar = tarry[i];
      if (mapping.containsKey(schar)) {
        char value = mapping.get(schar);
        if (value != tchar) {
          return  false;
        }
      } else  {
        mapping.put(schar, tchar);
        if (mapping2.containsKey(tchar)) {
          char value = mapping2.get(tchar);
          if (value != schar) {
            return  false;
          }
        } else {
          mapping2.put(tchar, schar);
        }

      }
    }
    return  true;
  }

}

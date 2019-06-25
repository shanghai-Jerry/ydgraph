package com.bigchange.algorithm.leetcode.problems;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * User: JerryYou
 *
 * Date: 2019-06-25
 *
 * Copyright (c) 2018 devops
 *
 * <<licensetext>>
 */
public class LetterCombinationsofaPhoneNumber17 {

  public List<String> letterCombinations(String digits) {
    prepare();
    String answer = "";
    List<String> ret = new ArrayList<>();
    backtracking(digits, 0, ret, answer);
    return ret;
  }
  private HashMap<Character, char[]> letterMap = new HashMap<>();
  private void prepare() {
    letterMap.put('2', "abc".toCharArray());
    letterMap.put('3', "def".toCharArray());
    letterMap.put('4', "ghi".toCharArray());
    letterMap.put('5', "jkl".toCharArray());
    letterMap.put('6', "mno".toCharArray());
    letterMap.put('7', "pqrs".toCharArray());
    letterMap.put('8', "tuv".toCharArray());
    letterMap.put('9', "wxyz".toCharArray());
  }
  private void backtracking(String digits, int start, List<String> ret, String answer) {
    if (answer.length() == digits.length()) {
      if (digits.length() != 0) {
        ret.add(answer);
      }
    } else {
      char[] letters = letterMap.get(digits.charAt(start));
      int len = letters.length;
      for (int i  = 0; i < len; i++) {
        answer = answer + letters[i];
        backtracking(digits, start + 1, ret, answer);
        answer = answer.substring(0, answer.length() - 1);
      }
    }
  }
}

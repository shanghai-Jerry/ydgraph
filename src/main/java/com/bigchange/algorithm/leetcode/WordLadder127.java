package com.bigchange.algorithm.leetcode;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * User: JerryYou
 *
 * Date: 2019-05-08
 *
 * Copyright (c) 2018 devops
 *
 * <<licensetext>>
 *
 * Input:
 * beginWord = "hit",
 * endWord = "cog",
 * wordList = ["hot","dot","dog","lot","log","cog"]
 *
 * Output: 5
 *
 * Explanation: As one shortest transformation is "hit" -> "hot" -> "dot" -> "dog" -> "cog",
 * return its length 5.
 */
public class WordLadder127 {

  public int ladderLength(String beginWord, String endWord, List<String> wordList) {

    Set<String> beginSet = new HashSet<>(), endSet = new HashSet<>();

    int len = 1;
    HashSet<String> visited = new HashSet<>();

    beginSet.add(beginWord);
    endSet.add(endWord);

    while (!beginSet.isEmpty() && !endSet.isEmpty()) {
      // 始终保持 beginset's length > endset's length
      if (beginSet.size() > endSet.size()) {
        Set<String> set = beginSet;
        beginSet = endSet;
        endSet = set;
      }

      Set<String> temp = new HashSet<>();
      for (String word : beginSet) {
        char[] chs = word.toCharArray();
        for (int i = 0; i < chs.length; i++) {
          for (char c = 'a'; c <= 'z'; c++) {
            // 依次替换其中的某个char
            char old = chs[i];
            chs[i] = c;
            // 替换后的string
            String target = String.valueOf(chs);
            if (endSet.contains(target)) {
              return len + 1;
            }
            // 判断是否已是替换过的string
            if (!visited.contains(target) && wordList.contains(target)) {
              temp.add(target);
              visited.add(target);
            }
            // 替换其他位置时，原来的位置char还原
            chs[i] = old;
          }
        }
      }

      beginSet = temp;

      len++;
    }

    return 0;
  }
}

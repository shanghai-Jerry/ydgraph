package com.bigchange.algorithm.leetcode.problems;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static java.lang.Math.max;

/**
 * User: JerryYou
 *
 * Date: 2019-05-21
 *
 * Copyright (c) 2018 devops
 *
 * <<licensetext>>
 */
public class LongestStringChain1048 {

  // 7ms
  public int longestStrChain_2(String[] words)
  {
    List<List<String>> strings = new ArrayList<>();
    for (int i = 0; i < 16; ++i)
    {
      strings.add(new ArrayList<>());
    }

    for (String word : words)
    {
      strings.get(word.length() - 1).add(word);
    }

    int res = 0;
    for (int i = 0; i < 16; ++i)
    {
      if (res > 15 - i)
      {
        break;
      }

      List<String> curs = strings.get(i);
      for (String cur : curs)
      {
        res = Math.max(res, test(cur, i, strings));
      }
    }

    return res;
  }

  private static int test(String s, int idx, List<List<String>> strings)
  {
    if (idx == 15)
    {
      return 1;
    }

    List<String> nexts = strings.get(idx + 1);
    int res = 1;
    for (String next : nexts)
    {
      if (match(s, next))
      {
        res = Math.max(res, test(next, idx + 1, strings) + 1);
      }
    }
    return res;
  }

  // this match method is really fast
  private static boolean match(String x, String y)
  {
    for (int i = 0; i < x.length(); ++i)
    {
      if (x.charAt(i) != y.charAt(i))
      {
        return x.substring(i).equals(y.substring(i + 1));
      }
    }
    return true;
  }


  // 13 ms
  // 预处理好需要继续LCS的words, 时间变快
  Map<Integer, ArrayList<String>> newWordsMap = new HashMap<>();
  Set<String>  visit = new HashSet<>();
  public int longestStrChain(String[] words) {
    long start = System.currentTimeMillis();
    int res = 0;
    calWordsLengthMap(words);
    // 16-res 之后的最长长度也只有 16 - res, 那就不用去从16-res的开始位置继续比较下去了
    for(int i = 1; i <= 16 - res; i++) {
      ArrayList<String> lenWords = newWordsMap.getOrDefault(i, new ArrayList<>());
      for (int j = 0 ;j < lenWords.size(); j++) {
        String word = lenWords.get(j);
        if (visit.contains(word)) {
          continue;
        }
        int len = 1;
        ArrayList<String> newWords = newWords(word);
        if (newWords.size() != 0) {
          len = longestStrChainCal(word, len, newWords);
        }
        if (len > res) {
          res = len;
        }
      }

    }
    long end = System.currentTimeMillis();
    System.out.println((end - start)/1000);
    return res;
  }

  private void calWordsLengthMap(String[] words) {
    for (String word : words) {
      int len = word.length();
      ArrayList<String> lenWords = newWordsMap.getOrDefault(len, new ArrayList<>());
      lenWords.add(word);
      newWordsMap.put(len, lenWords);
    }
  }
  // a.length < b.length
  private boolean maxLCS(String a, String b) {
    int maxa = a.length() + 1;
    int maxb = b.length() + 1;
    int c[][] = new int[maxa][maxb];
    for (int i = 1; i < maxa; i++) {
      for (int j = 1; j <maxb; j++) {
        char ac = a.charAt(i-1);
        char bc = b.charAt(j-1);
        if (ac == bc) {
          if (i - 1 < 0 || j - 1 < 0) {
            c[i][j] = 1;
          } else {
            c[i][j] = c[i-1][j-1] + 1;
          }
        } else {
          c[i][j] = max(c[i-1][j],c[i][j-1]);
        }
      }
    }

    int maxLcs = c[maxa-1][maxb-1];
    if (maxLcs == a.length()) {
      return true;
    }
    return false ;
  }

  private  ArrayList<String> newWords(String wordBase) {
    ArrayList list = newWordsMap.getOrDefault(wordBase.length() + 1, new ArrayList<>());
    return list;
  }

  private int longestStrChainCal(String wordBase, int len, ArrayList<String> words) {
    int ret = len;
    for (int i= 0; i< words.size();i++) {
      String word = words.get(i);
      boolean lcs = match(wordBase, word);
      if (lcs) {
        visit.add(word);
        ArrayList<String> newWords = newWords(word);
        if (newWords.size() == 0) {
          return len + 1;
        }
        int res = longestStrChainCal(word, len+1, newWords);
        if (res > ret) {
          ret = res;
        }
      }
    }
    return ret;
  }
}

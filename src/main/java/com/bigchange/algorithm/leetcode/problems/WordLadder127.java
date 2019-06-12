package com.bigchange.algorithm.leetcode.problems;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;


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

  class Pair {
    String word;
    int level;
    Pair(String word, int level) {
      this.word = word;
      this.level = level;
    }
    String getKey() {
      return this.word;
    }
    int getValue() {
      return this.level;
    }
  }


  // Since all words are of same length.
  int L = 0;

  // Dictionary to hold combination of words that can be formed,
  // from any given word. By changing one letter at a time.
  HashMap<String, ArrayList<String>> allComboDict = new HashMap<>();


  // 双向bfs
  // 我们可以做双向的DFS遍历，即从beginword和endword出发，双向搜索，搜素策略：选择当前节点数较少的一端继续搜索
  private int visitWordNode(
      Queue<Pair> Q,
      HashMap<String, Integer> visited,
      HashMap<String, Integer> othersVisited) {
    Pair node = Q.remove();
    String word = node.getKey();
    int level = node.getValue();

    for (int i = 0; i < this.L; i++) {

      // Intermediate words for current word
      String newWord = word.substring(0, i) + '*' + word.substring(i + 1, L);

      // Next states are all the words which share the same intermediate state.
      for (String adjacentWord : this.allComboDict.getOrDefault(newWord, new ArrayList<String>())) {
        // If at any point if we find what we are looking for
        // i.e. the end word - we can return with the answer.
        if (othersVisited.containsKey(adjacentWord)) {
          return level + othersVisited.get(adjacentWord);
        }

        if (!visited.containsKey(adjacentWord)) {

          // Save the level as the value of the dictionary, to save number of hops.
          visited.put(adjacentWord, level + 1);
          Q.add(new Pair(adjacentWord, level + 1));
        }
      }
    }
    return -1;
  }

  public int ladderLength(String beginWord, String endWord, List<String> wordList) {

    if (!wordList.contains(endWord)) {
      return 0;
    }

    // Since all words are of same length.
    this.L = beginWord.length();

    wordList.forEach(
        word -> {
          for (int i = 0; i < L; i++) {
            // Key is the generic word
            // Value is a list of words which have the same intermediate generic word.
            String newWord = word.substring(0, i) + '*' + word.substring(i + 1, L);
            ArrayList<String> transformations =
                this.allComboDict.getOrDefault(newWord, new ArrayList<String>());
            transformations.add(word);
            this.allComboDict.put(newWord, transformations);
          }
        });

    // Queues for birdirectional BDFS
    // BDFS starting from beginWord
    Queue<Pair> Q_begin = new LinkedList<>();
    // BDFS starting from endWord
    Queue<Pair> Q_end = new LinkedList<>();
    Q_begin.add(new Pair(beginWord, 1));
    Q_end.add(new Pair(endWord, 1));

    // Visited to make sure we don't repeat processing same word.
    HashMap<String, Integer> visitedBegin = new HashMap<String, Integer>();
    HashMap<String, Integer> visitedEnd = new HashMap<String, Integer>();
    visitedBegin.put(beginWord, 1);
    visitedEnd.put(endWord, 1);

    while (!Q_begin.isEmpty() && !Q_end.isEmpty()) {

      // One hop from begin word
      int ans = visitWordNode(Q_begin, visitedBegin, visitedEnd);
      if (ans > -1) {
        return ans;
      }

      // One hop from end word
      ans = visitWordNode(Q_end, visitedEnd, visitedBegin);
      if (ans > -1) {
        return ans;
      }
    }

    return 0;
  }

  // 单向bfs
  public int ladderLength_2(String beginWord, String endWord, List<String> wordList) {

    // Since all words are of same length.
    this.L = beginWord.length();

    wordList.forEach(
        word -> {
          for (int i = 0; i < L; i++) {
            // Key is the generic word
            // Value is a list of words which have the same intermediate generic word.
            String newWord = word.substring(0, i) + '*' + word.substring(i + 1, L);
            ArrayList<String> transformations =
                allComboDict.getOrDefault(newWord, new ArrayList<>());
            transformations.add(word);
            allComboDict.put(newWord, transformations);
          }
        });

    // Queue for BDFS
    Queue<Pair> Q = new LinkedList<>();
    Q.add(new Pair(beginWord, 1));

    // Visited to make sure we don't repeat processing same word.
    HashMap<String, Boolean> visited = new HashMap<>();
    visited.put(beginWord, true);

    while (!Q.isEmpty()) {
      Pair node = Q.remove();
      String word = node.getKey();
      int level = node.getValue();
      for (int i = 0; i < L; i++) {

        // Intermediate words for current word
        String newWord = word.substring(0, i) + '*' + word.substring(i + 1, L);

        // Next states are all the words which share the same intermediate state.
        for (String adjacentWord : allComboDict.getOrDefault(newWord, new ArrayList<String>())) {
          // If at any point if we find what we are looking for
          // i.e. the end word - we can return with the answer.
          if (adjacentWord.equals(endWord)) {
            return level + 1;
          }
          // Otherwise, add it to the BDFS Queue. Also mark it visited
          if (!visited.containsKey(adjacentWord)) {
            visited.put(adjacentWord, true);
            Q.add(new Pair(adjacentWord, level + 1));
          }
        }
      }
    }

    return 0;
  }

  public int ladderLength_1(String beginWord, String endWord, List<String> wordList) {

    // 预处理
    // Since all words are of same length.
    int L = beginWord.length();

    // Dictionary to hold combination of words that can be formed,
    // from any given word. By changing one letter at a time.
    HashMap<String, ArrayList<String>> allComboDict = new HashMap<>();

    wordList.forEach(
        word -> {
          for (int i = 0; i < L; i++) {
            // Key is the generic word
            // Value is a list of words which have the same intermediate generic word.
            String newWord = word.substring(0, i) + '*' + word.substring(i + 1, L);
            ArrayList<String> transformations =
                allComboDict.getOrDefault(newWord, new ArrayList<>());
            transformations.add(word);
            allComboDict.put(newWord, transformations);
          }
        });


    // corner cases
    // what if endword is not a transformed word!!
    if (!wordList.contains(endWord)) {
      return  0;
    }
    // begin or end word is empty
    if (endWord.length() == 0 || beginWord.length() == 0) {
      return  0;
    }
    HashMap<String, Boolean> beginMap = new HashMap<>(), endMap = new HashMap<>();

    int len = 1;
    HashMap<String, Boolean> visited = new HashMap<>();

    beginMap.put(beginWord, true);
    endMap.put(endWord, true);

    while (!beginMap.isEmpty() && !endMap.isEmpty()) {
      // 始终保持 beginset's length > endset's length
      if (beginMap.size() > endMap.size()) {
        HashMap<String, Boolean> map = beginMap;
        beginMap = endMap;
        endMap = map;
      }

      HashMap<String, Boolean> temp = new HashMap<>();
      Iterator<Map.Entry<String, Boolean>> iterator = beginMap.entrySet().iterator();

      while (iterator.hasNext()) {
        String word = iterator.next().getKey();
        for (int i = 0; i < L; i++) {
          // Intermediate words for current word
          String newWord = word.substring(0, i) + '*' + word.substring(i + 1, L);
          // Next states are all the words which share the same intermediate state.
          for (String adjacentWord : allComboDict.getOrDefault(newWord, new ArrayList<String>())) {
              if (endMap.containsKey(adjacentWord)) {
                return len + 1;
              }
              if (!visited.containsKey(adjacentWord)) {
                temp.put(adjacentWord, true);
                visited.put(adjacentWord, true);
              }
          }
        }

      }

      beginMap = temp;

      len++;
    }

    return 0;
  }
}

package com.bigchange.algorithm.leetcode.problems;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

/**
 * User: JerryYou
 *
 * Date: 2019-05-31
 *
 * Copyright (c) 2018 devops
 *
 * <<licensetext>>
 */
public class OpentheLock752 {

  private int[] position = {0,1,2,3};
  private  Map<Character, Integer> asc = new HashMap<>();
  private  Map<Character, Integer> desc = new HashMap<>();
  private Queue<String> tryQueue = new LinkedList();
  private Set<String> visit = new HashSet();
  Set<String> dead = new HashSet();

  public void prepare() {
    // clockwise
    asc.put('0', 1);
    asc.put('1', 2);
    asc.put('2', 3);
    asc.put('3', 4);
    asc.put('4', 5);
    asc.put('5', 6);
    asc.put('6', 7);
    asc.put('7', 8);
    asc.put('8', 9);
    asc.put('9', 0);
    // anti-clockwise
    desc.put('0',9);
    desc.put('1',0);
    desc.put('2',1);
    desc.put('3',2);
    desc.put('4',3);
    desc.put('5',4);
    desc.put('6',5);
    desc.put('7',6);
    desc.put('8',7);
    desc.put('9',8);
  }

  public int openLock(String[] deadends, String target) {
    int count = 0;
    prepare();
    dead.addAll(Arrays.asList(deadends));
    visit.addAll(dead);
    String start = "0000";
    visit.add(start);
    tryQueue.add(start);
    while(!tryQueue.isEmpty()) {
      // all possible
      int level = tryQueue.size();
      for (int i= 0; i< level; i++) {
        String tryLock = tryQueue.poll();
        if (dead.contains(tryLock)) {
          continue;
        }
        if (target.equals(tryLock)) {
          return count;
        }
        for (int pos : position) {
          // get clockwise try
          String next = tryLock.substring(0,pos) + asc.get(tryLock.charAt(pos))+tryLock.substring(pos + 1);
          // anti-clockwise
          String pre = tryLock.substring(0,pos) + desc.get(tryLock.charAt(pos))+tryLock.substring(pos + 1);
          if (!visit.contains(next)) {
            tryQueue.add(next);
            visit.add(next);
          }
          if (!visit.contains(pre)) {
            tryQueue.add(pre);
            visit.add(pre);
          }
        }
      }
      count++;
    }
    return -1;
  }

}

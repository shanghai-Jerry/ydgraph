package com.bigchange.algorithm.leetcode.problems;

import java.util.Deque;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * User: JerryYou
 *
 * Date: 2019-06-13
 *
 * Copyright (c) 2018 devops
 *
 * <<licensetext>>
 */
public class KeysandRooms841 {

  public boolean canVisitAllRooms(List<List<Integer>> rooms) {
    int numRoom = rooms.size();
    Set<Integer> all_keys = new HashSet<>();
    // 1ms, much fast then bdfs, but never mind the differences
    enterRoomRecursion(rooms, all_keys, 0);
    // can we get all keys for rooms?
    return all_keys.size() == numRoom;
  }


  private void enterRoomRecursion(List<List<Integer>> rooms, Set<Integer> all_keys,
                         int room) {
    List<Integer> keys = rooms.get(room);
    all_keys.add(room);
    for (int i = 0; i < keys.size(); i++) {
      int key = keys.get(i);
      if (!all_keys.contains(key)) {
        enterRoomRecursion(rooms, all_keys, key);
      }
    }
  }

  public boolean canVisitAllRooms_1(List<List<Integer>> rooms) {
    int numRoom = rooms.size();
    Set<Integer> all_keys = new HashSet<>();
    Deque<Integer> deque = new LinkedList<>();
    enterRoom(rooms, deque, all_keys, 0);
    all_keys.add(0);
    while(!deque.isEmpty()) {
      // bfs - 2ms
      // int key = deque.poll();
      // dfs - same time but less memory
      int key = deque.pollLast();
      enterRoom(rooms, deque, all_keys, key);
    }
    // can we get all keys for rooms?
    return all_keys.size() == numRoom;
  }

  // enter room to get keys
  private void enterRoom(List<List<Integer>> rooms, Deque<Integer> deque, Set<Integer> all_keys,
                         int room) {
    List<Integer> keys = rooms.get(room);
    for (int i = 0; i < keys.size(); i++) {
      int key = keys.get(i);
      if (!all_keys.contains(key)) {
        all_keys.add(key);
        deque.add(key);
      }
    }
  }
}

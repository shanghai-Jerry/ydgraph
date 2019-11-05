package com.bigchange.algorithm.leetcode.problems;

import java.util.Arrays;

/**
 * User: JerryYou
 *
 * Date: 2019-11-05
 *
 * Copyright (c) 2018 devops
 *
 * <<licensetext>>
 */
public class MyHashMap706 {

  private int TABLE_SIZE = 1000001;

  private int[] hashTable;

  private int hashFunction(int key) {
    return  key % TABLE_SIZE;
  }

  /** Initialize your data structure here. */
  public MyHashMap706() {
    hashTable = new int[TABLE_SIZE];
    // 初始化为-1
    Arrays.fill(hashTable, -1);
  }

  /** value will always be non-negative. */
  public void put(int key, int value) {
    hashTable[hashFunction(key)] = value;
  }

  /** Returns the value to which the specified key is mapped, or -1 if this map contains no mapping for the key */
  public int get(int key) {
    return  hashTable[hashFunction(key)];
  }

  /** Removes the mapping of the specified value key if this map contains a mapping for the key */
  public void remove(int key) {
    hashTable[hashFunction(key)] = -1;
  }
}

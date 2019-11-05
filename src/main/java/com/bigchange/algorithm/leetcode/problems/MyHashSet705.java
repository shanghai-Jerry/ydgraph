package com.bigchange.algorithm.leetcode.problems;

/**
 * User: JerryYou
 *
 * Date: 2019-11-05
 *
 * Copyright (c) 2018 devops
 *
 * <<licensetext>>
 */
public class MyHashSet705 {

  private int TABLE_SIZE = 1000001;

  private boolean[] hashTable;

  private int hashFunction(int key) {
    return  key % TABLE_SIZE;
  }

  /** Initialize your data structure here. */
  public MyHashSet705() {
    hashTable = new boolean[TABLE_SIZE];
  }

  public void add(int key) {
    hashTable[hashFunction(key)] = true;
  }

  public void remove(int key) {
    hashTable[hashFunction(key)] = false;
  }

  /** Returns true if this set contains the specified element */
  public boolean contains(int key) {
    return hashTable[hashFunction(key)];
  }

}

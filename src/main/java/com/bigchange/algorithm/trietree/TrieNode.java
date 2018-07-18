package com.bigchange.algorithm.trietree;

/**
 * User: JerryYou
 *
 * Date: 2018-07-18
 *
 * Copyright (c) 2018 devops
 *
 * <<licensetext>>
 */
public class TrieNode {
  private int num; // 由根到该节点的字符串模式出现的次数
  private TrieNode[] child; // 子节点
  private char val; // 节点的值
  private int SIZE = 127; // 字符集大小
  private boolean isEnd; // 是不是最后一个节点
  public TrieNode() {
    this.num = 1;
    child = new TrieNode[SIZE];
    isEnd = false;
  }

  public int getNum() {
    return num;
  }

  public void setNum(int num) {
    this.num = num;
  }

  public TrieNode[] getChild() {
    return child;
  }

  public void setChild(TrieNode[] child) {
    this.child = child;
  }

  public char getVal() {
    return val;
  }

  public void setVal(char val) {
    this.val = val;
  }

  public int getSIZE() {
    return SIZE;
  }

  public void setSIZE(int SIZE) {
    this.SIZE = SIZE;
  }

  public boolean isEnd() {
    return isEnd;
  }

  public void setEnd(boolean end) {
    isEnd = end;
  }
}

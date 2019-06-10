package com.bigchange.algorithm.leetcode.tutorials;

/**
 * User: JerryYou
 *
 * Date: 2019-05-27
 *
 * Copyright (c) 2018 devops
 *
 * // 1. Initialize a queue.
 *  Queue<Integer> q = new LinkedList();
 *
 * <<licensetext>>
 */
public class MyCircularQueue {

  private int []data;
  private int size;
  private int head = -1;
  private int tail = -1;

  /** Initialize your data structure here. Set the size of the queue to be k. */
  public MyCircularQueue(int k) {
    data = new int[k] ;
    size = k;

  }

  /** Insert an element into the circular queue. Return true if the operation is successful. */
  public boolean enQueue(int value) {
    if (isFull()) {
      return  false;
    }
    if (isEmpty()) {
      head = 0;
    }
    tail =  (tail + 1) % size;
    data[tail] = value;
    return  true;
  }

  /** Delete an element from the circular queue. Return true if the operation is successful. */
  public boolean deQueue() {
    if(isEmpty()) {
      return  false;
    }
    if (head == tail) {
      head = -1;
      tail = -1;
      return true;
    }
    head = (head +1 ) % size;
    return  true;
  }

  /** Get the front item from the queue. */
  public int Front() {
    if (isEmpty()) {
      return  -1;
    }
    return data[head];
  }

  /** Get the last item from the queue. */
  public int Rear() {
    if (isEmpty()) {
      return -1;
    }
    return data[tail];
  }

  /** Checks whether the circular queue is empty or not. */
  public boolean isEmpty() {
    return head == -1;
  }

  /** Checks whether the circular queue is full or not. */
  public boolean isFull() {
    return (tail +1) % size == head ;
  }
}

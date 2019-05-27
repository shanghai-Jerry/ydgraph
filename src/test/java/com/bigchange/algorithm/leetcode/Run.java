package com.bigchange.algorithm.leetcode;


/**
 * User: JerryYou
 *
 * Date: 2019-05-08
 *
 * Copyright (c) 2018 devops
 *
 * <<licensetext>>
 */
public class Run {

  public void println(Object o) {
    System.out.println(o);
  }

  public static void main(String[] args) {
    Run run = new Run();
    MyCircularQueue circularQueue = new MyCircularQueue(3); // set the size to be 3
    run.println(circularQueue.enQueue(1));  // return true
    run.println(circularQueue.Rear());  // return 3
    run.println(circularQueue.Rear());
    run.println(circularQueue.Rear());
    run.println(circularQueue.isFull());  // return true
    run.println(circularQueue.deQueue());  // return true
    run.println(circularQueue.enQueue(4));  // return true
    run.println(circularQueue.Rear());  // return 4
  }
}

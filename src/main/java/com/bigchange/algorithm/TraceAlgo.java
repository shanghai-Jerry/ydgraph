package com.bigchange.algorithm;

import io.vertx.core.logging.Logger;

/**
 * User: JerryYou
 *
 * Date: 2018-11-02
 *
 * Copyright (c) 2018 devops
 *
 * <<licensetext>>
 */
public class TraceAlgo {

  public void move(int n, String a, String c) {
    System.out.println("[move] => " + a + " to " + c);
  }

  /***
   * 汉诺塔的移动问题（递归算法）
   * @param n
   * @param a
   * @param b
   * @param c
   */
  public void hanoi(int n, String a, String b, String c) {
    if (n == 1) { // 终止条件
      move(n, a, c);
    } else {
      hanoi(n-1, a,c,b); // 借助c将n-1个盘子从a移动到b（n-1个盘子顺序保持不变）
      move(n, a, c); // 将最后一个移动到c
      hanoi(n-1, b,a, c); // 同样，借助a将b中盘子移动到c
    }
  }

  public static void main(String[] args) {
    Logger logger = io.vertx.core.logging.LoggerFactory.getLogger(TraceAlgo.class);
    TraceAlgo traceAlgo = new TraceAlgo();
    traceAlgo.hanoi(4, "a", "b", "c");
  }


}

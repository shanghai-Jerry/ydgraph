package com.bigchange.algorithm.leetcode.problems;

import java.util.HashMap;
import java.util.Map;

/**
 * User: JerryYou
 *
 * Date: 2019-05-16
 *
 * Copyright (c) 2018 devops
 *
 * <<licensetext>>
 */
public class RobotBoundedInCircle1041 {

  public boolean isRobotBounded(String instructions) {

    int [][]direct = new int[2][2];
    // 0 - add, 1 - reverse add
    int []directOpt = new int[]{-1, 1};
    // x
    direct[0] = directOpt;
    // y
    direct[1] = directOpt;

    // AXIS AND THEN DIRECT
    int []nowInDirect = new int[]{1, 1};

    int x = 0;
    int y = 0;

    char [] opts =  instructions.toCharArray();
    int count = 0;
    for (int i = 0; i < opts.length; i ++) {
      int d = nowInDirect[0];
      int o = nowInDirect[1];
      if (opts[i] == 'G') {
        count ++;
        if (d == 0) {
          x += o;
        } else {
          y += o;
        }
        if (count == opts.length) {
          return false;
        }
      } else if (opts[i] == 'L') {
        if (d == 1) {
          nowInDirect[1] = -nowInDirect[1];
          nowInDirect[0] = 1 - nowInDirect[0];
        } else {
          nowInDirect[0] = 1 + nowInDirect[0];
        }
      } else {
        // 'R'
        if (d == 0 ) {
          nowInDirect[0] = 1 + nowInDirect[0];
          nowInDirect[1] = -nowInDirect[1];
        } else {
          nowInDirect[0] = 1 - nowInDirect[0];
        }

      }
    }
   // if we travel some distance, in the meanwhile facing the north, sky is the limit!
    if (x * x + y * y > 0 && nowInDirect[0] == 1 && nowInDirect[1] == 1)
      return  false;
    else
      return  true;

  }
}

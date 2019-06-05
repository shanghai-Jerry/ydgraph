package com.bigchange.algorithm.leetcode.problems;

import java.util.HashMap;
import java.util.Map;

/**
 * User: JerryYou
 *
 * Date: 2019-05-08
 *
 * Copyright (c) 2018 devops
 *
 * <<licensetext>>
 *
 * F(0) = 0,   F(1) = 1
 * F(N) = F(N - 1) + F(N - 2), for N > 1.
 *
 *  DP has exactly the same algorithm/solution as recursion (i.e. If there is a recursive solution for a problem,
 *  then there must also be a DP solution with the same algorithm.
 *  The only difference is that DP stores the recursive calls so that we do not make multiple duplicate recursive calls.
 *  As you know, it is time-consuming and useless
 */
public class FibonacciSequence509 {

  // Memoization
  Map<Integer, Integer> hashTable = new HashMap<>();

  public int fib(int N) {

    if (hashTable.containsKey(N)) {
      return hashTable.get(N);
    }
    int res;
    if (N < 2) {
      return N;
    } else {
      res = fib(N-1) + fib(N-2);
      hashTable.put(N, res);
    }

    return res;
  }
}

package com.bigchange.algorithm.leetcode.problems;

/**
 * User: JerryYou
 *
 * Date: 2019-05-13
 *
 * Copyright (c) 2018 devops
 *
 * <<licensetext>>
 */
public class KthSymbolInGrammar779 {

  // row 1: 0
  // row 2: 01
  // row 3: 0110
  // row 4: 01101001 === 0110 + 0110的取反结果
  // We see that, for any level N, the first half of the string is the same as the string in N-1,
  // the next half is just complement of it (opposite number). The total number of items in level N
  // is 2^N.
  // The half mark of the string is marked by [2^(N-1)]-th item. So, for any level N:
  // if K is in the first half, it is same as the Kth element in level N-1
  // if K is in the second half, it is the complement of the number in [K-2^(N-1)]-th position in
  // level N-1
  public int kthGrammar(int N, int K) {
    if (N == 1) {
      if (K == 1) {
        return 0;
      } else {
        return  1;
      }
    }
    int half = (int)Math.pow(2, N-1);

    if (K<=half) {
      return kthGrammar(N-1, K);
    } else {
      int res = kthGrammar(N-1, K-half);
      // opposite number 0 -> 1, 1 -> 0
      if (res == 0) {
        return  1;
      } else {
        return  0;
      }
    }
  }

  public int kthGrammar_2(int N, int K) {
    // what, how do you find this ?
    return Integer.bitCount(K-1) & 1;
  }

  public int kthGrammar_1(int N, int K) {
    int power = (int)Math.pow(2, N-1);
    return kthGrammarRecursion(N, K, 1, false, power);
  }
  public int kthGrammarRecursion(int N, int K, int current, boolean bit, int power) {
    if (N==current)
      return (bit)?1:0;
    else
      return kthGrammarRecursion(N, (K<=power/2)?K:K-power/2, current+1,(K<=power/2)?bit:!bit, power/2);
  }
}

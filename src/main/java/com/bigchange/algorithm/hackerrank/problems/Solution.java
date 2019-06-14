package com.bigchange.algorithm.hackerrank.problems;

/**
 * User: JerryYou
 *
 * Date: 2019-06-14
 *
 * Copyright (c) 2018 devops
 *
 * <<licensetext>>
 */
public class Solution {

  /**
   * What we need to solve is, regardless of the case, if b is a subsequence of a with the
   * constraint that a can only discard lower case characters. Therefore, if we want to know if
   * b[0, i] is an abbreviation of a[0, j], we have two cases to consider:
   *
   * a[j] is a lower case character.
   * if uppercase(a[j]) == b[i], either b[i - 1]is an abbreviation of a[0, j - 1] or b[i - 1] is
   * an abbreviation of a[0, j], b[0, i] is an abbreviation of a[0, j].
   *
   * else if b[0, i] is an abbreviation of a[0, j -1], b[0, i] is an abbreviation of a[0, j].
   *
   * else, b[0, i] cannot be an abbreviation of a[0, j].
   *
   * a[j] is a upper case character.
   * if a[j] == b[i] and b[0, i - 1] is an abbreviation of a[0, j - 1], b[0, i] is an
   * abbreviation of a[0, j].
   *
   * else b[0, i] cannot be an abbreviation of a[0, j].
   * @param a
   * @param b
   * @return
   */

  static String abbreviation(String a, String b) {
    boolean[][] isValid = new boolean[a.length()+1][b.length()+1];
    // initializing the first raw to all false; ie. if b is
    // not empty, isValid will always be false
    isValid[0][0] = true;
    // array initialization - if a is non-empty but b is empty,
    // then isValid == true iff remaining(a) != contain uppercase
    boolean containsUppercase = false;
    for (int k = 1; k <= a.length(); k++) {
      int i = k - 1;
      // if the pointer at string a is uppercase
      if (a.charAt(i) <= 90 && a.charAt(i) >= 65 || containsUppercase) {
        containsUppercase = true;
        isValid[k][0] = false;
      }
      else isValid[k][0] = true;
    }
    // tabulation from start of string
    for (int k = 1; k <= a.length(); k++) {
      for (int l = 1; l <= b.length(); l++) {
        int i = k - 1; int j = l - 1;
        // when the characters are equal, set = previous character bool.
        if (a.charAt(i) == b.charAt(j)) {
          isValid[k][l] = isValid[k-1][l-1];
          continue;
        }// elif uppercase a == b, set = prev character bool. or just eat a.
        else if ((int) a.charAt(i) - 32 == (int) b.charAt(j)) {
          isValid[k][l] = isValid[k-1][l-1] || isValid[k-1][l];
          continue;
        }
        // elif a is uppercase and no more b, or uppercase a is not b, then false
        else if (a.charAt(i) <= 90 && a.charAt(i) >= 65) {
          isValid[k][l] = false;
          continue;
        }
        //else just eat a
        else {
          isValid[k][l] = isValid[k-1][l];
          continue;
        }
      }
    }
    return isValid[a.length()][b.length()]? "YES" : "NO";
  }

}

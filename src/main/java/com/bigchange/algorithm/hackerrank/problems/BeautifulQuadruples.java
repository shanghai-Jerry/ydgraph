package com.bigchange.algorithm.hackerrank.problems;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class BeautifulQuadruples {

  /*
   * Complete the beautifulQuadruples function below.
   */
  static int beautifulQuadruples(int a, int b, int c, int d) {
    /*
     * Write your code here.
     */
    int [] ret = new int[4];
    Set<String> visit = new HashSet<>();
    int count = 0;
    for (int i = 1; i <= a; i++) {
      for (int j = 1; j <= b; j++) {
        for (int k = 1; k <= c; k++) {
          for (int m = 1; m <= d; m++) {
            int index = 0;
            String v = "";
            int r = i ^ j ^ k ^ m;
            ret[index++] = i;
            ret[index++] = j;
            ret[index++] = k;
            ret[index++] = m;
            Arrays.sort(ret);
            for(int n = 0; n < 4; n++) {
              v += ret[n];
            }
            if (r != 0) {
              if (!visit.contains(v)) {
                count++;
                visit.add(v);
              }
            }
          }
        }
      }
    }
    return count;

  }

}
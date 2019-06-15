package com.bigchange.algorithm.hackerrank;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

public class Solution {

  private static final Scanner scanner = new Scanner(System.in);

  public static void main(String[] args) throws IOException {
    BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(System.getenv("OUTPUT_PATH")));

    String[] abcd = scanner.nextLine().split(" ");

    int a = Integer.parseInt(abcd[0].trim());

    int b = Integer.parseInt(abcd[1].trim());

    int c = Integer.parseInt(abcd[2].trim());

    int d = Integer.parseInt(abcd[3].trim());

    int result = beautifulQuadruples(a, b, c, d);

    bufferedWriter.write(String.valueOf(result));
    bufferedWriter.newLine();

    bufferedWriter.close();
  }


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

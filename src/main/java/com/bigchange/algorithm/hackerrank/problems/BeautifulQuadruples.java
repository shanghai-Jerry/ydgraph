package com.bigchange.algorithm.hackerrank.problems;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Scanner;

public class BeautifulQuadruples {

  private static final Scanner scanner = new Scanner(System.in);

  public static void main(String[] args) throws IOException {

    System.setProperty("OUTPUT_PATH", "./out_put.txt");

    BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(System.getProperty("OUTPUT_PATH")));

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
   * TimeExceeded
   */
  static int beautifulQuadruples(int a, int b, int c, int d) {
    /*
     * Write your code here.
     * new solution: s.t. i <= j <= k <= m
     */
    int [] abcd = new int[4];
    abcd[0] = a;
    abcd[1] = b;
    abcd[2] = c;
    abcd[3] = d;
    Arrays.sort(abcd);
    int count = 0;
    for (int i = 1; i <= abcd[0]; i++) {
      for (int j = i; j <= abcd[1]; j++) {
        for (int k = j; k <= abcd[2]; k++) {
          for (int m = k; m <= abcd[3]; m++) {
            int r = i ^ j ^ k ^ m;
            if (r != 0) {
              count++;
            }
          }
        }
      }
    }
    return count;

  }

}
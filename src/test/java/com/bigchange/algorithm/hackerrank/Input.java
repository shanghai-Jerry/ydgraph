package com.bigchange.algorithm.hackerrank;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class Input {
  private static final Scanner scanner = new Scanner(System.in);

  public static void main(String[] args) throws IOException {

    System.setProperty("OUTPUT_PATH", "./out_put.txt");

    BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(System.getProperty("OUTPUT_PATH")));

  }
}
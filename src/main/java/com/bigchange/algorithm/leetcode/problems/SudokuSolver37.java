package com.bigchange.algorithm.leetcode.problems;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * User: JerryYou
 *
 * Date: 2019-06-19
 *
 * Copyright (c) 2018 devops
 *
 * <<licensetext>>
 */
public class SudokuSolver37 {

  // i don't care about the time complexity cause i am great if it can be accepted at last
  char[][] ret;
  public void solveSudoku(char[][] board) {
    // 数据预处理
    ret = new char[board.length][board[0].length];
    prepared();
    //从第一个empty cell开始
    backtracking(board, 0, 0);
    int x = board.length;
    int y = board[0].length;
    for (int i = 0; i < x; i++) {
      for (int j = 0; j < y; j++) {
        board[i][j] = ret[i][j];
      }
    }
    // print_out(board);
  }
  private Map<Integer, int[]> subGrid = new HashMap<>();

  private void prepared() {
    for (int i = 0; i < 9; i++) {
      int[] item;
      if (i < 3) {
        item = new int[]{0,1,2};
        subGrid.put(i, item);
      } else if (i >= 3 && i < 6 ) {
        item = new int[]{3,4,5};
        subGrid.put(i, item);
      } else {
        item = new int[]{6,7,8};
        subGrid.put(i, item);
      }
    }
  }
  private void backtracking(char[][] board,int row, int column) {
    if (board[row][column] == '.') {
      for (int i = 1; i <= 9; i++) {
        char candidate = Character.forDigit(i, 10);
        if (isValid(board, candidate, row, column)) {
          place_candidate(board, candidate, row, column);
          if (row == board.length - 1 && column == board[0].length - 1) {
            // fill over and Sudoku puzzle will have a unique solution.
            print_out(board);
            for (int m = 0; m < board.length; m++) {
              char []copyOfRange = Arrays.copyOfRange(board[m], 0, board[i].length);
              ret[m] = copyOfRange;
            }
            // if return, it will not to try another case, much faster
            return;
          } else {
            if (column + 1 == board[0].length) {
              backtracking(board, row + 1, 0);
            } else {
              backtracking(board, row, column + 1);
            }
          }
          remove_candidate(board, '.', row, column);
        }
      }
    } else {
      if (row == board.length - 1 && column == board[0].length - 1) {
        // fill over and Sudoku puzzle will have a unique solution.
        print_out(board);
        for (int i = 0; i < board.length; i++) {
          char []copyOfRange = Arrays.copyOfRange(board[i], 0, board[i].length);
          ret[i] = copyOfRange;
        }
        // if return, it will not to try another case, much faster
        return;
      } else {
        if (column + 1 == board[0].length) {
          backtracking(board, row + 1, 0);
        } else {
          backtracking(board, row, column + 1);
        }
      }
    }
  }

  private boolean isValid(char[][] board, char candidate, int row, int column) {

    // check row and column
    for (int i = 0; i < board.length; i++) {
      if (board[i][column] == candidate) {
        return false;
      }
      if (board[row][i] == candidate) {
        return false;
      }
    }
    // check sub grid
    int[] rows = subGrid.get(row);
    int[] cols = subGrid.get(column);
    for (int i = 0; i < rows.length; i++) {
      for (int j = 0; j < cols.length; j++) {
        if (board[rows[i]][cols[j]] == candidate) {
          return false;
        }
      }
    }
    return true;
  }

  private void place_candidate(char [][]board, char candidate, int row, int column) {
    board[row][column] = candidate;
  }
  private void remove_candidate(char [][]board, char candidate, int row, int column) {
    place_candidate(board, candidate, row, column);
  }

  private void print_out(char [][]board) {
    int x = board.length;
    int y = board[0].length;
    System.out.println("matrix:\n");
    for (int i = 0; i < x; i++) {
      for (int j = 0; j < y; j++) {
        System.out.print(board[i][j] + " ");
      }
      System.out.println("\n");
    }
  }

  /*
    "53..7....",
    "6..195...",
    ".98....6.",
    "8...6...3",
    "4..8.3..1",
    "7...2...6",
    ".6....28.",
    "...419..5",
    "....8..79"
   */
  public void setInputString(String [] sudu) {
    char[][] board = new char[sudu.length][sudu.length];
    for (int i  = 0; i < 9; i++) {
      for (int j = 0; j < sudu[i].length(); j ++) {
        board[i][j] = sudu[i].charAt(j);
      }
    }
    solveSudoku(board);
  }

  public void getInput() {
    String [] strings = new String[9];
    Scanner scanner = new Scanner(System.in);
    for (int i = 0; i< 9; i++) {
      String row = scanner.nextLine();
      strings[i] = row;
    }
    setInputString(strings);
  }

  private String[] convertInput(char[][] board) {
    // all set '.'
    int x = board.length;
    int y = board[0].length;
    String [] input = new String[x];
    for (int i = 0; i < x; i++) {
      String row = "";
      for (int j = 0; j < y; j++) {
        row += board[i][j];
      }
      System.out.print( "\"" + row + "\"" + ",");
      input[i] = row;
    }
    return input;
  }
}

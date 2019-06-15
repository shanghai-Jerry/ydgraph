package com.bigchange.algorithm.hackerrank.problems;

import java.util.Arrays;

public class LilysHomework {

  // Complete the lilysHomework function below.
  static int lilysHomework(int[] arr) {
    // 分别算倒序和正序的后的交换次数，取最小交换次数为最后结果
    // 排序后可以使得the sum of |arr[i] - arr[i - 1]| for 0 < i < n is minimal
    int[] arr2 = Arrays.copyOfRange(arr, 0, arr.length);
    int[] newArray = Arrays.copyOfRange(arr, 0, arr.length);
    // 正序
    Arrays.sort(newArray);
    int count = calSwap(arr2, newArray);
    arr2 = arr;
    // 倒序
    for (int i = 0, j = newArray.length - 1; i < j; i++,j--) {
      int t = newArray[i];
      newArray[i] = newArray[j];
      newArray[j] = t;
    }
    int count2 = calSwap(arr2, newArray);
    if (count < count2) {
      return count;
    } else {
      return  count2;
    }
  }

  // optimize
  static int calSwap(int [] arr, int []newArr) {
    int count = 0;
    for (int i = 0; i < arr.length; i++) {
      if (newArr[i] != arr[i]) {
        for (int j = i + 1; j < arr.length; j ++) {
          if (arr[j] == newArr[i]) {
            int tmp = arr[j];
            arr[j] = arr[i];
            arr[i] = tmp;
            count++;
            break;
          }
        }
      }
    }
    return count;
  }


}
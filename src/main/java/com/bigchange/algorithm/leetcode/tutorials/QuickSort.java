package com.bigchange.algorithm.leetcode.tutorials;

/**
 * User: JerryYou
 *
 * Date: 2019-06-14
 *
 * Copyright (c) 2018 devops
 *
 * <<licensetext>>
 */
public class QuickSort {

  private int[] arr;

  public int[] sortArray(int[] nums) {
    arr = nums;
    return quickSort(nums);
  }

  private int[] quickSort(int[] nums) {
    Qsort(0,arr.length - 1);
    return arr;
  }

  private void Qsort(int low, int high) {
    if(low < high) {
      int pivotloc = partition(low, high);
      Qsort(low, pivotloc-1);
      Qsort(pivotloc+1,high);
    }
  }

  //  按pivotkey位置排序，左边小于，右边大于
  private int partition(int low, int high) {
    int pivotkey = arr[low];
    while(low < high) {
      while(low < high && arr[high] >= pivotkey)
        --high;
      arr[low] = arr[high];
      while(low < high && arr[low] <= pivotkey)
        ++low;
      arr[high] = arr[low];
    }
    arr[low] = pivotkey;
    return low;
  }
}

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

  private int partition(int [] lst, int lo, int hi) {
    /*
      Picks the last element hi as a pivot
      and returns the index of pivot value in the sorted array */
    int pivot = lst[hi];
    int i = lo;
    for (int j = lo; j < hi; ++j) {
      if (lst[j] < pivot) {
        int tmp = lst[i];
        lst[i] = lst[j];
        lst[j] = tmp;
        i++;
      }
    }
    int tmp = lst[i];
    lst[i] = lst[hi];
    lst[hi] = tmp;
    return i;
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

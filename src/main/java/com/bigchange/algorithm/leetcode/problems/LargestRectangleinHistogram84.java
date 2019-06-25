package com.bigchange.algorithm.leetcode.problems;

import java.util.Arrays;
import java.util.Stack;

/**
 * User: JerryYou
 *
 * Date: 2019-06-24
 *
 * Copyright (c) 2018 devops
 *
 * <<licensetext>>
 */
public class LargestRectangleinHistogram84 {

  // as long as we try to add a new bar with height shorter than the current bar’s height to the rectangle.
  // It will decrease the area of the maximum rectangle
  // left: the first coordinate of the bar to the left with height h[l] < h[i].
  // right : the first coordinate of the bar to the right with height h[r] < h[i].
  public int largestRectangleArea(int[] heights) {
    int max_rect = 0, len = heights.length;

    int[] PLE = new int[len], NLE = new int[len];
    Arrays.fill(NLE, len);

    // monotonously increasing stack
    int[] stk = new int[len];
    int k = -1;
    for (int i = 0; i < len; i ++) {
      while (k != -1 && heights[i] <= heights[stk[k]]) {
        k--;
      }
      PLE[i] = k == -1 ? -1 : stk[k];
      stk[++k] = i;
    }
    k = -1;
    for (int i = 0; i < len; i ++) {
      while (k != -1 && heights[i] < heights[stk[k]]) {
        NLE[stk[k--]] = i;
      }
      stk[++k] = i;
    }


    for (int i = 0; i < len; i++) {
      //PLE[i] the first left bar to the bar i shorter than bar i
      //NLE[i] the first right bar to the bar i shorter than bar i
      max_rect = Math.max(max_rect, (NLE[i] - PLE[i] - 1) * heights[i]);
    }
    return max_rect;
  }

  // divide and conquer, but not enough efficient
  public int largestRectangleArea_divide_and_conquer(int[] heights) {
    return get_largest_rec(heights, 0, heights.length - 1);
  }
  int get_largest_rec(int []heights, int start, int end){
    if(start < 0 || end > heights.length - 1 || end < start){
      return 0;
    }
    if(start == end){
      return heights[start];
    }

    int h_min = heights[start];
    int i_min = start;
    // 找到最矮的那个方形
    for(int i = start + 1; i <= end; ++i){
      if(h_min > heights[i]){
        h_min = heights[i];
        i_min = i;
      }
    }
    // 计算左边： 解在左边，递归求解
    int l_max = get_largest_rec(heights, start, i_min - 1);
    // 计算右边： 解在右边，递归求解
    int r_max = get_largest_rec(heights, i_min + 1, end);
    // 计算当前值： 当前方形就是，这就是解
    int m_max = h_min * (end - start + 1);
    // 取最大
    int ans = Math.max(Math.max(l_max, r_max), m_max);

    return ans;
  }

  // efficient
  // Each rectangle have left boundary and right boundary.
  // If we can get each position's boundaries, we can know the rectangle which this position can
  // build. So we use Stack to loop heights array while recoding each position left point(the first
  // position which is smaller than this position) and right point(the first position which is
  // smaller than this position).
  // For example:
  // heights: [2,1,5,6,2,3]
  // We can know lefts: [-1,-1,1,2,1,4] and rights: [1,6,4,4,6,6]
  // Finally, for each position, we calculate its area and return the max one.
  public int largestRectangleArea_stack(int[] heights) {
    int m = heights.length;
    int[] lefts = new int[m];
    Arrays.fill(lefts, -1);
    int[] rights = new int[m];
    Arrays.fill(rights, m);
    Stack<Integer> stack = new Stack();
    for (int i=0; i<m; i++) {
      if (!stack.empty()&&heights[stack.peek()]>heights[i]) {
        rights[stack.pop()] = i;
        while(!stack.empty()&&heights[i]<heights[stack.peek()]) {
          rights[stack.pop()] = i;
        }
      }
      stack.push(i);
    }
    stack.clear();
    for (int i=m-1; i>=0; i--) {
      if (!stack.empty()&&heights[i]<heights[stack.peek()]) {
        lefts[stack.pop()] = i;
        while(!stack.empty()&&heights[i]<heights[stack.peek()]) {
          lefts[stack.pop()] = i;
        }
      }
      stack.push(i);
    }
    int max = 0;
    for (int i=0; i<m; i++) {
      max = Math.max(max, heights[i]*(rights[i]-lefts[i]-1));
    }
    return max;
  }

  // of course: time complexity is high, any efficient way?
  public int largestRectangleArea_custom(int[] heights) {
    int ret = 0;
    int min = 0;
    int sum = 0;
    for (int i = 0; i < heights.length; i++) {
      for (int j = i; j < heights.length; j++) {
        if (i == j) {
          min = heights[j];
        }
        int add_item = heights[j];
        if (add_item < min) {
          min = add_item;
        }
        sum = (j - i + 1) * min;
        if (ret < sum) {
          ret = sum;
        }
      }
    }
    return ret;
  }

}

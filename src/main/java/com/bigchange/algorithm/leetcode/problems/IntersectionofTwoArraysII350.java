package com.bigchange.algorithm.leetcode.problems;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User: JerryYou
 *
 * Date: 2019-11-06
 *
 * Copyright (c) 2018 devops
 *
 * <<licensetext>>
 */
public class IntersectionofTwoArraysII350 {

  public int[] intersect(int[] nums1, int[] nums2) {
    Map<Integer, Integer> map = new HashMap<>();
    List<Integer> intersect = new ArrayList();
    for (int num : nums1){
      if(map.containsKey(num)){
        map.put(num, map.get(num) +1);
      }else{
        map.put(num, 1);
      }
    }


    for (int num: nums2){
      if (map.containsKey(num) && map.get(num) >0){
        intersect.add(num);
        map.put(num, map.get(num) -1);
      } else{
        map.put(num, -1);
      }

    }
    int[] arr = new int[intersect.size()];
    for (int i =0; i < intersect.size(); i++)
      arr[i] = intersect.get(i);

    return arr;
  }
}

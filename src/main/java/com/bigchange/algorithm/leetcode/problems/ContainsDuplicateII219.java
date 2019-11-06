package com.bigchange.algorithm.leetcode.problems;

import java.util.HashMap;

/**
 * User: JerryYou
 *
 * Date: 2019-11-06
 *
 * Copyright (c) 2018 devops
 *
 * <<licensetext>>
 */
public class ContainsDuplicateII219 {


  public boolean containsNearbyDuplicate(int[] nums, int k) {
    HashMap<Integer, Integer> map = new HashMap<Integer,Integer>();

    for(int i=0;i < nums.length;i++){

      if(map.containsKey(nums[i])){

        if(i-map.get(nums[i]) <= k)  {
          return true;
        }
      }
      map.put(nums[i],i);
    }
    return false;

  }

}

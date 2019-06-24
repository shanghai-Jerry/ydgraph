package com.bigchange.algorithm.leetcode.problems;

import java.util.ArrayList;
import java.util.List;

/**
 * User: JerryYou
 *
 * Date: 2019-06-24
 *
 * Copyright (c) 2018 devops
 *
 * <<licensetext>>
 */
public class Permutations46 {

  public List<List<Integer>> permute(int[] nums) {
    List<List<Integer>> res = new ArrayList<>();
    int [] visit = new int[nums.length];
    backtracking(res, new ArrayList<>(), nums, visit);
    return res;


  }

  private void backtracking(List<List<Integer>> list, List<Integer> answer, int[] candidates,
                            int[] visit){
    if(answer.size() == candidates.length){
      list.add(new ArrayList<>(answer));
    } else{
      for(int i= 0; i < candidates.length; i++){
        if (visit[i] == 0) {
          answer.add(candidates[i]);
          visit[i] = 1;
          backtracking(list, answer, candidates, visit);
          answer.remove(answer.size()-1);
          visit[i] = 0;
        }
      }
    }
  }
}

package com.bigchange.algorithm.leetcode.problems;

import java.util.ArrayList;
import java.util.List;

/**
 * User: JerryYou
 *
 * Date: 2019-06-20
 *
 * Copyright (c) 2018 devops
 *
 * <<licensetext>>
 */
public class Combinations77 {

  public List<List<Integer>> combine(int n, int k) {
    List<List<Integer>> res = new ArrayList<>();
    int[] candidates = new int[n];
    for(int j=0; j < candidates.length; j++){
      candidates[j] = j+1;
    }
    backtracking(res, new ArrayList<>(), k, candidates, 0);
    return res;


  }

  private void backtracking(List<List<Integer>> list, List<Integer> answer, int k, int[] candidates,
                          int start){
    if(answer.size() == k){
      list.add(new ArrayList<>(answer));
    } else{
      for(int i= start; i < candidates.length; i++){
        answer.add(candidates[i]);
        backtracking(list, answer, k, candidates, i+1);
        answer.remove(answer.size()-1);
      }
    }
  }
}

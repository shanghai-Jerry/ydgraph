package com.bigchange.algorithm.leetcode.problems;

/**
 * User: JerryYou
 *
 * Date: 2019-05-08
 *
 * Copyright (c) 2018 devops
 *
 * <<licensetext>>
 *
 *   Given 1->2->3->4, you should return the list as 2->1->4->3.
 */
public class SwapNodesPair24 {

  class ListNode {
    int val;
    ListNode next;
    ListNode(int x) { val = x; }
  }

  public ListNode swapPairs(ListNode head) {
    if(head == null || head.next == null) {
      return head;
    }

    ListNode second = head.next;
    ListNode third = head.next.next;

    second.next = head;
    head.next = swapPairs(third);

    return second;
  }
}

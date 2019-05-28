package com.bigchange.algorithm.leetcode.problems;

/**
 * User: JerryYou
 *
 * Date: 2019-05-08
 *
 * Copyright (c) 2018 devops
 *
 * Input: 1->2->3->4->5->NULL
 *
 * Output: 5->4->3->2->1->NULL
 *
 * <<licensetext>>
 */
public class ReverseLinkedList206 {

  class ListNode {
    int val;
    ListNode next;
    ListNode(int x) { val = x; }
  }

  public ListNode reverseList(ListNode head) {
    if (head == null) {
      return  head;
    }
    ListNode second = head.next;
    head.next = null;
    while(second != null) {
      ListNode tmp = second.next;
      second.next = head;
      head = second;
      second = tmp;
    }
    return head;

  }

  public ListNode reverseListRecursive(ListNode head) {
    if (head == null || head.next == null) return head;
    ListNode p = reverseList(head.next);
    head.next.next = head;
    head.next = null;
    return p;
  }

}

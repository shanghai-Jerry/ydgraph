package com.bigchange.algorithm.leetcode.problems;

/**
 * User: JerryYou
 *
 * Date: 2019-05-08
 *
 * Copyright (c) 2018 devops
 *
 * <<licensetext>>
 */
public class MergeTwoSortedLists21 {

  class ListNode {
    int val;
    ListNode next;
    ListNode(int x) { val = x; }
  }
  public ListNode mergeTwoLists(ListNode l1, ListNode l2) {
    ListNode head = new ListNode(0);
    ListNode p = head;

    while(l1!=null||l2!=null){
      if(l1!=null&&l2!=null){
        if(l1.val < l2.val){
          p.next = l1;
          l1=l1.next;
        }else{
          p.next=l2;
          l2=l2.next;
        }
        p = p.next;
      }else if(l1==null){
        p.next = l2;
        break;
      }else if(l2==null){
        p.next = l1;
        break;
      }
    }

    return head.next;
  }

  public ListNode custom(ListNode l1, ListNode l2) {

    ListNode head = new ListNode(0);
    ListNode headMv = head;
    while(l1 != null && l2 != null) {
      if (l1.val > l2.val) {
        headMv.next = l2;
        l2 = l2.next;
      } else {
        headMv.next = l1;
        l1 = l1.next;
      }
      headMv = headMv.next;
    }

    if (l1 == null) {
      headMv.next = l2;
    } else {
      headMv.next = l1;
    }

    return  head.next;
  }
}

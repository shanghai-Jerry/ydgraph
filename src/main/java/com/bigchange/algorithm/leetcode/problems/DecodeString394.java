package com.bigchange.algorithm.leetcode.problems;

import java.util.Stack;

/**
 * User: JerryYou
 *
 * Date: 2019-06-06
 *
 * Copyright (c) 2018 devops
 *
 * <<licensetext>>
 */
public class DecodeString394 {

  class StackItem {
    int index;
    char item;
    String str;

    public StackItem(int index, char item, String str) {
      this.index = index;
      this.item = item;
      this.str = str;
    }
  }
  // 1ms
  // 每次先解码最里面的[], 解码后的str填入stack，然后依次继续解码，知道所有的[]都解码完毕
  public String decodeString(String s) {
    String ret = "";
    Stack<StackItem> stack = new Stack<>();
    char[] chs = s.toCharArray();
    for (int i = 0; i < chs.length;i++) {
      if (chs[i] == ']') {
        boolean isEnd = false;
        String repeat = "";
        String number = "";
        while (!stack.empty()) {
          StackItem top = stack.pop();
          // System.out.println("index:" + top.index + ", item:" + top.item);
          if ('[' == top.item) {
            if (isEnd) {
              String finStr = "";
              for (int k = 0; k < Integer.parseInt(number);k++) {
                finStr = finStr + repeat;
              }
              stack.push(top);
              String continueStr = finStr;
              // System.out.println("num:"+ number + ",continue:" + continueStr);
              stack.push(new StackItem(-1, '!', continueStr));
              break;
            }
            isEnd = true;
          } else if (top.item >= '0' && top.item <= '9') {
             number = top.item + number;
          } else  {
              if (isEnd) {
                  String finStr = "";
                  for (int k = 0; k < Integer.parseInt(number);k++) {
                    finStr = finStr + repeat;
                  }
                String continueStr = "";
                  if (top.index != -1) {
                    continueStr = top.item + finStr;
                  } else {
                    continueStr = top.str + finStr;
                  }
                // System.out.println("num:"+ number + ",continue2:" + continueStr);
                stack.push(new StackItem(-1, '!', continueStr));
                break;
              } else {
                if (top.index != -1) {
                  repeat =  top.item + repeat;
                } else {
                  repeat = top.str + repeat;
                }
              }
          }
        } // while
        // final check
        if (stack.isEmpty() && !"".equals(number)) {
          String finStr = "";
          for (int k = 0; k < Integer.parseInt(number);k++) {
            finStr += repeat;
          }
          // System.out.println("number:" + number +",finStr2:" + finStr);
          stack.push(new StackItem(-1, '!', finStr));
        }
      } else {
        stack.push(new StackItem(i, chs[i], ""));
      }
    }
    // stack, 保存了解码后的字符串，依次读取即可
    while (!stack.isEmpty()) {
      StackItem top = stack.pop();
      if (top.index != -1) {
        ret = top.item + ret;
      } else {
        ret = top.str + ret;
      }
    }
    return ret;
  }
}

package com.bigchange.algorithm.trietree;

import io.vertx.core.logging.Logger;

/**
 * User: JerryYou
 *
 * Date: 2018-07-18
 *
 * Copyright (c) 2018 devops
 *
 * 字典树: 又称单词查找树，Trie树，前缀树，是一种树形结构
 *
 * 性质 :
 * 根节点不包含字符，除根节点外每一个节点都只包含一个字符；
 * 从根节点到某一节点，路径上经过的字符连接起来，为该节点对应的字符串；
 * 每个节点的所有子节点包含的字符都不相同
 *
 * 实现:
 * 采用数组的方式创建字典树, 这棵树的每个结点的所有儿子很显然地按照其字母大小排序
 *
 * 后缀树：
 * 把一个字符串所有后缀压缩并保存的字典树，所以我们把字符串的所有后缀还是按照字典树的规则建立。
 * 注意还是和字典树一样，根节点必须为空
 * 注意: 可以进行压缩，节省空间的处理
 *
 * 延伸：
 * 广义后缀树：后缀树可以存储一个或多个字符串，当存储的字符串数量大于等于2时就叫做广义后缀树。末尾节点通过新增一个字符区别不同字符串
 * 最长回文串: 定义，正反相同；可以把要求的最长回文串的字符串s1和它的反向（逆）字符串s2建立一棵广义后缀树，求共同前缀即可
 *
 * <<licensetext>>
 */
public class Trie {

  private Logger logger = io.vertx.core.logging.LoggerFactory.getLogger(Trie.class);

  private int SIZE = 127;
  private String preTravers;

  public String getPreTravers() {
    return preTravers;
  }

  public void setPreTravers(String preTravers) {
    this.preTravers = preTravers;
  }

  private TrieNode root;
  public Trie() {
    root = new TrieNode();
    root.setSIZE(SIZE);
  }

  // 建立字典树
  public void insert(String str) {
    if (str == null || str.length() == 0) {
      return;
    }
    TrieNode node  = root;
    char[] letters = str.toCharArray();
    for (int i = 0, len = letters.length; i < len; i++) {
      int pos = letters[i] - 'a';
      TrieNode child = node.getChild()[pos];
      if ( child == null) {
        child = new TrieNode();
        child.setVal(letters[i]);
        node.getChild()[pos] = child;
      } else {
        int num = child.getNum();
        child.setNum(num + 1);
      }
      node = node.getChild()[pos];
    }
  }

  // 前缀单词的数量
  public int  countPrefix(String prefix) {
    if (prefix == null || prefix.length() == 0) {
      return  -1;
    }
    TrieNode node = root;
    char[] letters = prefix.toCharArray();
    for (int i = 0, len = letters.length; i < len; i++) {
      int pos = letters[i] - 'a';
      TrieNode child = node.getChild()[pos];
      if (child == null) {
        return 0;
      } else {
        node = child;
      }
    }
    return node.getNum();
  }

  // 遍历经过此节点的单词. 该节点的对应的词就为：prefix
  public void preTraverse(TrieNode node, String prefix) {
    if(node.isEnd()) {
      logger.info("string => " + prefix);
      return;
    } else {
      for (TrieNode child : node.getChild()) {
        if (child != null) {
          preTraverse(child, prefix + child.getVal());
        }
      }
    }
  }

  // 打印指定前缀的单词
  public String hasPrefix(String prefix) {
    if (prefix == null || prefix.length() == 0) {
      return null;
    }
    TrieNode node = root;
    char[] letters = prefix.toCharArray();
    for (int i = 0, len = letters.length; i < len; i++) {
      int pos = letters[i] - 'a';
      TrieNode child = node.getChild()[pos];
      if (child == null) {
        return  null;
      } else {
        node = child;
      }
    }
    preTraverse(node, prefix);
    return null;
  }

  // 在字典树中查找一个完全匹配的单词
  public boolean has(String str) {
    if (str == null || str.length() == 0) {
      return  false;
    }
    TrieNode node = root;
    char [] letters = str.toCharArray();
    for (int i = 0, len = letters.length; i < len; i++) {
      int pos = letters[i] - 'a';
      TrieNode child = node.getChild()[pos];
      if (child != null) {
        node = child;
      } else {
        return false;
      }
    }
    return node.isEnd();
  }
  // 前序遍历字典树
  public void preTraverse(TrieNode node) {
    if (node == null) {
      return;
    }
    if (this.preTravers == null || this.preTravers.length() == 0) {
      this.preTravers = String.valueOf(node.getVal());
    } else {
      this.preTravers = this.preTravers + node.getVal();
    }

    for (TrieNode child : node.getChild()) {
      preTraverse(child);
    }
  }

  public TrieNode getRoot() {
    return root;
  }

  public void setRoot(TrieNode root) {
    this.root = root;
  }

  public static void main(String[] args) {
    Logger logger = io.vertx.core.logging.LoggerFactory.getLogger(Trie.class);
    Trie trie = new Trie();
    String[] strs = {"canana","cand","cee","fbsolute","acm"};
    String[] prefix = {"ba","b","band","abc"};
    for (String s : strs) {
      trie.insert(s);
    }
    trie.preTraverse(trie.getRoot());
    logger.info("preTraverse => " + trie.getPreTravers());
    logger.info("has => " + trie.hasPrefix("ee"));
    for (String pre : prefix) {
      logger.info("count prefix => " + pre + " -> " + trie.countPrefix(pre));
    }
  }
}

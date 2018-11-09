package com.bigchange.algorithm;

import com.bigchange.algorithm.hashInt.HashIntMap;

import java.util.HashMap;
import java.util.concurrent.atomic.AtomicLong;

import io.vertx.core.logging.Logger;

/**
 * User: JerryYou
 *
 * Date: 2018-11-09
 *
 * 将字符串转成整型的算法，节约存储空间
 *
 * Copyright (c) 2018 devops
 *
 * <<licensetext>>
 */
public class CharactersDepress {

  Logger logger = io.vertx.core.logging.LoggerFactory.getLogger(CharactersDepress.class);

  AtomicLong atomicLong = new AtomicLong();

  public AtomicLong getAtomicLong() {
    return atomicLong;
  }

  public void setAtomicLong(AtomicLong atomicLong) {
    this.atomicLong = atomicLong;
  }

  HashMap<HashIntMap, Long> map = new HashMap();

  public long convertHex(String hexString) {
    long value = Long.parseLong(hexString, 16);
    return value;
  }

  public String convertLong(long value) {

    return Long.toHexString(value);
  }

  public String pandingZero(String hex, int index, int max) {
    StringBuilder stringBuilder = new StringBuilder();
    for(int i = 0; i<max;i++) {
      if (index == i) {
        stringBuilder.append(hex);
      } else {
        if (i == 0) {
          stringBuilder.append("7f");
        } else {
          stringBuilder.append("ff");
        }
      }
    }
    return  stringBuilder.toString();
  }

  public long convertBound(String str) {
    int length = str.length();
    long bound = Long.MAX_VALUE;
    int index = 0;
    for(int i=0; i<length;i=i+2) {
      String pads = pandingZero(str.substring(i,i+2), index, length/2);
      index++;
      long v = convertHex(pads);
      bound = bound & v;
    }
    return bound;
  }

  public HashIntMap convertHashInt(String hash) {
    int legth = hash.length();
    logger.info("length:"+ legth);
    String sub1 = hash.substring(0, legth/2);
    String sub2 = hash.substring(legth/2);
    logger.info("sub1:" + sub1 + ", sub2:" + sub2);
    long bound1 = convertBound(sub1);
    long bound2 = convertBound(sub2);
    return  new HashIntMap(bound1,bound2);
  }

  public static void main(String[] args) {
    Logger logger = io.vertx.core.logging.LoggerFactory.getLogger(CharactersDepress.class);
    String hash = "10003e07447ebc2b9d70f0a7e0a1be93";
    CharactersDepress charactersDepress = new CharactersDepress();
    charactersDepress.convertHashInt(hash);
    // charactersDepress.convertLong(Long.MAX_VALUE)
    HashIntMap hashIntMap = charactersDepress.convertHashInt(hash);
    logger.info("bound:"+ hashIntMap.getLowerBound() + " -> " + hashIntMap.getUpperBound());


  }

}

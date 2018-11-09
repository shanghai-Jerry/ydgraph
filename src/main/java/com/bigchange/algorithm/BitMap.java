package com.bigchange.algorithm;

/**
 * User: JerryYou
 *
 * Date: 2018-11-09
 *
 * Copyright (c) 2018 devops
 *
 * <<licensetext>>
 */
public class BitMap {
  private int[] mBits;

  public int[] getmBits() {
    return mBits;
  }
  private int mSize;

  public BitMap(int size) {
    // TODO Auto-generated constructor stub
    mSize = size;
    initBits();
  }

  private void initBits() {
    int count =(int) Math.ceil(mSize/32f)+1;
    mBits = new int[count];
  }
}

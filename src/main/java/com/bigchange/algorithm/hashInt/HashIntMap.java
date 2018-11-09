package com.bigchange.algorithm.hashInt;

import java.util.Objects;

/**
 * User: JerryYou
 *
 * Date: 2018-11-09
 *
 * Copyright (c) 2018 devops
 *
 * <<licensetext>>
 */
public class HashIntMap {
  long upperBound;
  long lowerBound;

  public HashIntMap(long upperBound, long lowerBound) {
    this.upperBound = upperBound;
    this.lowerBound = lowerBound;
  }

  public long getUpperBound() {
    return upperBound;
  }

  public long getLowerBound() {
    return lowerBound;
  }



  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    HashIntMap that = (HashIntMap) o;
    return upperBound == that.upperBound && lowerBound == that.lowerBound;
  }

  @Override
  public int hashCode() {

    return Objects.hash(upperBound, lowerBound);
  }
}




package com.higgs.cassandra;

/**
 * User: JerryYou
 *
 * Date: 2019-09-06
 *
 * Copyright (c) 2018 devops
 *
 * <<licensetext>>
 */
public class QueryFilter {
  String channel;
  String name;
  String receiveLessThen;
  String receiveGreatThen;

  int batchSize = 1000;
  int uid;

  public QueryFilter() {}

  public QueryFilter(String channel, String name, String receiveLessThen, String
      receiveGreatThen, int uid) {
    this.channel = channel;
    this.name = name;
    this.receiveLessThen = receiveLessThen;
    this.receiveGreatThen = receiveGreatThen;
    this.uid = uid;
  }

  public int getBatchSize() {
    return batchSize;
  }

  public void setBatchSize(int batchSize) {
    this.batchSize = batchSize;
  }

  public String getChannel() {
    return channel;
  }

  public QueryFilter setChannel(String channel) {
    this.channel = channel;
    return this;
  }

  public String getName() {
    return name;
  }

  public QueryFilter setName(String name) {
    this.name = name;
    return this;
  }

  public String getReceiveLessThen() {
    return receiveLessThen;
  }

  public QueryFilter setReceiveLessThen(String receiveLessThen) {
    this.receiveLessThen = receiveLessThen;
    return this;
  }

  public String getReceiveGreatThen() {
    return receiveGreatThen;
  }

  public QueryFilter setReceiveGreatThen(String receiveGreatThen) {
    this.receiveGreatThen = receiveGreatThen;
    return this;
  }

  public int getUid() {
    return uid;
  }

  public QueryFilter setUid(int uid) {
    this.uid = uid;
    return this;
  }

  public boolean isVailad() {
    if (channel.isEmpty() || name.isEmpty() || receiveGreatThen.isEmpty() || receiveLessThen
        .isEmpty()) {
      return false;
    }
    return true;
  }

  @Override
  public String toString() {
    return "queryFilter channel:" + channel + ",name:"+ name+ ",receiveGreatThen:"+
        receiveGreatThen+ ",receiveLessThen:"+ receiveLessThen+ ",uid:"+ uid;
  }
}

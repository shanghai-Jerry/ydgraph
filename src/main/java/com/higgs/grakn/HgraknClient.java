package com.higgs.grakn;

import grakn.client.GraknClient;

/**
 * User: JerryYou
 *
 * Date: 2019-08-21
 *
 * Copyright (c) 2018 devops
 *
 * <<licensetext>>
 */
public class HgraknClient {

  GraknClient client;

  public HgraknClient(String address) {
    client = new GraknClient(address);
  }

  public GraknClient.Session NewSession(String keySpace) {
    return  client.session(keySpace);
  }

  public void close() {
    if (client != null) {
      client.close();
    }
  }

  public GraknClient getClient() {
    return client;
  }
}

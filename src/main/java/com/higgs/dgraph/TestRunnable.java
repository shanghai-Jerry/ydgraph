package com.higgs.dgraph;

import java.util.List;

/**
 * Created by lolaliva on 2018/5/15.
 */
public class TestRunnable implements Runnable {
  List<String> rdfs;
  DClient dClient;
  TestRunnable(DClient dClient, List<String> rdfs) {
    this.rdfs = rdfs;
    this.dClient = dClient;
  }
  @Override
  public void run() {
     dClient.multiplyEdgesMutation(rdfs, false);
  }
}

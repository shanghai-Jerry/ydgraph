package com.higgs.dgraph;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.higgs.dgraph.node.Label;
import com.higgs.dgraph.node.NodeUtil;
import com.higgs.utils.util;

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

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
  Label label;
  DClient dClient;
  TestRunnable(DClient dClient,Label label) {
    this.label = label;
    this.dClient = dClient;
  }
  @Override
  public void run() {

    Map<String, List<String>> uid = NodeUtil.insertEntity(dClient, Arrays.asList(label));
    util.println("finished:", uid.size());
    // FileUtils.saveFile("src/main/resources/test_label_uid_map.txt", uid);

  }
}

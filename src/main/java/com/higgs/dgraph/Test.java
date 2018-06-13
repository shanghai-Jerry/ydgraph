package com.higgs.dgraph;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import com.higgs.client.EntityIdClient;
import com.higgs.dgraph.del.NodeDel;
import com.higgs.dgraph.node.Candidate;
import com.higgs.dgraph.node.Company;
import com.higgs.dgraph.node.Industry;
import com.higgs.dgraph.node.Label;
import com.higgs.dgraph.node.NodeUtil;
import com.higgs.dgraph.node.School;
import com.higgs.dgraph.put.EdgeFacetPut;
import com.higgs.utils.FileUtils;

/**
 * Created by lolaliva on 2018/5/10.
 */
public class Test {

  DClient dClient = new DClient(Config.TEST_VM_HOSTNAME);

  EntityIdClient client = new EntityIdClient("172.20.0.14", 26544);

  Demo demo = new Demo(dClient);

  private void test_list_type() {
    Industry industry = new Industry();
    industry.setUnique_ids(Arrays.asList("1", "2"));
    industry.setUid("0x3d");
    NodeUtil.putEntity(dClient, Arrays.asList(industry));
  }

  private void test_seven() {
    NodeDel nodeDel = new NodeDel();
    nodeDel.setUniqueId("llb00000000000000000000000100192");
    NodeUtil.deleteEntity(dClient, client, Arrays.asList(nodeDel),"候选人");
  }
  public static void main(String[] arg) {
    Test test = new Test();
    test.test_list_type();
  }


}

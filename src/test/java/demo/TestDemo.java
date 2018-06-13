package demo;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import com.higgs.dgraph.Config;
import com.higgs.dgraph.DClient;
import com.higgs.dgraph.Demo;
import com.higgs.dgraph.node.NodeUtil;
import com.higgs.dgraph.node.Person;
import com.higgs.dgraph.node.School;

public class TestDemo {

  DClient dClient = new DClient(Config.TEST_VM_HOSTNAME);

  Demo demo = new Demo(dClient);
  public static void main(String[] args) throws Exception {
    TestDemo testDemo = new TestDemo();
  }

}

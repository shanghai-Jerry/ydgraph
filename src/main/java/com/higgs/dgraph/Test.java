package com.higgs.dgraph;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import com.higgs.client.EntityIdClient;
import com.higgs.client.dgrpah.DgraphClient;
import com.higgs.dgraph.del.NodeDel;
import com.higgs.dgraph.node.Candidate;
import com.higgs.dgraph.node.Company;
import com.higgs.dgraph.node.DeptName;
import com.higgs.dgraph.node.Industry;
import com.higgs.dgraph.node.Label;
import com.higgs.dgraph.node.NodeUtil;
import com.higgs.dgraph.node.School;
import com.higgs.dgraph.put.EdgeFacetPut;
import com.higgs.utils.FileUtils;
import com.higgs.utils.TimeUtil;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;

import io.dgraph.DgraphProto;

/**
 * Created by lolaliva on 2018/5/10.
 */
public class Test {

  DClient dClient = new DClient(Config.TEST_HOSTNAME);

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

  protected void test_concurrently() {
    Label label = new Label();
    label.setUnique_id("label_test");
    label.setName("");
  }

  private void test_conut_byuid () {
    demo.dropSchema();
    DeptName deptName = new DeptName();
    String dept_name1 = "DEPTONE";
    deptName.setName(dept_name1);
    deptName.setUnique_id(dept_name1);
    DeptName deptName1 = new DeptName();
    String dept_name2 = "DEPTTWO";
    deptName1.setName(dept_name2);
    deptName1.setUnique_id(dept_name2);
    // company
    Company company = new Company();
    String companyName = "百度";
    company.setName(companyName);
    company.setUnique_id(companyName);
    // candidate
    Candidate candidate = new Candidate();
    String caName = "youchaojiang";
    candidate.setName(caName);
    candidate.setUnique_id(caName);
    Candidate candidate1 = new Candidate();
    String cName2 = "xiaoyy";
    candidate1.setName(cName2);
    candidate1.setUnique_id(cName2);
    Candidate candidate2 = new Candidate();
    String cName3 = "test1";
    candidate2.setName(cName3);
    candidate2.setUnique_id(cName3);

    List<DeptName> deptNameList = Arrays.asList(deptName, deptName1);

    Map<String, List<String>> duid = NodeUtil.insertEntity(dClient, deptNameList);
    FileUtils.saveFile("src/main/resources/test_dir/dpet_uid.txt", duid);
    NodeUtil.putEntityUidWithNames(deptNameList, duid);
    // // set predicate
    company.setCompany_dept(deptNameList);
    candidate.setCandidate_company_dept(Arrays.asList(deptNameList.get(0)));
    candidate1.setCandidate_company_dept(Arrays.asList(deptNameList.get(1)));
    candidate2.setCandidate_company_dept(Arrays.asList(deptNameList.get(1)));
    Map<String, List<String>> cuid = NodeUtil.insertEntity(dClient, Arrays.asList(company));
    FileUtils.saveFile("src/main/resources/test_dir/company_uid.txt", cuid);
    Map<String, List<String>> canuid = NodeUtil.insertEntity(dClient, Arrays.asList(candidate,
        candidate1, candidate2));
    FileUtils.saveFile("src/main/resources/test_dir/candidate_uid.txt", canuid);

  }

  private void test_unkonw_format() {
    String uniqueId = "??平县?\u0000?\u0000?\u0000??\u0000??裰行?\u0000?增庄分销?\u0000";
    DgraphProto.NQuad.Builder builder = DgraphProto.NQuad.newBuilder().setSubject(String
        .format("_:%s", uniqueId)).setPredicate("name")
        .setObjectValue(DgraphProto.Value.newBuilder().setStrVal(uniqueId).build())
        ;
    DgraphProto.Mutation mu = DgraphProto.Mutation.newBuilder().addSet(builder.build()).build();
    DgraphProto.Assigned ag = null;
    DgraphClient.Transaction txn = this.dClient.getDgraphClient().newTransaction();
    try {
      ag = txn.mutate(mu);
      txn.commit();
    }  catch (Exception e) {
    } finally {
      txn.discard();
    }
    Map<String, String> map = ag.getUidsMap();
    if (map.containsKey(uniqueId)) {
      System.out.println(uniqueId + " has uid:" + map.get(uniqueId));
    }
    FileUtils.saveFiles("src/main/resources/test_dir/unknow_format_uid.txt", ag.getUidsMap());

  }

  private void test_import() {
    List<String> edges = new ArrayList<>();
    edges.add("<0x47c2> <name> \"哈尔滨市艺乐糖酒有限公司\"^^<xs:string> .");
    dClient.multiplyEdgesMutation(edges, false);
  }

  private void test_delete_edges() {
    String uid = "0x3";
    DgraphProto.NQuad nQuad = DgraphProto.NQuad.newBuilder().setSubject(String
        .format(uid)).setPredicate("name")
        .setObjectValue(DgraphProto.Value.newBuilder().setStrVal("_STAR_ALL").build()).build();

    DgraphProto.Mutation mutation = DgraphProto.Mutation.newBuilder().addDel(nQuad).build();

    DgraphClient.Transaction txn = this.dClient.getDgraphClient().newTransaction();
    try {
      txn.mutate(mutation);
      txn.commit();
    }  catch (Exception e) {
    } finally {
      txn.discard();
    }
  }


  public static void main(String[] arg) {
    Test test = new Test();
    // test.test_list_type();
    // test.test_conut_byuid();
    // test.test_unkonw_format();
    // test.test_import();
    // String time = TimeUtil.consumeTime(30000 * 1000);
    test.test_delete_edges();
  }


}

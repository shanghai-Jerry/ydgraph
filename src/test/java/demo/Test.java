package demo;

import com.google.protobuf.ByteString;

import com.higgs.client.EntityIdClient;
import com.higgs.client.dgrpah.DgraphClient;
import com.higgs.dgraph.Config;
import com.higgs.dgraph.DClient;
import com.higgs.dgraph.Demo;
import com.higgs.dgraph.del.NodeDel;
import com.higgs.dgraph.node.Candidate;
import com.higgs.dgraph.node.Company;
import com.higgs.dgraph.node.DeptName;
import com.higgs.dgraph.node.GenderNode;
import com.higgs.dgraph.node.Industry;
import com.higgs.dgraph.node.NodeUtil;
import com.higgs.dgraph.node.NquadUtil;
import com.higgs.utils.FileUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import io.dgraph.DgraphProto;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

/**
 * Created by lolaliva on 2018/5/10.
 */
public class Test {

  Logger logger = LoggerFactory.getLogger(Test.class);

  DClient dClient = new DClient(Config.TEST_HOSTNAME);

  EntityIdClient client = new EntityIdClient("172.20.0.14", 26544);

  Demo demo = new Demo(dClient);

  public Test(DClient dClient) {
    this.dClient = dClient;
  }

  public Test() {

  }

  private void putWithJsonFormat() {
    Industry industry = new Industry();
    industry.setUnique_ids(Arrays.asList("1", "2"));
    industry.setUid("0x3d");
    NodeUtil.putEntity(dClient, Arrays.asList(industry));
  }

  private void deleteNodeEdgeInDgraph() {
    NodeDel nodeDel = new NodeDel();
    nodeDel.setUniqueId("llb00000000000000000000000100192");
    NodeUtil.deleteEntity(dClient, client, Arrays.asList(nodeDel), "候选人");
  }

  private void putWithRDF() {
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

  private void putWithNquad() {
    String uniqueId = "??平县?\u0000?\u0000?\u0000??\u0000??裰行?\u0000?增庄分销?\u0000";
    DgraphProto.NQuad.Builder builder = DgraphProto.NQuad.newBuilder().setSubject(String.format
        ("_:%s", uniqueId)).setPredicate("name").setObjectValue(DgraphProto.Value.newBuilder()
        .setStrVal(uniqueId).build());
    DgraphProto.Mutation mu = DgraphProto.Mutation.newBuilder().addSet(builder.build()).build();
    DgraphProto.Assigned ag = null;
    DgraphClient.Transaction txn = this.dClient.getDgraphClient().newTransaction();
    try {
      ag = txn.mutate(mu);
      txn.commit();
    } catch (Exception e) {
    } finally {
      txn.discard();
    }
    Map<String, String> map = ag.getUidsMap();
    if (map.containsKey(uniqueId)) {
      System.out.println(uniqueId + " has uid:" + map.get(uniqueId));
    }
    FileUtils.saveFiles("src/main/resources/test_dir/unknow_format_uid.txt", ag.getUidsMap());

  }

  private void putWithNquadWithFacets() {
    List<String> rdf = new ArrayList<>();
    rdf.add(" <0x7b454d> <candidate_dept> <0x4ea8c0> (on_job=true,salary=18000.0) .\n");
    rdf.add(" <0x7b454d> <candidate_dept> <0x63569c> (on_job=false,salary=26000.0) .\n ");
    // rdf.add("\n");
    DgraphProto.Assigned assigned = null;
    List<ByteString> newEdges = new ArrayList<>();
    for (String edge : rdf) {
      newEdges.add(ByteString.copyFromUtf8(edge));
    }
    DgraphProto.Mutation mu = DgraphProto.Mutation.newBuilder().setSetNquads(ByteString.copyFrom
        (newEdges)).build();
    DgraphClient.Transaction txn = this.dClient.getDgraphClient().newTransaction();
    try {
      assigned = txn.mutate(mu);
      txn.commit();
    } catch (Exception e) {
    } finally {
      txn.discard();
    }
    FileUtils.saveFiles("src/main/resources/test_dir/test_import_uid.txt", assigned.getUidsMap());
  }

  private void handleSubEntityUid() {
    Candidate candidate = new Candidate();
    candidate.setGender("其他");
    NodeUtil.dealingCandidatesSubNodes(Arrays.asList(candidate), client);
    GenderNode genderNode = candidate.getGender_node();
    logger.info("uid:" + genderNode.getUid());
  }

  private void prepareUid() {
    DeptName deptName = new DeptName();
    String dept_name1 = "DEPTONE";
    deptName.setName(dept_name1);
    deptName.setUnique_id(dept_name1);
    List<DeptName> deptNameList = new ArrayList<>();
    deptNameList.add(deptName);
    Map<String, String> assignedUidMap = NodeUtil.getAssignedUid(dClient, deptNameList);
    logger.info("uid:" + assignedUidMap.get(dept_name1));
    Map<String, List<String>> companyRet = NodeUtil.putEnitityAssignedUid(assignedUidMap,
        deptNameList);
    logger.info("ret size::" + companyRet.size() + "," + deptNameList.get(0).getUid());
    NodeUtil.putFacetAssignedUid(assignedUidMap, new ArrayList<>());
    List<String> entityNquads = NquadUtil.getEntityNquads(deptNameList, new ArrayList<>());
    FileUtils.saveFile("src/main/resources/test_dir/uid_nquad.txt", entityNquads, true);

  }

  public static void main(String[] arg) {
    DClient dClient = new DClient(Config.TEST_HOSTNAME);
    Logger logger = LoggerFactory.getLogger(Test.class);
    Test test = new Test(dClient);
    // String time = TimeUtil.consumeTime(30000 * 1000);
    // test.putWithJsonFormat();
    // test.putWithRDF();
    // test.putWithNquad();
    // test.putWithNquadWithFacets();
    // test.handleSubEntityUid();
    // test.prepareUid();
  }

}

package dgraph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import client.EntityIdClient;
import dgraph.node.Major;
import dgraph.node.NodeUtil;
import dgraph.put.Nodeput;
import io.dgraph.DgraphProto;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import utils.FileUtils;

public class MajorToDgraph {
  private static final Logger logger = LoggerFactory.getLogger(MajorToDgraph.class);
  private DClient dClient;
  private EntityIdClient entityIdClient;

  public MajorToDgraph() {
    dClient = new DClient(Config.TEST_HOSTNAME, Config.TEST_PORT);
    entityIdClient = new EntityIdClient(Config.EntityId_Host, Config.EntityIdService_PORT);
  }

  public void getMajor(List<String> dictLines, List<Major> majors) {
    for (String line : dictLines) {
      Major major = new Major();
      String[] lineSplits = line.split("\t");
      if (lineSplits.length != 2) {
        System.out.println("line:" + line + ",line length:" + lineSplits.length);
      }
      major.setName(lineSplits[1]);
      major.setCode(lineSplits[0]);
      major.setType("专业");
      if ("0".equals(major.getCode())) {
        continue;
      }
      majors.add(major);
    }
  }

  public void getList(List<Major> majors, List<Major> insertList, List<Major> updateList) {
    List<List<String>> reqs = new ArrayList<List<String>>();
    Map<String, String> uidMap = new HashMap<String, String>();
    String type = "";
    for (Major entityNode : majors) {
      if ("".equals(type)) {
        type = entityNode.getType();
      }
      List<String> names = new ArrayList<String>();
      names.add(entityNode.getName());
      reqs.add(names);
    }
    entityIdClient.checkEntityList(reqs, uidMap, type);
    for (Major entityNode : majors) {
      if (uidMap.containsKey(entityNode.getName())) {
        entityNode.setUid(uidMap.get(entityNode.getName()));
        updateList.add(entityNode);
      } else {
        insertList.add(entityNode);
      }
    }
  }

  public void insertEntity(List<Major> majorList, String type) {
    Map<String, String> uidMaps = new HashMap<String, String>();
    List<Nodeput> dputList = new ArrayList<Nodeput>();
    io.dgraph.DgraphClient.Transaction txn = dClient.getDgraphClient().newTransaction();
    int batch = 0;
    for (Major major : majorList) {
      List<String> pres = new ArrayList<String>();
      List<String> values = new ArrayList<String>();
      major.getStrAttrValueMap(pres, values);
      Nodeput dput = new Nodeput();
      dput.setUniqueId(major.getName());
      dput.setPredicates(pres);
      dput.setValues(values);
      dputList.add(dput);
      batch++;
      if (batch >= Config.batch) {
        DgraphProto.Assigned ag = dClient.entityWithStrAttrInitial(txn, dputList);
        uidMaps.putAll(ag.getUidsMap());
        txn.commit();
        txn.discard();
        txn = dClient.getDgraphClient().newTransaction();
        batch = 0;
        dputList.clear();
      }
    }
    if (batch > 0) {
      if (txn != null) {
        DgraphProto.Assigned ag = dClient.entityWithStrAttrInitial(txn, dputList);
        uidMaps.putAll(ag.getUidsMap());
        txn.commit();
        txn.discard();
      }
    }
    entityIdClient.putFeedEntity(uidMaps, type);
    System.out.println("get all uids :" + uidMaps.size());
    FileUtils.saveFile("/Users/devops/Documents/知识图谱/school/major_uid_map.txt", uidMaps);
  }

  public void updateEntity(List<Major> majorList) {
    // update
    List<Nodeput> dputList = new ArrayList<Nodeput>();
    io.dgraph.DgraphClient.Transaction txn = dClient.getDgraphClient().newTransaction();
    int batch = 0;
    for (Major major : majorList) {
      List<String> pres = new ArrayList<String>();
      List<String> values = new ArrayList<String>();
      major.getStrAttrValueMap(pres, values);
      Nodeput dput = new Nodeput();
      dput.setUid(major.getUid());
      dput.setUniqueId(major.getName());
      dput.setPredicates(pres);
      dput.setValues(values);
      dputList.add(dput);
      batch++;
      if (batch >= Config.batch) {
        dClient.entityAddStrAttr(txn, dputList);
        txn.commit();
        txn.discard();
        txn = dClient.getDgraphClient().newTransaction();
        batch = 0;
        dputList.clear();
      }
    }
    if (batch > 0) {
      if (txn != null) {
        dClient.entityAddStrAttr(txn, dputList);
        txn.commit();
        txn.discard();
      }
    }
  }

  public void init(String dictPath) {
    List<String> dictLines = new ArrayList<String>();
    List<Major> majors = new ArrayList<Major>();
    FileUtils.readFiles(dictPath, dictLines);
    getMajor(dictLines, majors);
    int batch = 0;
    long startTime = System.currentTimeMillis();
    List<Major> majorList = new ArrayList<Major>();
    List<Major> updateMajorList = new ArrayList<Major>();
    getList(majors, majorList, updateMajorList);
    System.out.println("get separate list: :" + majorList.size() + ", "
        + updateMajorList.size());
    System.out.println("get all majors :" + majors.size());
    NodeUtil.insertEntity(dClient, entityIdClient, majorList, "学校");
    // insertEntity(majorList, "专业");
    NodeUtil.updateEntity(dClient, entityIdClient, updateMajorList);
    // updateEntity(updateMajorList);
    long endStart = System.currentTimeMillis();
    System.out.println("spend time:" + (endStart - startTime) + " ms");
  }

  public void initWithJson(String dictPath) {
    List<String> dictLines = new ArrayList<String>();
    List<Major> majors = new ArrayList<Major>();
    List<String> majorString = new ArrayList<String>();
    List<String> keys = new ArrayList<String>();
    List<String> values = new ArrayList<String>();
    Map<String, String> uidMaps = new HashMap<String, String>();
    FileUtils.readFiles(dictPath, dictLines);
    getMajor(dictLines, majors);
    io.dgraph.DgraphClient.Transaction txn = dClient.getDgraphClient().newTransaction();
    long startTime = System.currentTimeMillis();
    System.out.println("get all majors :" + majors.size());
    for (Major major : majors) {
      keys.add(major.getName());
      majorString.add(major.toString());
    }
    List<DgraphProto.Assigned> assignedList = dClient.mutiplyMutation(txn, majorString);
    for (DgraphProto.Assigned ag : assignedList) {
      values.addAll(ag.getUidsMap().values());
    }
    long endStart = System.currentTimeMillis();
    System.out.println("get all uids :" + uidMaps.size());
    System.out.println("spend time:" + (endStart - startTime) + " ms");
    FileUtils.saveFile("/Users/devops/Documents/知识图谱/school/major_uid_map.txt", uidMaps);
  }
  public static  void main(String []args) {
    String dictPath = "/Users/devops/workspace/gitlab/idmg/resume_extractor/src/cc/major_dict.txt";
    MajorToDgraph majorToDgraph = new MajorToDgraph();
    majorToDgraph.init(dictPath);
    // majorToDgraph.initWithJson(dictPath);
  }
}

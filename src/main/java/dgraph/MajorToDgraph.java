package dgraph;

import java.util.*;

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
      major.setTestType(100);
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
    // NodeUtil.insertEntity(dClient, entityIdClient, majorList, "学校");
    // insertEntity(majorList, "专业");
    NodeUtil.updateEntityNew(dClient, entityIdClient, updateMajorList);
    // updateEntity(updateMajorList);
    long endStart = System.currentTimeMillis();
    System.out.println("spend time:" + (endStart - startTime) + " ms");
  }

  /**
   * this way is better and faster than NQuad, you'd better try this much more.
   * @param dictPath
   */
  public void initWithJson(String dictPath) {
    List<String> dictLines = new ArrayList<String>();
    List<Major> majors = new ArrayList<Major>();
    List<String> values = new ArrayList<String>();
    Map<String, String> uidMaps = new HashMap<String, String>();
    FileUtils.readFiles(dictPath, dictLines);
    getMajor(dictLines, majors);
    io.dgraph.DgraphClient.Transaction txn = dClient.getDgraphClient().newTransaction();
    long startTime = System.currentTimeMillis();
    System.out.println("get all majors :" + majors.size());
    DgraphProto.Assigned assigned = dClient.mutiplyMutationEntity(txn, majors);
    NodeUtil.uidFlattenMapping(assigned.getUidsMap(), majors, uidMaps);
    long endStart = System.currentTimeMillis();
    System.out.println("get all uids :" + uidMaps.size());
    System.out.println("spend time:" + (endStart - startTime) + " ms");
    FileUtils.saveFile("/Users/lolaliva/Documents/知识图谱/major/major_uid_map.txt",uidMaps);
  }
  public static  void main(String []args) {
    String dictPath = "/Users/lolaliva/workspace/home/gitlab/idmg/resume_extractor/src/cc/major_dict.txt";
    MajorToDgraph majorToDgraph = new MajorToDgraph();
    // majorToDgraph.init(dictPath);
    majorToDgraph.initWithJson(dictPath);
  }
}

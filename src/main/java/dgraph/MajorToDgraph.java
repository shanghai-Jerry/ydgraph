package dgraph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import client.EntityIdClient;
import dgraph.node.Major;
import dgraph.node.NodeUtil;
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
      List<String> names = new ArrayList<String>();
      Major major = new Major();
      String[] lineSplits = line.split("\t");
      if (lineSplits.length != 2) {
        System.out.println("line:" + line + ",line length:" + lineSplits.length);
      }
      String name = lineSplits[1];
      names.add(name);
      major.setNames(names);
      major.setName(name);
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

  public void init(String dictPath) {
    List<String> dictLines = new ArrayList<String>();
    List<Major> majors = new ArrayList<Major>();
    FileUtils.readFiles(dictPath, dictLines);
    getMajor(dictLines, majors);
    Map<String, String> uidMaps = new HashMap<String, String>();
    long startTime = System.currentTimeMillis();
    List<Major> majorList = new ArrayList<Major>();
    List<Major> updateMajorList = new ArrayList<Major>();
    getList(majors, majorList, updateMajorList);
    System.out.println("get separate list: :" + majorList.size() + ", "
        + updateMajorList.size());
    System.out.println("get all majors :" + majors.size());
    NodeUtil.insertEntity(dClient, majorList, uidMaps);
    entityIdClient.putFeedEntity(uidMaps,  "学校");
    NodeUtil.updateEntityNew(dClient, updateMajorList);
    long endStart = System.currentTimeMillis();
    System.out.println("spend time:" + (endStart - startTime) + " ms");
  }

  /**
   * this way is better and faster than NQuad, you'd better try this much more.
   * @param dictPath
   */
  public void initWithJson(String dictPath) {
    String type = "专业";
    List<String> dictLines = new ArrayList<String>();
    List<Major> majors = new ArrayList<Major>();
    FileUtils.readFiles(dictPath, dictLines);
    getMajor(dictLines, majors);
    System.out.println("get all majors :" + majors.size());
    NodeUtil.putEntity(dClient, entityIdClient, majors, type);
  }
  public static  void main(String []args) {
    String dictPath = "/Users/devops/workspace/gitlab/idmg/resume_extractor/src/cc/major_dict.txt";
    MajorToDgraph majorToDgraph = new MajorToDgraph();
    // majorToDgraph.init(dictPath);
    majorToDgraph.initWithJson(dictPath);
    majorToDgraph.dClient.getDgraphClient();
  }
}

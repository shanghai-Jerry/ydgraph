package dgraph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import client.EntityIdClient;
import dgraph.node.NodeUtil;
import dgraph.node.School;
import dgraph.put.Nodeput;
import io.dgraph.DgraphProto;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import utils.FileUtils;
import utils.util;

public class SchoolToDgraph {

  private static final Logger logger = LoggerFactory.getLogger(SchoolToDgraph.class);
  private DClient dClient;
  private EntityIdClient entityIdClient;

  public SchoolToDgraph() {
    dClient = new DClient(Config.TEST_HOSTNAME, Config.TEST_PORT);
    entityIdClient = new EntityIdClient(Config.EntityId_Host, Config.EntityIdService_PORT);
  }

  public void getSchool(List<String> dictLines, List<School> schools) {
    for (String line : dictLines) {
      School school = new School();
      String[] lineSplits = line.split("\t");
      if (lineSplits.length != 7) {
        System.out.println("line:" + line + ",line length:" + lineSplits.length);
      }
      school.setName(lineSplits[3]);
      school.setEngName(lineSplits[4]);
      school.setAlias(lineSplits[5]);
      school.setType("学校");
      schools.add(school);

    }
  }

  public void getList(List<School> schools, List<School> dputList, List<School> duputList) {
    List<List<String>> reqs = new ArrayList<List<String>>();
    Map<String, String> uidMap = new HashMap<String, String>();
    String type = "";
    for (School school : schools) {
      if ("".equals(type)) {
        type = school.getType();
      }
      List<String> names = new ArrayList<String>();
      names.add(school.getName());
      reqs.add(names);
    }
    entityIdClient.checkEntityList(reqs, uidMap, type);
    for (School school : schools) {
      if (uidMap.containsKey(school.getName())) {
        school.setUid(uidMap.get(school.getName()));
        duputList.add(school);
      } else {
        dputList.add(school);
      }
    }
  }

  public void insertEntity(List<School> schoolList,String type) {
    // insert new
    Map<String, String> uidMaps = new HashMap<String, String>();
    List<Nodeput> dputList = new ArrayList<Nodeput>();
    io.dgraph.DgraphClient.Transaction txn = dClient.getDgraphClient().newTransaction();
    int batch = 0;
    for (School school : schoolList) {
      List<String> pres = new ArrayList<String>();
      List<String> values = new ArrayList<String>();
      school.getStrAttrValueMap(pres, values);
      Nodeput dput = new Nodeput();
      dput.setUniqueId(school.getName());
      dput.setPredicates(pres);
      dput.setValues(values);
      dputList.add(dput);
      batch++;
      if (batch >= Config.batch) {
        DgraphProto.Assigned ag = dClient.entityWithStrAttrInitial(txn, dputList);
        util.mapCombiner(ag.getUidsMap(), uidMaps);
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
        util.mapCombiner(ag.getUidsMap(), uidMaps);
        txn.commit();
        txn.discard();
      }
    }
    entityIdClient.putFeedEntity(uidMaps, type);
    FileUtils.saveFile("/Users/devops/Documents/知识图谱/school/school_uid_map.txt", uidMaps);
    System.out.println("get all uids :" + uidMaps.size());
  }
  public void updateEntity(List<School> updateSchoolList) {
    int updateBatch = 0;
    io.dgraph.DgraphClient.Transaction txn = dClient.getDgraphClient().newTransaction();
    List<Nodeput> updatePutList = new ArrayList<Nodeput>();
    for (School school : updateSchoolList) {
      List<String> pres = new ArrayList<String>();
      List<String> values = new ArrayList<String>();
      school.getStrAttrValueMap(pres, values);
      Nodeput dput = new Nodeput();
      dput.setUid(school.getUid());
      dput.setUniqueId(school.getName());
      dput.setPredicates(pres);
      dput.setValues(values);
      updatePutList.add(dput);
      updateBatch++;
      if (updateBatch >= Config.batch) {
        dClient.entityAddStrAttr(txn, updatePutList);
        txn.commit();
        txn.discard();
        txn = dClient.getDgraphClient().newTransaction();
        updateBatch = 0;
        updatePutList.clear();
      }
    }
    if (updateBatch > 0) {
      if (txn != null) {
        dClient.entityAddStrAttr(txn, updatePutList);
        txn.commit();
        txn.discard();
      }
    }
  }

  /**
   * 初始化实体
   * @param filePath
   */
  public void init(String filePath) {
    List<String> dictLines = new ArrayList<String>();
    List<School> schools = new ArrayList<School>();
    FileUtils.readFiles(filePath, dictLines);
    getSchool(dictLines, schools);
    long startTime = System.currentTimeMillis();
    System.out.println("get all schools :" + schools.size());
    List<School> schoolList = new ArrayList<School>();
    List<School> updateSchoolList = new ArrayList<School>();
    getList(schools, schoolList, updateSchoolList);
    System.out.println("get separate list: :" + schoolList.size() +
        ", " + updateSchoolList.size());
    // insert
    // insertEntity(schoolList, "学校");
    NodeUtil.insertEntity(dClient,entityIdClient, schoolList, "学校");
    // update
    // updateEntity(updateSchoolList);
    NodeUtil.updateEntity(dClient, entityIdClient, updateSchoolList);

    long endStart = System.currentTimeMillis();
    System.out.println("spend time:" + (endStart - startTime) + " ms");
  }

  public static void main(String[] args) {
    SchoolToDgraph schoolToDgraph = new SchoolToDgraph();
    String dictPath = "/Users/devops/Documents/知识图谱/school/school_dump_dict.txt";
    schoolToDgraph.init(dictPath);
  }
}

package dgraph.node;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import client.EntityIdClient;
import dgraph.Config;
import dgraph.DClient;
import dgraph.put.Dput;
import io.dgraph.DgraphProto;
import utils.FileUtils;
import utils.util;

public class NodeUtil {


  public static  <T extends  EntityNode> void updateEntity(DClient dClient, EntityIdClient
      entityIdClient, List<T> list) {
    int updateBatch = 0;
    io.dgraph.DgraphClient.Transaction txn = dClient.getDgraphClient().newTransaction();
    List<Dput> updatePutList = new ArrayList<Dput>();
    for (T school : list) {
      List<String> pres = new ArrayList<String>();
      List<String> values = new ArrayList<String>();
      school.getStrAttrValueMap(pres, values);
      Dput dput = new Dput();
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


  public static <T extends EntityNode> void insertEntity(DClient dClient, EntityIdClient entityIdClient,
                                  List<T> list, String type) {
    // insert new
    Map<String, String> uidMaps = new HashMap<String, String>();
    List<Dput> dputList = new ArrayList<Dput>();
    io.dgraph.DgraphClient.Transaction txn = dClient.getDgraphClient().newTransaction();
    int batch = 0;
    for (T item : list) {
      List<String> pres = new ArrayList<String>();
      List<String> values = new ArrayList<String>();
      item.getStrAttrValueMap(pres, values);
      Dput dput = new Dput();
      dput.setUniqueId(item.getName());
      dput.setPredicates(pres);
      dput.setValues(values);
      dputList.add(dput);
      batch++;
      if (batch >= Config.batch) {
        DgraphProto.Assigned ag = dClient.entityWithStrAttrInitial(txn, dputList);
        util.mapCombiner(ag.getUidsMap(), uidMaps);
        txn.commit();
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

  /**
   * 泛型: 支持扩展
   * @param entityIdClient
   * @param entityNodes
   * @param insertList
   * @param updateList
   * @param <T>
   */
  public static <T extends EntityNode> void getList(EntityIdClient entityIdClient, List<T> entityNodes,
                                                    List<T> insertList, List<T> updateList) {
    List<List<String>> reqs = new ArrayList<List<String>>();
    Map<String, String> uidMap = new HashMap<String, String>();
    String type = "";
    for (T entityNode : entityNodes) {
      if ("".equals(type)) {
        type = entityNode.getType();
      }
      List<String> names = new ArrayList<String>();
      names.add(entityNode.getName());
      reqs.add(names);
    }
    entityIdClient.checkEntityList(reqs, uidMap, type);
    for (T entityNode : entityNodes) {
      if (uidMap.containsKey(entityNode.getName())) {
        entityNode.setUid(uidMap.get(entityNode.getName()));
        updateList.add(entityNode);
      } else {
        insertList.add(entityNode);
      }
    }
  }
}

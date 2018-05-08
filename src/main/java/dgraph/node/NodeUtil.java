package dgraph.node;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import client.EntityIdClient;
import client.dgrpah.DgraphClient;
import dgraph.Config;
import dgraph.DClient;
import dgraph.put.Nodeput;
import io.dgraph.DgraphProto;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

public class NodeUtil {

  private static final Logger logger = LoggerFactory.getLogger(NodeUtil.class);
  public static  <T extends  EntityNode> void updateEntity(DClient dClient, List<T> list) {
    int updateBatch = 0;
    DgraphClient.Transaction txn = dClient.getDgraphClient().newTransaction();
    List<Nodeput> updatePutList = new ArrayList<Nodeput>();
    for (T school : list) {
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
        dClient.entityAddStrAttr(updatePutList);
        updateBatch = 0;
        updatePutList.clear();
      }
    }
    if (updateBatch > 0) {
      dClient.entityAddStrAttr(updatePutList);
    }
  }

  public static  <T extends  EntityNode> void updateEntityNew(DClient dClient, List<T> list) {
    int updateBatch = 0;
    List<Nodeput> updatePutList = new ArrayList<Nodeput>();
    for (T school : list) {
      List<String> pres = new ArrayList<String>();
      List<Object> values = new ArrayList<Object>();
      school.getAttrValueMap(pres, values);
      Nodeput dput = new Nodeput();
      dput.setUid(school.getUid());
      dput.setUniqueId(school.getName());
      dput.setPredicates(pres);
      dput.setValueObjects(values);
      updatePutList.add(dput);
      updateBatch++;
      if (updateBatch >= Config.batch) {
        dClient.entityAddAttr(updatePutList);
        updateBatch = 0;
        updatePutList.clear();
      }
    }
    if (updateBatch > 0) {
      dClient.entityAddAttr(updatePutList);
    }
  }


  public static <T extends EntityNode> void insertEntity(DClient dClient,
                                  List<T> list, Map<String, String> uidMaps) {
    // insert new
    List<Nodeput> dputList = new ArrayList<Nodeput>();
    int batch = 0;
    for (T item : list) {
      List<String> pres = new ArrayList<String>();
      List<String> values = new ArrayList<String>();
      item.getStrAttrValueMap(pres, values);
      Nodeput dput = new Nodeput();
      dput.setUniqueId(item.getName());
      dput.setPredicates(pres);
      dput.setValues(values);
      dputList.add(dput);
      batch++;
      if (batch >= Config.batch) {
        DgraphProto.Assigned ag = dClient.entityWithStrAttrInitial(dputList);
        mapCombiner(ag.getUidsMap(), uidMaps);
        batch = 0;
        dputList.clear();
      }
    }
    if (batch > 0) {
      DgraphProto.Assigned ag = dClient.entityWithStrAttrInitial(dputList);
      mapCombiner(ag.getUidsMap(), uidMaps);
    }
    System.out.println("get all uids :" + uidMaps.size());
  }

  public static <T extends EntityNode> void putEntity(DClient dClient, EntityIdClient
      entityIdClient, List<T> list, String type) {
    Map<String, String> existuidMap = new HashMap<String, String>();
    Map<String, String> newUidMap = new HashMap<String, String>();
    List<List<String>> reqs = new ArrayList<List<String>>();
    NodeUtil.getCheckNames(list, reqs);
    entityIdClient.checkEntityList(reqs, existuidMap, type);
    NodeUtil.checkEntityUid(list, existuidMap);
    long startTime = System.currentTimeMillis();
    DgraphProto.Assigned assigned = dClient.mutiplyMutationEntity(list);
    logger.info("get ret uids :" + assigned.getUidsMap().size() + ", existUids:" +
        existuidMap.size());
    NodeUtil.uidFlattenMapping(assigned.getUidsMap(), list, newUidMap);
    entityIdClient.putFeedEntity(newUidMap,  type);
    long endStart = System.currentTimeMillis();
    logger.info(" new uids:" + newUidMap.size());
    logger.info("spend time:" + (endStart - startTime) + " ms");
    // util.mapCombiner(newUidMap, existuidMap);
    // FileUtils.saveFile("/Users/devops/Documents/知识图谱/school/school_uid_map.txt", existuidMap);
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



  public static <T extends EntityNode> void getCheckNames(List<T> entityNodes, List<List<String>> reqs) {
    for (T entityNode : entityNodes) {
      reqs.add(entityNode.getNames());
    }
  }

  /**
   * 将已有uid写入实体字段
   * @param entityNodes
   * @param uidMap
   * @param <T>
   */
  public static <T extends EntityNode> void checkEntityUid(List<T> entityNodes, Map<String,
      String> uidMap) {
    for (T entityNode : entityNodes) {
      List<String> names = entityNode.getNames();
      for (String name : names) {
        if (!"".equals(name) && uidMap.containsKey(name)) {
          entityNode.setUid(uidMap.get(name));
          break;
        }
      }
    }
  }

  /**
   * blank-id mapping uniqueName to uid
   * @param blankUid
   * @param list
   * @param uidMap
   * @param <T>
   */
  public static <T extends EntityNode> void uidFlattenMapping(Map<String, String> blankUid, List<T>  list, Map<String, String> uidMap) {
    Set<Map.Entry<String, String>> entrySet=  blankUid.entrySet();
    Iterator<Map.Entry<String, String>> iterator = entrySet.iterator();
    while(iterator.hasNext()) {
      Map.Entry<String, String> entry = iterator.next();
      String key = entry.getKey();
      String value = entry.getValue();
      int index = Integer.parseInt(key.substring(6));
      uidMap.put(list.get(index).getName(), value);
    }
  }

  public static Map<String, String> getNewUidMap(Map<String, String> existUidMap, Map<String,
      String> retUidMap) {
    Map<String, String> newUidMap = new HashMap<String, String>();
    Set<Map.Entry<String, String>> entrySet=  retUidMap.entrySet();
    Iterator<Map.Entry<String, String>> iterator = entrySet.iterator();
    while(iterator.hasNext()) {
      Map.Entry<String, String> entry = iterator.next();
      String key = entry.getKey();
      String value = entry.getValue();
      if (!existUidMap.containsKey(key)) {
        newUidMap.put(key, value);
      }
    }
    return newUidMap;
  }

  public static void mapCombiner(Map<String, String> map, Map<String, String> resultMap) {
    Set<Map.Entry<String, String>> entrySet=  map.entrySet();
    Iterator<Map.Entry<String, String>> iterator = entrySet.iterator();
    while(iterator.hasNext()) {
      Map.Entry<String, String> entry = iterator.next();
      String key = entry.getKey();
      String value = entry.getValue();
      resultMap.put(key, value);
    }
  }

  public static String longToHex(long i) {
    return Long.toHexString(i);
  }
  public static long hexToLong(String str) {
    if (str.startsWith("0x")) {
      return Long.parseLong(str.substring(2),16);
    } else  {
      return  Long.parseLong(str,16);
    }
  }

  public static void getUidMap(List<String> key, List<String> value, Map<String, String>
      resultMap) {
    int size = key.size();
    if (size != value.size()) {
      return;
    }
    for (int i = 0; i < size; i++) {
      resultMap.put(key.get(i), value.get(i));
    }
  }

}

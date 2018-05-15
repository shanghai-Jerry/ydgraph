package dgraph.node;

import com.google.gson.Gson;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import client.EntityIdClient;
import dgraph.DClient;
import dgraph.put.Nodeput;
import io.dgraph.DgraphProto;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

/**
 * User: JerryYou
 *
 * Date: 2018-05-08
 *
 * Copyright (c) 2018 devops
 *
 * <<licensetext>>
 */
public class NodeUtil {

  private static final Logger logger = LoggerFactory.getLogger(NodeUtil.class);

  public static <T extends EntityNode> void addEntityEdge(DClient dClient, List<T> list) {
    List<Nodeput> updatePutList = new ArrayList<Nodeput>();
    for (T entityNode : list) {
      List<String> pres = new ArrayList<String>();
      List<String> values = new ArrayList<>();
      entityNode.getEdgeValueMap(pres, values, "getUid");
      Nodeput dput = new Nodeput();
      if ("".equals(entityNode.getUid())) {
        continue;
      }
      dput.setUid(entityNode.getUid());
      dput.setEdge_predicates(pres);
      dput.setObjectIds(values);
      updatePutList.add(dput);
    }
    dClient.entityAdd(updatePutList);
  }

  public static <T extends EntityNode> void updateEntity(DClient dClient, List<T> list) {
    List<Nodeput> updatePutList = new ArrayList<Nodeput>();
    for (T school : list) {
      List<String> pres = new ArrayList<String>();
      List<Object> values = new ArrayList<Object>();
      school.getAttrValueMap(pres, values);
      Nodeput dput = new Nodeput();
      if ("".equals(school.getUid())) {
        continue;
      }
      dput.setUid(school.getUid());
      dput.setUniqueId(school.getName());
      dput.setPredicates(pres);
      dput.setValueObjects(values);
      updatePutList.add(dput);
    }
    // dClient.entityAddAttr(updatePutList);
  }


  public static <T extends EntityNode> Map<String, String> insertEntity(DClient dClient, List<T>
      list) {
    Map<String, String> uidMap = new HashMap<>();
    // insert
    List<Nodeput> dputList = new ArrayList<>();
    List<Nodeput> newPutList = new ArrayList<>();
    for (T item : list) {
      List<String> pres = new ArrayList<String>();
      List<String> edge_pres = new ArrayList<String>();
      List<String> objectIds = new ArrayList<String>();
      List<Object> values = new ArrayList<>();
      item.getAttrValueMap(pres, values);
      item.getEdgeValueMap(edge_pres, objectIds, "getUid");
      Nodeput dput = new Nodeput();
      String uid = item.getUid();
      dput.setUniqueId(item.getUnique_id());
      dput.setPredicates(pres);
      dput.setValueObjects(values);
      dput.setEdge_predicates(edge_pres);
      dput.setObjectIds(objectIds);
      if (uid != null && !"".equals(uid)) {
        dput.setUid(uid);
        newPutList.add(dput);
      } else {
        dputList.add(dput);
      }
    }
    if (newPutList.size() > 0) {
      dClient.entityAdd(newPutList);
    }
    if (dputList.size() > 0) {
      DgraphProto.Assigned assigned = dClient.entityInitial(dputList);
      if (assigned != null) {
        // 内部写回子实体的uid
        // NodeUtil.putEntityUid(list, assigned.getUidsMap());
        return assigned.getUidsMap();
      } else  {
        return uidMap;
      }
    }
    return uidMap;
  }

  /**
   *
   * @param src
   * @param <T>
   * @return
   */
  public static <T> List<T> deepCopy(List<T> src) {
    // 深拷贝， 引用对象独立，互不影响
    ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
    ObjectOutputStream out = null;
    try {
      out = new ObjectOutputStream(byteOut);
      out.writeObject(src);
    } catch (IOException e) {
      e.printStackTrace();
    }
    ByteArrayInputStream byteIn = new ByteArrayInputStream(byteOut.toByteArray());
    ObjectInputStream in = null;
    List<T> dest = new ArrayList<>();
    try {
      in = new ObjectInputStream(byteIn);
      dest = (List<T>) in.readObject();
    } catch (IOException e) {
      e.printStackTrace();
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
    }
    return dest;
  }

  public static <T extends EntityNode> Map<String, String> putEntity(DClient dClient, List<T> list) {
    Map<String, String> newUidMap = new HashMap<String, String>();
    List<T> copyList = deepCopy(list);
    long startTime = System.currentTimeMillis();
    // NodeUtil.removUniqueId(copyList);
    int size = copyList.size();
    if (size > 0) {
      logger.info("entity json object :" + new Gson().toJson(copyList.get(0)));
      // 返回没设置uid的实体id的map,已设置uid的不会返回uid
      DgraphProto.Assigned assigned = dClient.multiplyMutationEntity(copyList);
      if (assigned != null) {
        logger.info("get ret uids :" + assigned.getUidsMap().size());
        NodeUtil.uidFlattenMapping(assigned.getUidsMap(), copyList, newUidMap);
      }
      long endStart = System.currentTimeMillis();
      logger.info("spend time:" + (endStart - startTime) + " ms");
      return newUidMap;
    }
    return newUidMap;
  }

  public static <T extends EntityNode> void getCheckNames(List<T> entityNodes, List<List<String>>
      reqs) {
    for (T entityNode : entityNodes) {
      List<String> names = new ArrayList<>();
      names.add(entityNode.getUnique_id());
      reqs.add(names);
    }
  }


  /**
   * 不需要进入dgraph的属性
   */
  public static <T extends EntityNode> void removUniqueId(List<T> entityNodes) {
    for (T entityNode : entityNodes) {
      entityNode.setUnique_id("");
    }
  }


  /**
   * 将已有uid写入实体字段
   * @param list       原始实体list
   * @param resultList 没有uid的实体，新增的
   */
  public static <T extends EntityNode> void setEntityUid(EntityIdClient entityIdClient, List<T>
      list, String type, List<T> resultList) {
    List<List<String>> reqs = new ArrayList<List<String>>();
    Map<String, String> existuidMap = new HashMap<String, String>();
    NodeUtil.getCheckNames(list, reqs);
    entityIdClient.checkEntityList(reqs, existuidMap, type);
    NodeUtil.putEntityUid(list, existuidMap, resultList);
  }


  /**
   * 将已有uid写入实体字段
   * @param entityIdClient
   * @param list
   * @param type
   * @param havaUidList 存在uid的实体
   * @param resultList  没有uid的实体，新增的
   * @param <T>
   */
  public static <T extends EntityNode> void setEntityUid(EntityIdClient entityIdClient, List<T>
      list, String type,  List<T> havaUidList, List<T> resultList) {
    List<List<String>> reqs = new ArrayList<List<String>>();
    Map<String, String> existuidMap = new HashMap<String, String>();
    NodeUtil.getCheckNames(list, reqs);
    entityIdClient.checkEntityList(reqs, existuidMap, type);
    NodeUtil.putEntityUid(list, existuidMap, havaUidList, resultList);
  }


  /**
   * 将已有uid写入实体字段: 拆分数组成一个没有uid的list, 一个有uid的list
   * @param entityNodes
   * @param uidMap
   * @param havaUidList
   * @param resultList
   * @param <T>
   */
  public static <T extends EntityNode> void putEntityUid(List<T> entityNodes, Map<String,
      String> uidMap,  List<T> havaUidList, List<T> resultList) {
    for (T entityNode : entityNodes) {
      String unique_id = entityNode.getUnique_id();
      if (!"".equals(unique_id) && uidMap.containsKey(unique_id)) {
        entityNode.setUid(uidMap.get(unique_id));
        havaUidList.add(entityNode);
      } else {
        resultList.add(entityNode);
      }
    }
  }

  /**
   * 将已有uid写入实体字段: 拆分数组成一个没有uid的list
   * @param entityNodes
   * @param uidMap
   * @param resultList
   * @param <T>
   */
  public static <T extends EntityNode> void putEntityUid(List<T> entityNodes, Map<String,
      String> uidMap, List<T> resultList) {
    for (T entityNode : entityNodes) {
      String unique_id = entityNode.getUnique_id();
      if (!"".equals(unique_id) && uidMap.containsKey(unique_id)) {
        entityNode.setUid(uidMap.get(unique_id));
      } else {
        resultList.add(entityNode);
      }
    }
  }

  public static <T extends EntityNode> void putEntityUid(List<T> entityNodes, Map<String,
      String> uidMap) {
    for (T entityNode : entityNodes) {
      String unique_id = entityNode.getUnique_id();
      if (!"".equals(unique_id) && uidMap.containsKey(unique_id)) {
        entityNode.setUid(uidMap.get(unique_id));
      }
    }
  }

  /**
   * blank-id mapping uniqueName to uid
   */
  public static <T extends EntityNode> void uidFlattenMapping(Map<String, String> blankUid,
                                                              List<T> list, Map<String, String>
                                                                  uidMap) {
    Set<Map.Entry<String, String>> entrySet = blankUid.entrySet();
    Iterator<Map.Entry<String, String>> iterator = entrySet.iterator();
    while (iterator.hasNext()) {
      Map.Entry<String, String> entry = iterator.next();
      String key = entry.getKey();
      String value = entry.getValue();
      int index = Integer.parseInt(key.substring(6));
      if (index >= list.size()) {
        logger.info("uidFlattenMapping error blankUid size not equal list size");
        continue;
      }
      uidMap.put(list.get(index).getName(), value);
    }
  }

  public static Map<String, String> getNewUidMap(Map<String, String> existUidMap, Map<String,
      String> retUidMap) {
    Map<String, String> newUidMap = new HashMap<String, String>();
    Set<Map.Entry<String, String>> entrySet = retUidMap.entrySet();
    Iterator<Map.Entry<String, String>> iterator = entrySet.iterator();
    while (iterator.hasNext()) {
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
    Set<Map.Entry<String, String>> entrySet = map.entrySet();
    Iterator<Map.Entry<String, String>> iterator = entrySet.iterator();
    while (iterator.hasNext()) {
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
      return Long.parseLong(str.substring(2), 16);
    } else {
      return Long.parseLong(str, 16);
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


  /**
   * 泛型: 支持扩展
   */
  public static <T extends EntityNode> void getList(EntityIdClient entityIdClient, List<T>
      entityNodes, List<T> insertList, List<T> updateList) {
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
    // 暂时不检查entitid服务
    // entityIdClient.checkEntityList(reqs, uidMap, type);
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

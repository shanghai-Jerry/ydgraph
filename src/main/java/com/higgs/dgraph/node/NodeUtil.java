package com.higgs.dgraph.node;

import com.google.gson.Gson;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import client.EntityIdClient;
import com.higgs.dgraph.DClient;
import com.higgs.dgraph.del.NodeDel;
import com.higgs.dgraph.put.EdgeFacetPut;
import com.higgs.dgraph.put.EdgeFacetsPut;
import com.higgs.dgraph.put.Nodeput;
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

  public static <T extends EntityNode> Map<String, List<String>> insertEntity(DClient dClient,
                                                                              List<T> list,
                                                                              List<EdgeFacetPut>
                                                                                  edgeFacetPutList) {
    // insert
    Map<String, List<String>> uidMap = new HashMap<>();
    List<Nodeput> dputList = new ArrayList<Nodeput>();
    List<Nodeput> newPutList = new ArrayList<>();
    for (T item : list) {
      List<String> pres = new ArrayList<String>();
      List<Object> values = new ArrayList<>();
      List<String> edge_pres = new ArrayList<String>();
      List<String> objectIds = new ArrayList<String>();
      Nodeput dput = new Nodeput();
      String uid = item.getUid();
      if (uid != null && !"".equals(uid)) {
        item.getAttrValueMap(pres, values);
        item.getEdgeValueMap(edge_pres, objectIds, "getUid");
        dput.setUniqueId(item.getUnique_id());
        dput.setEdge_predicates(edge_pres);
        dput.setObjectIds(objectIds);
        dput.setUid(uid);
        newPutList.add(dput);
      } else {
        item.getAttrValueMap(pres, values);
        item.getEdgeValueMap(edge_pres, objectIds, "getUid");
        dput.setUniqueId(item.getUnique_id());
        dput.setPredicates(pres);
        dput.setValueObjects(values);
        dput.setEdge_predicates(edge_pres);
        dput.setObjectIds(objectIds);
        dputList.add(dput);
      }
    }
    if (newPutList.size() > 0) {
      dClient.entityAdd(newPutList);
    }
    if (dputList.size() > 0) {
      DgraphProto.Assigned assigned = dClient.entityInitial(dputList);
      if (assigned != null) {
        // 写回uid到实体中
        Map<String, String> assignedUidsMap = assigned.getUidsMap();
        NodeUtil.putEntityUid(list, assignedUidsMap);
        NodeUtil.getFacetsUidSrc(assignedUidsMap, edgeFacetPutList);
        NodeUtil.uidReMapping(assignedUidsMap, list, uidMap);
        // 补充facets
        dClient.entityAddFacets(edgeFacetPutList);
        return uidMap;
      } else {
        return uidMap;
      }
    }
    return uidMap;
  }

  public static void deleteEntity(DClient dClient, EntityIdClient entityIdClient, List<NodeDel>
      nodeDelList, String type) {
    entityIdClient.checkDelEntityUid(nodeDelList, type);
    dClient.entityDel(nodeDelList);
  }

  /**
   * 插入新增实体属性，与子实体属性等
   * @param dClient com.higgs.dgraph client
   * @param list 实体数组
   * @param <T> 支持实体泛型，继承自EntityNode
   * @return 实体对应uid和unique_ids的映射
   */
  public static <T extends EntityNode> Map<String, List<String>> insertEntity(DClient dClient,
                                                                              List<T> list) {
    Map<String, List<String>> uidMap = new HashMap<>();
    if (!checkUniqueId(list)) {
      logger.info("Please set unique_id !!");
      return uidMap;
    }
    // insert
    List<Nodeput> dputList = new ArrayList<>();
    List<Nodeput> newPutList = new ArrayList<>();
    for (T item : list) {
      List<String> pres = new ArrayList<String>();
      List<String> edge_pres = new ArrayList<String>();
      List<String> objectIds = new ArrayList<String>();
      List<Object> values = new ArrayList<>();
      Nodeput dput = new Nodeput();
      String uid = item.getUid();
      if (uid != null && !"".equals(uid)) {
        item.getAttrValueMap(pres, values);
        item.getEdgeValueMap(edge_pres, objectIds, "getUid");
        dput.setUniqueId(item.getUnique_id());
        dput.setEdge_predicates(edge_pres);
        dput.setObjectIds(objectIds);
        dput.setUid(uid);
        newPutList.add(dput);
      } else {
        item.getAttrValueMap(pres, values);
        item.getEdgeValueMap(edge_pres, objectIds, "getUid");
        dput.setUniqueId(item.getUnique_id());
        dput.setPredicates(pres);
        dput.setValueObjects(values);
        dput.setEdge_predicates(edge_pres);
        dput.setObjectIds(objectIds);
        dputList.add(dput);
      }
    }
    if (newPutList.size() > 0) {
      logger.info("entityAdd =====> ");
      dClient.entityAdd(newPutList);
    }
    if (dputList.size() > 0) {
      logger.info("entityInitial =====> ");
      DgraphProto.Assigned assigned = dClient.entityInitial(dputList);
      if (assigned != null) {
        // 写回uid到实体中
        NodeUtil.putEntityUid(list, assigned.getUidsMap());
        NodeUtil.uidReMapping(assigned.getUidsMap(), list, uidMap);
        return uidMap;
      } else {
        return uidMap;
      }
    }
    return uidMap;
  }



  /**
   * 深拷贝， 引用对象独立，互不影响
   * @param src 源数组
   * @param <T> 支持实体泛型，继承自EntityNode
   * @return 目标数组
   */
  public static <T> List<T> deepCopy(List<T> src) {

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

  /**
   * json object形式插入实体
   * @param dClient com.higgs.dgraph client
   * @param list 实体数组
   * @param <T> 支持实体泛型，继承自EntityNode
   * @return 实体对应uid和unique_ids的映射
   */
  public static <T extends EntityNode> Map<String, List<String>> putEntity(DClient dClient, List<T>
      list) {
    Map<String, List<String>> newUidMap = new HashMap<>();
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
      reqs.add(entityNode.getUnique_ids());
    }
  }

  /**
   * 不需要进入dgraph的属性
   * @param entityNodes 实体数组
   * @param <T> 支持实体泛型，继承自EntityNode
   */
  public static <T extends EntityNode> void removUniqueId(List<T> entityNodes) {
    for (T entityNode : entityNodes) {
      entityNode.setUnique_ids(new ArrayList<String>());
    }
  }

  /**
   * 将已有uid写入实体字段: 拆分数组成一个没有uid的list, 一个有uid的list
   * @param entityNodes 实体实体数组
   * @param uidMap uidMap
   * @param havaUidList 存在uid的实体数组
   * @param resultList 不存在uid的实体数组
   * @param <T> 支持实体泛型，继承自EntityNode
   */
  public static <T extends EntityNode> void putEntityUid(List<T> entityNodes, Map<String, String>
      uidMap, List<T> havaUidList, List<T> resultList) {
    for (T entityNode : entityNodes) {
      boolean isNewEntity = true;
      List<String> uniqueIdList = entityNode.getUnique_ids();
      for (String unique_id : uniqueIdList) {
        if (!"".equals(unique_id) && uidMap.containsKey(unique_id)) {
          entityNode.setUid(uidMap.get(unique_id));
          havaUidList.add(entityNode);
          isNewEntity = false;
          break;
        }
      }
      if (isNewEntity) {
        resultList.add(entityNode);
      }
    }
  }

  /**
   * 将已有uid写入实体字段: 拆分数组成一个没有uid的list
   * @param entityNodes 实体数组
   * @param uidMap uidMap
   * @param resultList 没有uid的实体数组
   * @param <T> 支持实体泛型，继承自EntityNode
   */
  public static <T extends EntityNode> void putEntityUid(List<T> entityNodes, Map<String, String>
      uidMap, List<T> resultList) {
    for (T entityNode : entityNodes) {
      boolean isNewEntity = true;
      List<String> uniqueIdList = entityNode.getUnique_ids();
      for (String unique_id : uniqueIdList) {
        if (!"".equals(unique_id) && uidMap.containsKey(unique_id)) {
          entityNode.setUid(uidMap.get(unique_id));
          isNewEntity = false;
          break;
        }
      }
      if (isNewEntity) {
        resultList.add(entityNode);
      }
    }
  }

  /**
   * 将已有uid写入实体字段: 不拆分数组
   * @param entityNodes 实体数组
   * @param uidMap uidmAP
   * @param <T> 支持实体泛型，继承自EntityNode
   */
  public static <T extends EntityNode> void putEntityUid(List<T> entityNodes, Map<String, String>
      uidMap) {
    for (T entityNode : entityNodes) {
      List<String> uniqueIdList = entityNode.getUnique_ids();
      for (String unique_id : uniqueIdList) {
        if (!"".equals(unique_id) && uidMap.containsKey(unique_id)) {
          entityNode.setUid(uidMap.get(unique_id));
          break;
        }
      }
    }
  }

  /**
   * 多个names值映射uid下的uid写回
   * @param entityNodes 实体数组
   * @param uidMap uidMap uid -> names
   * @param <T> 支持实体泛型，继承自EntityNode
   */
  public static <T extends EntityNode> void putEntityUidWithNames(List<T> entityNodes, Map<String,
      List<String>> uidMap) {
    List<T> needRemoveNodes = new ArrayList<>();
    for (T entityNode : entityNodes) {
      List<String> uniqueIdList = entityNode.getUnique_ids();
      Set<Map.Entry<String, List<String>>> entrySet = uidMap.entrySet();
      boolean  flag = true;
      for (String unique_id : uniqueIdList) {
        Iterator<Map.Entry<String, List<String>>> iterator = entrySet.iterator();
        while (iterator.hasNext()) {
          Map.Entry<String, List<String>> entry = iterator.next();
          String key = entry.getKey();
          List<String> values = entry.getValue();
          if (!"".equals(unique_id) && values.contains(unique_id)) {
            entityNode.setUid(key);
            flag = false;
            break;
          }
        }
        if (!flag) {
          break;
        }
      }
      if (flag) {
        logger.info("remove don't hava uid entity!");
        needRemoveNodes.add(entityNode);
      }
    }
    for (T entityNode : needRemoveNodes) {
      entityNodes.remove(entityNode);
    }
  }

  /**
   * uid重新映射到names
   * @param keyUidMap key -> uid
   * @param list 实体数组
   * @param uidMap uid -> names
   * @param <T> 支持实体泛型，继承自EntityNode
   */

  public static <T extends EntityNode> void uidReMapping(Map<String, String> keyUidMap, List<T>
      list, Map<String, List<String>> uidMap) {
    for (T entityNode : list) {
      List<String> uniqueIdList = entityNode.getUnique_ids();
      if (uniqueIdList.size() == 0) {
        String uniqueId = entityNode.getUnique_id();
        if (uniqueId != null && !"".equals(uniqueId)) {
          if (keyUidMap.containsKey(uniqueId)) {
            String uid = keyUidMap.get(uniqueId);
            uidMap.put(uid, Arrays.asList(uniqueId));
          }
        }
      } else {
        for (String uniqueId : uniqueIdList) {
          if (keyUidMap.containsKey(uniqueId)) {
            String uid = keyUidMap.get(uniqueId);
            uidMap.put(uid, entityNode.getUnique_ids());
            break;
          }
        }
      }
    }
  }


  /**
   * blank-id mapping uniqueName to uid
   * @param blankUid blank-id -> uid
   * @param list 实体数组
   * @param uidMap uid -> names
   * @param <T> 支持实体泛型，继承自EntityNode
   */
  public static <T extends EntityNode> void uidFlattenMapping(Map<String, String> blankUid,
                                                              List<T> list, Map<String,
      List<String>> uidMap) {
    Set<Map.Entry<String, String>> entrySet = blankUid.entrySet();
    Iterator<Map.Entry<String, String>> iterator = entrySet.iterator();
    while (iterator.hasNext()) {
      Map.Entry<String, String> entry = iterator.next();
      String key = entry.getKey();
      String value = entry.getValue();
      // logger.info("uidFlattenMapping key: " + key + ", value: " + value);
      int index = Integer.parseInt(key.substring(6));
      if (index >= list.size()) {
        logger.fatal("uidFlattenMapping error blankUid size:" + blankUid.size() + " not equal " +
            "list " + "size:" + list.size());
        continue;
      }
      T entityNode = list.get(index);
      List<String> uniqueIdList = entityNode.getUnique_ids();
      entityNode.setUid(value);
      uidMap.put(value, uniqueIdList);
    }
  }

  /**
   * long -> hex
   * @param i long
   * @return hex start with 0x..
   */
  public static String longToHex(long i) {
    return "0x" + Long.toHexString(i);
  }

  /**
   * hex -> long
   * @param str hex string start with 0x.. or not with 0x
   * @return long
   */
  public static long hexToLong(String str) {
    if (str.startsWith("0x")) {
      return Long.parseLong(str.substring(2), 16);
    } else {
      return Long.parseLong(str, 16);
    }
  }

  /**
   * 检查实体中必须存在的uinque_id存在与否
   * @param list 实体数组
   * @param  <T> 支持实体泛型，继承自EntityNode
   * @return pass or not pass
   */
  private static  <T extends  EntityNode> boolean checkUniqueId(List<T> list) {
    for (T entityNode : list) {
      String uniqueId = entityNode.getUnique_id();
      if (uniqueId == null || "".equals(uniqueId)) {
        return false;
      }
    }
    return true;
  }

  public static void getFacetsUidSrc(Map<String, String> map, List<EdgeFacetPut> edgeFacetsPutList) {
    for (EdgeFacetPut edgeFacetPut : edgeFacetsPutList) {
      String src = edgeFacetPut.getSrc();
      if (map.containsKey(src)) {
        edgeFacetPut.setUidSrc(map.get(src));
      }
    }
  }

}

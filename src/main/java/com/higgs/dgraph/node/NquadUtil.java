package com.higgs.dgraph.node;

import com.higgs.dgraph.put.EdgeFacetPut;
import com.higgs.dgraph.put.Nodeput;

import java.util.ArrayList;
import java.util.List;

import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

/**
 * User: JerryYou
 *
 * Date: 2018-07-09
 *
 * Copyright (c) 2018 devops
 *
 * 已知uid的rdf格式, 好像bulk loader不支持用户自分配的uid
 *
 * <<licensetext>>
 */
public class NquadUtil {

  private static final Logger LOGGER = LoggerFactory.getLogger(NquadUtil.class);
  /**
   * 批量<uid> <relation> <uid>的方式format
   * @param src start uid
   * @param pred relation
   * @param dest end uid
   * @return format string
   */
  private static String edgeFormat(String src, String pred, String dest) {
    return String.format("<%s> <%s> <%s> . \n", src, pred, dest);
  }

  /**
   * 批量<uid> <relation> "attribute"的方式format
   * @param src start uid
   * @param pred relation
   * @param dest end uid
   * @return format string
   */
  private static String attrFormat(String src, String pred, String dest) {
    return String.format("<%s> <%s> \"%s\" . \n", src, pred, dest);
  }

  /**
   * 增加边的facets
   * @param src start uid or unique_id
   * @param pred relation
   * @param dest end uid
   * @param facets 边的属性集合
   * @return Nquad string
   */
  private static String edgeUidFacetsFormat(String src, String pred, String dest, List<String> facets) {
    StringBuilder stringBuilder = new StringBuilder();
    int facetSize = facets.size();
    for (int i = 0; i < facetSize; i++) {
      if (i == 0) {
        stringBuilder.append(facets.get(i));
      } else {
        stringBuilder.append("," + facets.get(i));
      }
    }
    return String.format("<%s> <%s> <%s> (%s) . \n", src, pred, dest, stringBuilder.toString());
  }

  /**
   * 增加边的facets
   * @param src start uid or unique_id
   * @param pred relation
   * @param dest end uid
   * @param facets 边的属性集合
   * @return Nquad string
   */
  private static String edgeAttributeFacetsFormat(String src, String pred, String dest,List<String> facets) {
    StringBuilder stringBuilder = new StringBuilder();
    int facetSize = facets.size();
    for (int i = 0; i < facetSize; i++) {
      if (i == 0) {
        stringBuilder.append(facets.get(i));
      } else {
        stringBuilder.append("," + facets.get(i));
      }
    }
    return String.format("<%s> <%s> \"%s\" (%s) . \n", src, pred, dest, stringBuilder.toString());
  }


  public static List<String> entityFacetNquads(List<EdgeFacetPut> edgeFacetPutList) {
    List<String> stringList = new ArrayList<>();
    for (EdgeFacetPut edgeFacetPut : edgeFacetPutList) {
      String src = edgeFacetPut.getUidSrc();
      String dst = edgeFacetPut.getDst();
      String predicate = edgeFacetPut.getPredicate();
      EdgeFacetPut.PredicateType predicateType = edgeFacetPut.getPredicateType();
      List<String> facet = edgeFacetPut.getFacets();
      String result = "";
      if (src == null || "".equals(src)) {
        continue;
      }
      if (src.startsWith("0x")) {
        switch (predicateType) {
          case UID:
            result = edgeUidFacetsFormat(src, predicate, dst, facet);
            break;
          case ATTRIBUTE:
            result = edgeAttributeFacetsFormat(src, predicate, dst, facet);
            break;
        }
      } else if (src != null && !"".equals(src)) {
        switch (predicateType) {
          case UID:
            result = edgeUidFacetsFormat(src, predicate, dst, facet);
            break;
          case ATTRIBUTE:
            result = edgeAttributeFacetsFormat(src, predicate, dst, facet);
            break;
        }
      }
      if (!"".equals(result)) {
        stringList.add(result);
      }

    }
    return stringList;
  }
  /**
   * Nquad形式:
   * <uid> <> <>
   * <_:uniquer_id> <> <>
   */
  public static List<String> entityNquads(List<Nodeput> putList) {
    int ids = putList.size();
    List<String> stringList = new ArrayList<>();
    for (int j = 0; j < ids; j++) {
      Nodeput nodeput = putList.get(j);
      String uid = nodeput.getUid();
      List<String> predicates = nodeput.getPredicates();
      List<Object> values = nodeput.getValueObjects();
      List<String> edge_pred = nodeput.getEdge_predicates();
      List<String> objectIds = nodeput.getObjectIds();
      if (edge_pred.size() != objectIds.size()) {
        LOGGER.fatal("entity add predicates edge length not equal values ");
      }
      int size = predicates.size();
      if (size != values.size()) {
        LOGGER.fatal("entity add predicates attr length not equal values ");
      }
      for (int i = 0; i < size; i++) {
        String value = String.valueOf(values.get(i).toString());
        String pred = predicates.get(i);
        if (uid != null && !"".equals(uid)) {
          String result = attrFormat(uid, pred, value);
          stringList.add(result);
        }
      }
      // edge feed
      for (int k = 0; k < edge_pred.size(); k++) {
        String edgePredicate = edge_pred.get(k);
        String objectId = objectIds.get(k);
        if (uid != null && !"".equals(uid)) {
          String result = edgeFormat(uid, edgePredicate, objectId);
          stringList.add(result);
        }
      }
    }
    return stringList;
  }

  /**
   * 获取实体的rdf格式文件
   * @param list 实体数组
   * @param <T>  实体类型
   * @return rdf Nquad list
   */
  public static <T extends EntityNode> List<String> getEntityNquads(List<T> list,
                                                                    List<EdgeFacetPut> edgeFacetPutList) {

    // 预处理流程
    // Map<String, String> assignedUidMap =  NodeUtil.getAssignedUid(dClient, list);
    // Map<String, List<String>> retUidMap = NodeUtil.putEnitityAssignedUid(assignedUidMap, list);
    // NodeUtil.putFacetAssignedUid(assignedUidMap, edgeFacetPutList);
    // generate nquads
    // List<String> entityNquads = NquadUtil.getEntityNquads(list, edgeFacetPutList);

    // get nquads
    List<String> nQuands;
    List<Nodeput> dputList = new ArrayList<>();
    for (T item : list) {
      List<String> pres = new ArrayList<>();
      List<Object> values = new ArrayList<>();
      List<String> edge_pres = new ArrayList<>();
      List<String> objectIds = new ArrayList<>();
      String uid = item.getUid();
      if (uid != null && !"".equals(uid) ) {
        Nodeput dput = new Nodeput();
        item.getAttrValueMap(pres, values);
        item.getEdgeValueMap(edge_pres, objectIds, "getUid");
        dput.setUid(uid);
        dput.setPredicates(pres);
        dput.setValueObjects(values);
        dput.setEdge_predicates(edge_pres);
        dput.setObjectIds(objectIds);
        dputList.add(dput);
      }
    }

    // entity
    nQuands = entityNquads(dputList);
    // facets
    List<String> hasFacetsNquads = entityFacetNquads(edgeFacetPutList);
    nQuands.addAll(hasFacetsNquads);
    return nQuands;
  }

}

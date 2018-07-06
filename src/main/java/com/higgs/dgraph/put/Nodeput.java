package com.higgs.dgraph.put;


import java.util.List;

public class Nodeput {

  private String uid;
  // List<String> uniqueIds;
  private String uniqueId;
  // predicate is scalar
  private List<String> predicates;
  private List<Object> valueObjects;
  // predicate is uid
  private List<String> edge_predicates;
  private List<String> objectIds;

  private String type;

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getUniqueId() {
    return uniqueId;
  }

  public void setUniqueId(String uniqueId) {
    this.uniqueId = uniqueId;
  }

  public List<Object> getValueObjects() {
    return valueObjects;
  }

  public void setValueObjects(List<Object> valueObjects) {
    this.valueObjects = valueObjects;
  }

  public List<String> getObjectIds() {
    return objectIds;
  }

  public List<String> getEdge_predicates() {
    return edge_predicates;
  }

  public void setEdge_predicates(List<String> edge_predicates) {
    this.edge_predicates = edge_predicates;
  }

  public void setObjectIds(List<String> objectIds) {
    this.objectIds = objectIds;
  }

  public List<String> getPredicates() {
    return predicates;
  }

  public void setPredicates(List<String> predicates) {
    this.predicates = predicates;
  }


  public String getUid() {
    return uid;
  }

  public void setUid(String uid) {
    this.uid = uid;
  }

}

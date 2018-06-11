package com.higgs.dgraph.put;

import java.util.ArrayList;
import java.util.List;

/**
 * User: JerryYou
 *
 * Date: 2018-06-04
 *
 * Copyright (c) 2018 devops
 *
 * <<licensetext>>
 */
public class EdgeFacetPut {
  public enum PredicateType {
    UID, ATTRIBUTE
  }

  public EdgeFacetPut() {

  }
  private String src;
  private String uidSrc;
  private String predicate;
  private EdgeFacetPut.PredicateType predicateType;
  private String dst;
  private List<String> facets = new ArrayList<>();
  public EdgeFacetPut(String src, String predicate, EdgeFacetPut.PredicateType predicateType,
                      String dst, List<String> facets) {
    this.src = (src);
    this.predicate = predicate;
    this.predicateType = predicateType;
    this.dst = dst;
    this.facets = (facets);
  }

  public String getSrc() {
    return src;
  }

  public String getUidSrc() {
    return uidSrc;
  }

  public void setUidSrc(String uidSr) {
    this.uidSrc = uidSr;
  }

  public void setSrc(String src) {
    this.src = src;
  }

  public String getPredicate() {
    return predicate;
  }

  public void setPredicate(String predicate) {
    this.predicate = predicate;
  }

  public PredicateType getPredicateType() {
    return predicateType;
  }

  public void setPredicateType(PredicateType predicateType) {
    this.predicateType = predicateType;
  }

  public String getDst() {
    return dst;
  }

  public void setDst(String dst) {
    this.dst = dst;
  }

  public List<String> getFacets() {
    return facets;
  }

  public void setFacets(List<String> facets) {
    this.facets = facets;
  }
}

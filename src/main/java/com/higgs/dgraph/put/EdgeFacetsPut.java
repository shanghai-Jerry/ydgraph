package com.higgs.dgraph.put;

import java.util.ArrayList;
import java.util.List;

@Deprecated
public class EdgeFacetsPut {

  public enum PredicateType {
    UID, ATTRIBUTE
  }

  private List<String> srcs;
  private List<String> predicates;
  private List<String> dsts;
  private List<PredicateType> predicateTypes;

  private List<List<String>> facets = new ArrayList<>();

  public List<String> getDst() {
    return dsts;
  }

  public List<PredicateType> getPredicateTypes() {
    return predicateTypes;
  }

  public void setPredicateTypes(List<PredicateType> predicateTypes) {
    this.predicateTypes = predicateTypes;
  }

  public void setDsts(List<String> dst) {
    this.dsts = dst;
  }

  public List<String> getDsts() {
    return dsts;
  }

  public List<String> getPredicates() {
    return predicates;
  }

  public void setPredicates(List<String> predicate) {
    this.predicates = predicate;
  }

  public List<String> getSrcs() {

    return srcs;
  }

  public void setSrcs(List<String> src) {
    this.srcs = src;
  }

  public List<List<String>> getFacets() {
    return facets;
  }

  public void setFacets(List<List<String>>facets) {
    this.facets = facets;
  }
}

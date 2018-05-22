package dgraph.put;

import java.util.ArrayList;
import java.util.List;

public class EdgeFacetsPut {

  private List<String> srcs;
  private List<String> predicates;
  private List<String> dsts;
  private List<List<String>> facets = new ArrayList<>();

  public List<String> getDst() {
    return dsts;
  }

  public void setDsts(List<String> dst) {
    this.dsts = dst;
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

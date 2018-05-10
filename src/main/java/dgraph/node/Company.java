package dgraph.node;

import java.util.List;

public class Company  extends  EntityNode  {

  String location;
  String legal_person;
  String establish_at;

  public String getLocation() {
    return location;
  }

  public void setLocation(String location) {
    this.location = location;
  }

  public String getLegal_person() {
    return legal_person;
  }

  public void setLegal_person(String legal_person) {
    this.legal_person = legal_person;
  }

  public String getEstablish_at() {
    return establish_at;
  }

  public void setEstablish_at(String establish_at) {
    this.establish_at = establish_at;
  }

  @Override
  public void getAttrValueMap(List<String> pre, List<Object> values) {
    pre.add("type");
    values.add(this.getType());
    pre.add("name");
    values.add(this.getName());
    pre.add("legal_person");
    values.add(this.getLegal_person());
    pre.add("location");
    values.add(this.getLocation());
    pre.add("establish_at");
    values.add(this.getEstablish_at());
  }

  @Override
  public void getEdgeValueMap(List<String> pre, List<Object> values) {
    if ("".equals(this.getUid()) && "".equals(this.getHas_label().getUid())) {
      return;
    }
    pre.add("has_label");
    values.add(this.getHas_label().getUid());
  }
}

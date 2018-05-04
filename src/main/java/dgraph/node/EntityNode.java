package dgraph.node;

import java.util.List;

public class EntityNode {

  String uid;
  String uniqueId;
  String name;
  String type;


  public String getUniqueId() {
    return uniqueId;
  }

  public void setUniqueId(String uniqueId) {
    this.uniqueId = uniqueId;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getUid() {
    return uid;
  }

  public void setUid(String uid) {
    this.uid = uid;
  }

  public void getStrAttrValueMap(List<String> pre, List<String> values) { }

  public void getAttrValueMap(List<String> pre, List<Object> values) {}

}

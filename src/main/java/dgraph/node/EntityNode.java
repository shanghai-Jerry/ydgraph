package dgraph.node;

import java.io.Serializable;
import java.util.List;
/**
 * User: JerryYou
 *
 * Date: 2018-05-08
 *
 * Copyright (c) 2018 devops
 *
 * <<licensetext>>
 */
public class EntityNode implements Serializable {

  String uid;
  // _:uniqueId <name> value
  String uniqueId;
  // 实体名称
  String name;
  // 实体类别
  String type;
  // 外部标识的names external ids
  List<String> names;

  public List<String> getNames() { return names; }

  public void setNames(List<String> names) { this.names = names; }

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

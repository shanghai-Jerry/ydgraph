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
 * 入库： 分两种形式
 * 1. with json object
 * 2. with rdf <uid_1>  <predicate> <uid_2>
 *
 * <<licensetext>>
 */
public class EntityNode implements Serializable {

  String uid;
  // _:uniqueId <name> value
  // 检查是否存在实体的唯一标识
  String unique_id;
  // 实体名称
  String name;
  // 实体label
  String label_name;
  // 实体类型
  String type;
  // 实体类别
  Label has_label;

  public Label getHas_label() {
    return has_label;
  }

  public void setHas_label(Label has_label) {
    this.has_label = has_label;
  }

  public String getLabel_name() {
    return label_name;
  }

  public void setLabel_name(String label_name) {
    this.label_name = label_name;
  }

  public String getUnique_id() {
    return unique_id;
  }

  public void setUnique_id(String unique_id) {
    this.unique_id = unique_id;
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

  public void getAttrValueMap(List<String> pre, List<Object> values) {}

  public void getEdgeValueMap(List<String> pre, List<Object> values) {}

}

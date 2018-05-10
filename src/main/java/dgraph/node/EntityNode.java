package dgraph.node;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.List;

import utils.util;

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

  public void getDeclaredFields(Object object, Class clazz, List<String> pre, List<Object> values){
    Field[] fields = clazz.getDeclaredFields();
    Field.setAccessible(fields,   true);
    for (Field field : fields) {
      try {
        pre.add(field.getName());
        values.add(field.get(object));
        util.println("name" , field.getName());
      } catch (IllegalAccessException e) {
      }
    }
  }

  public  void getAttrValueMap(List<String> pre, List<Object> values) {
   /* pre.add("type");
    values.add(this.getType());
    pre.add("name");
    values.add(this.getName());
    pre.add("alias");
    values.add(this.getAlias());
    pre.add("eng_name");
    values.add(this.getEng_name());*/
    getDeclaredFields(this, this.getClass(), pre, values);;
    getDeclaredFields(this, this.getClass().getSuperclass(), pre, values);
  }

  public void getEdgeValueMap(List<String> pre, List<Object> values) {
    // ... todo
  }

}

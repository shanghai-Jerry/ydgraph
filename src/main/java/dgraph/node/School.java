package dgraph.node;

import com.google.gson.Gson;

import org.apache.hadoop.util.LimitInputStream;

import java.util.List;

public class School extends EntityNode {

  String code;
  String alias;
  String engName;

  public String getUid() {
    return uid;
  }

  public void setUid(String uid) {
    this.uid = uid;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }

  public String getAlias() {
    return alias;
  }

  public void setAlias(String alias) {
    this.alias = alias;
  }

  public String getEngName() {
    return engName;
  }

  public void setEngName(String engName) {
    this.engName = engName;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  @Override
  public  void getAttrValueMap(List<String> pre, List<Object> values) {
    pre.add("type");
    values.add(this.getType());
    pre.add("name");
    values.add(this.getName());
    pre.add("alias");
    values.add(this.getAlias());
    pre.add("english_name");
    values.add(this.getEngName());
  }

  @Override
  public void getStrAttrValueMap(List<String> pre, List<String> values) {
    pre.add("type");
    values.add(this.getType());
    pre.add("name");
    values.add(this.getName());
    pre.add("alias");
    values.add(this.getAlias());
    pre.add("english_name");
    values.add(this.getEngName());
  }

  @Override
  public String toString() {
    return new Gson().toJson(this);
  }
}

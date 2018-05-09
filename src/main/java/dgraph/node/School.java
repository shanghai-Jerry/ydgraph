package dgraph.node;

import com.google.gson.Gson;

import java.util.List;

public class School extends EntityNode {

  String code;
  String alias;
  String engName;

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
  @Override
  public  void getAttrValueMap(List<String> pre, List<Object> values) {
    pre.add("type");
    values.add(this.getType());
    pre.add("name");
    values.add(this.getName());
    pre.add("alias");
    values.add(this.getAlias());
    pre.add("engName");
    values.add(this.getEngName());
  }

  @Override
  public String toString() {
    return new Gson().toJson(this);
  }
}

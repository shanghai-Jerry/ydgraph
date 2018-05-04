package dgraph.node;

import com.google.gson.Gson;

import java.util.List;

public class Major extends EntityNode {

  String code;

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

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  @Override
  public String toString() {
    return new Gson().toJson(this);
  }

  public void getStrAttrValueMap(List<String> pre, List<String> values) {
    pre.add("type");
    values.add(this.getType());
    pre.add("name");
    values.add(this.getName());
    pre.add("code");
    values.add(this.getCode());
  }

}

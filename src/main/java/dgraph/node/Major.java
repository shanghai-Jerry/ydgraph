package dgraph.node;

import com.google.gson.Gson;

import java.util.List;

public class Major extends EntityNode {

  int code;

  public int getCode() {
    return code;
  }

  public void setCode(int code) {
    this.code = code;
  }

  @Override
  public  void getAttrValueMap(List<String> pre, List<Object> values) {
    pre.add("type");
    values.add(this.getType());
    pre.add("name");
    values.add(this.getName());
    pre.add("code");
    values.add(this.getCode());
  }

  @Override
  public String toString() {
    return new Gson().toJson(this);
  }

}

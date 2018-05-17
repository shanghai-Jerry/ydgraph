package dgraph.node;

import com.google.gson.Gson;

public class Major extends EntityNode {

  private int code;

  public int getCode() {
    return code;
  }

  public void setCode(int code) {
    this.code = code;
  }

  @Override
  public String toString() {
    return new Gson().toJson(this);
  }

}

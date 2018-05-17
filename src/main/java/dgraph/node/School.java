package dgraph.node;

import com.google.gson.Gson;

public class School extends EntityNode {

  private  int code;
  private String alias;
  private  String eng_name;

  public int getCode() {
    return code;
  }

  public void setCode(int code) {
    this.code = code;
  }

  public String getAlias() {
    return alias;
  }

  public void setAlias(String alias) {
    this.alias = alias;
  }

  public String getEng_name() {
    return eng_name;
  }

  public void setEng_name(String eng_name) {
    this.eng_name = eng_name;
  }
  @Override
  public String toString() {
    return new Gson().toJson(this);
  }
}

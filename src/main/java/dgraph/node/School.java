package dgraph.node;

import com.google.gson.Gson;

import java.lang.reflect.Field;
import java.util.List;

import utils.util;

public class School extends EntityNode {

  String code;
  String alias;
  String eng_name;

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

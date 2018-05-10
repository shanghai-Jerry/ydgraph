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

  @Override
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

  @Override
  public String toString() {
    return new Gson().toJson(this);
  }
}

package dgraph.node;

import java.util.List;

public class Industry extends  EntityNode {

  int code;

  String parent_industry_uid;

  Industry parent_industry;

  public String getParent_industry_uid() {
    return parent_industry_uid;
  }

  public void setParent_industry_uid(String parent_industry_uid) {
    this.parent_industry_uid = parent_industry_uid;
  }

  public int getCode() {
    return code;
  }

  public void setCode(int code) {
    this.code = code;
  }

  public Industry getParent_industry() {
    return parent_industry;
  }

  public void setParent_industry(Industry parent_industry) {
    this.parent_industry = parent_industry;
  }
  @Override
  public void getAttrValueMap(List<String> pre, List<Object> values) {
    pre.add("type");
    values.add(this.getType());
    pre.add("name");
    values.add(this.getName());
    pre.add("code");
    values.add(this.getCode());
  }

  @Override
  public void getEdgeValueMap(List<String> pre, List<Object> values) {
    if ("".equals(this.getUid()) && "".equals(this.getParent_industry_uid())) {
      return;
    }
    pre.add("parent_industry");
    values.add(this.getParent_industry_uid());
  }
}

package dgraph.node;

public class Industry extends  EntityNode {

  int code;

  Industry parent_industry;

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
}

package dgraph.node;

public class Industry extends  EntityNode {

  int code;

  Industry partent_industry;

  public int getCode() {
    return code;
  }

  public void setCode(int code) {
    this.code = code;
  }

  public Industry getPartent_industry() {
    return partent_industry;
  }

  public void setPartent_industry(Industry partent_industry) {
    this.partent_industry = partent_industry;
  }
}

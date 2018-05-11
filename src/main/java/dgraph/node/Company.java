package dgraph.node;

public class Company extends EntityNode {

  String location;
  String legal_person;
  String establish_at;

  public String getLocation() {
    return location;
  }

  public void setLocation(String location) {
    this.location = location;
  }

  public String getLegal_person() {
    return legal_person;
  }

  public void setLegal_person(String legal_person) {
    this.legal_person = legal_person;
  }

  public String getEstablish_at() {
    return establish_at;
  }

  public void setEstablish_at(String establish_at) {
    this.establish_at = establish_at;
  }

}

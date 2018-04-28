package dgraph;

public class Config {

  public static  String schema =
      "uid:int . \n" +
      "name:string @index(exact,term) . \n" +
      "age:int @index(int) . \n" +
      "id:string@index(exact,term) .\n";

  public static  String updateSchema =
          "gender:int @index(int) . \n";
}

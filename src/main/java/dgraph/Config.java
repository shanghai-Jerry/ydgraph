package dgraph;

public class Config {

  public static final String TEST_HOSTNAME = "172.20.0.68";

  public static final int TEST_PORT = 9080;

  public static final String EntityId_Host = "172.20.0.14";

  public static final int EntityIdService_PORT = 26544;

  public  static  int batch = 20000;

  public static  String schema =
          "uid:int . \n" +
          "name:string @index(exact,term) . \n" +
          "alias:string@index(exact,term) .\n" +
          "code:string @index(exact,term) . \n" +
          "english_name:string @index(exact,term) . \n";

  public static String updateSchema =
            "uid:int . \n" +
            "name:string @index(exact,term) . \n" +
            "alias:string@index(exact,term) .\n" +
            "code:string @index(exact,term) . \n" +
            "type:string @index(exact,term)  . \n" +
            "english_name:string @index(exact,term) . \n"
      ;

  public static enum EntityType {

  }

}

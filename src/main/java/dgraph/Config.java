package dgraph;

public class Config {

  public static final String TEST_HOSTNAME = "172.20.0.8";

  public static final int TEST_PORT = 9080;

  public static final String EntityId_Host = "172.20.0.14";

  public static final int EntityIdService_PORT = 26544;

  public  static  int batch = 200;

  public static  String schema =
          "uid:int . \n" +
          "name:string @index(exact,term) . \n" +
          "alias:string@index(exact,term) .\n" +
          "code:string @index(exact,term) . \n" +
          "english_name:string @index(exact,term) . \n";

  public static String updateSchema =
            "uid:int . \n" +
            "name:string @index(hash,term,trigram) . \n" +
            "alias:string .\n" +
            "code:string . \n" +
            "type:string . \n" +
            "engName:string . \n" +
            "location:string  .\n" +
            "legal_person:string .\n" +
            "establish_at:string .\n" +
            "names:string .\n";

}

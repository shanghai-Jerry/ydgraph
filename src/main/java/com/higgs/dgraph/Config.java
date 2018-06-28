package com.higgs.dgraph;

import java.util.Arrays;
import java.util.List;

public class Config {

  public static final List<String> LOCAL_HOST_NAME = Arrays.asList("127.0.0.1:9080");

  public static final List<String> addressList =  Arrays.asList(
      "172.20.0.8:9080",
      "172.20.0.9:9080",
      "172.20.0.10:9080"
  );

  public static final List<String> TEST_VM_HOSTNAME = Arrays.asList("172.20.0.68:9080");

  public static final List<String> TEST_HOSTNAME = Arrays.asList("172.20.0.14:9080");

  public static final String ENTITY_ID_HOST = "172.20.0.14";

  // 公司归一化服务地址: 172.20.0.14:26543

  public static final int ENTITY_ID_SERVICE_PORT = 26544;

  public static final int ENTITY_ID_SERVICE_PORT_TEST = 26545;

  // 批次小一点速度快一些，但是不能太小
  public  static  int batch = 200;

  public static  String schema =
          "uid:int . \n" +
          "name:string  . \n" +
          "unique_id:string .\n" +
          "alias:string .\n" +
          "code:int . \n" +
          "type:string .  \n" +
          "gender：string .\n " +
          "eng_name:string . \n" +
          "location:string  .\n" +
          "legal_person:string .\n" +
          "establish_at:string .\n" +
          "company:uid .\n" +
          "industry:uid .\n" +
          "parent_industry:uid .\n" +
          "candidate_company:uid .\n" +
          "candidate_school:uid .\n" +
          "birthday: dateTime @index(year) . \n" +
          "started_work_at:dateTime @index(year) . \n" +
          "current_location_code: int ."


      ;

  public static String updateSchema =
            "birthday: dateTime @index(year) . \n" +
            "started_work_at:dateTime @index(year) . \n"
      ;

  public static String checkSchema =
      "name:string @index(hash) @upsert .\n"
      ;

}

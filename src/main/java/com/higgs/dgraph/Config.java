package com.higgs.dgraph;

import com.higgs.dgraph.kb_system.schema.Schema;

import java.util.Arrays;
import java.util.List;

public class Config {

  public static final String LOCAL_HOST_NAME = "127.0.0.1";

  public static final List<String> addressList =  Arrays.asList(
      "172.20.0.9:9080"
  );

  public static final List<String> TEST_VM_HOSTNAME = Arrays.asList("172.20.0.68:9080");

  public static final List<String> TEST_HOSTNAME = Arrays.asList("172.20.0.14:9080");

  // 公司归一化服务地址: 172.20.0.14:26544
  public static final String ENTITY_ID_HOST = "172.20.0.14";

  public static final int ENTITY_ID_SERVICE_PORT = 26544;

  public static final int ENTITY_ID_SERVICE_PORT_TEST = 26543;
  // Dgraph query server
  public static final String DGRAPH_QUERY_HOST = "172.20.0.14";

  public static final int DGRAPH_QUERY_PORT = 26549;

  // 批次小一点速度快一些，但是不能太小
  public  static  int batch = 200;

  public static String type_schema = "type <实体类型> {\n" +
      "   name: string\n" +
      "}\n" +
      "type <学校类型> {\n" +
      "  name: string\n" +
      "} \n" +
      "type <实体> {\n" +
      "  name: string\n" +
      "} \n" +
      "type <公司类型> {\n" +
      "  name: string\n" +
      "} \n"
      ;

  public static  String getKbSchema() {
    return Schema.generateEntityTypeSchema()
        + Schema.generateEntityAttributeSchema()
        + Schema.generateEntityRealtionsSchema();
  }

  public static  String schema =
          "uid:int . \n" +
          "name:string  . \n" +
          "unique_id:string .\n" +
          "alias:string .\n" +
          "code:int . \n" +
          "type:string .  \n" +
          "gender:string .\n " +
          "eng_name:string . \n" +
          "location:string  .\n" +
          "legal_person:string .\n" +
          "establish_at:string .\n" +
          "company:uid .\n" +
          "industry:uid .\n" +
          "parent_industry:uid .\n" +
          "candidate_school:uid .\n" +
          "candidate_dept:uid  . \n" +
          "birthday: int @index(int) . \n" +
          "started_work_at:int @index(int) . \n" +
          "current_location_code: int @index(int) . \n" +
          "annual_salary:float @index(float) . \n"


      ;

  public static String update_schema_type = Schema.generateEntityTypeSchema();

  public static String updateSchema =
               "name:string @index(term) . \n" +
               "entity-type:[string] @index(term) . \n" +
               "school-type:[string] @index(term) .\n" +
                // 关系
                "company-corptype:[uid] @reverse . \n" +
                "school-schooltype:[uid] @reverse . \n" +
                "entity-entitytype:[uid] @reverse . \n" +
                "is-synonym:[uid] @reverse . \n" +
                "superior-industry:[uid] @reverse . \n" +
                "keyword-direction:[uid] @reverse . \n" +
                "keyword-skill:[uid] @reverse . \n" +
                "keyword-topic:[uid] @reverse . \n" +
                "func-conflictfunc:[uid] @reverse . \n" +
                "keyword-cert:[uid] @reverse . \n" +
                "majorrelates-major:[uid] @reverse . \n" +
                "keyword-school:[uid] @reverse . \n" +
                "keyword-company:[uid] @reverse . \n" +
                "major-majorcategory:[uid] @reverse . \n" +
                "major-majordiscipline:[uid] @reverse . \n" +
                "is-similarity:[uid] @reverse . \n" +
                "keyword-industry:[uid] @reverse . \n" +
                "conflictword-industry:[uid] @reverse . \n" +
                "keyword-location:[uid] @reverse . \n" +
                "highergold-cert:[uid] @reverse . \n" +
                "company-companygroup:[uid] @reverse . \n" +
                "keyword-jobtitle:[uid] @reverse . \n" +
                "jobtitle-func:[uid] @reverse . \n" +
                "top-func:[uid] @reverse . \n" +
                "func-industry:[uid] @reverse . \n" +
                "jobtitle-industry:[uid] @reverse . \n" +
                "keyword-jobrank:[uid] @reverse . \n" +
                "conflictword-jobrank:[uid] @reverse . \n" +
                "top-orangeindustry:[uid] @reverse . \n" +
                "company-orangeindustry:[uid] @reverse . \n" +
                "keyword-orangeindustry:[uid] @reverse . \n" +
                "direction-func:[uid] @reverse . \n" +
                "skill-func:[uid] @reverse . \n" +
                "superior-direction:[uid] @reverse . \n" +
                "superior-skill:[uid] @reverse . \n" +
                "superior-cert:[uid] @reverse . \n" +
                "highergold-companytype:[uid] @reverse . \n" +
                "keyword-major:[uid] @reverse . \n" +
                "keyword-attribute:[uid] @reverse . \n"


      ;

  public static String checkSchema =
       "name:string .\n"
      ;

}

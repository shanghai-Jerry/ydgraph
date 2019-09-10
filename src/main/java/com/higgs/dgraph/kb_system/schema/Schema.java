package com.higgs.dgraph.kb_system.schema;

import java.util.Arrays;
import java.util.List;

/**
 * User: JerryYou
 *
 * Date: 2019-08-21
 *
 * Copyright (c) 2018 devops
 *
 * <<licensetext>>
 */
public class Schema {

  // 实体
  public enum Entity {
    NONE("未知", Arrays.asList(
        Attribute.NAME
    )),
    ENTITY_TYPE_ENTITY("实体类型",Arrays.asList(
        Attribute.NAME)
    ),
    CORP_TYPE_ENTITY("公司类型",Arrays.asList(
        Attribute.NAME
    )),
    SCHOOL_TYPE_ENTITY("学校类型",Arrays.asList(
        Attribute.NAME
    )),
    ENTITY("实体",Arrays.asList(
        Attribute.NAME
    )),
    KEYWORD("关键词",Arrays.asList(
        Attribute.NAME
    )),

    JOB_FUNCTION("职能",Arrays.asList(
        Attribute.NAME
    )),

    DIRECTION("方向",Arrays.asList(Attribute.NAME)),

    INDUSTRY("行业",Arrays.asList(
        Attribute.NAME,
        Attribute.IND_CODE
    )),

    SKILL("技能",Arrays.asList(Attribute.NAME)),

    TOPIC("主题",Arrays.asList(Attribute.NAME)),

    CERTIFICATE("证书",Arrays.asList(
        Attribute.NAME,
        Attribute.CERT_CODE
    )),

    MAJOR("专业",Arrays.asList(
        Attribute.NAME,
        Attribute.MAJOR_CODE
    )),

    SCHOOL("学校",Arrays.asList(
        Attribute.NAME,
        Attribute.SCHOOL_CODE,
        Attribute.SCHOOL_TYPE
    )),

    COMPANY("公司",Arrays.asList(
        Attribute.NAME,
        Attribute.CORP_TYPE,
        Attribute.CORP_ALIAS,
        Attribute.CORP_ENG_NAME
    )),

    MAJOR_CATEGORY("专业大类",Arrays.asList(
        Attribute.NAME
    )),

    MAJOR_DISCIPLINE("专业学科",Arrays.asList(
        Attribute.NAME
    )),

    LOCATION("城市",Arrays.asList(
        Attribute.NAME,
        Attribute.LOC_CODE,
        Attribute.CITY_TYPE,
        Attribute.LOC_CITY_CODE
    )),

    KNOW_NOT_RECOGNIZE("已知未识别",Arrays.asList(
        Attribute.NAME
    )),

    CONSENSUS("共识",Arrays.asList(
        Attribute.NAME,
        Attribute.CONSENSUS_TYPE,
        Attribute.CONSENSUS_MAX_SCORE,
        Attribute.CONSENSUS_DESC,
        Attribute.CONSENSUS_FACET,
        Attribute.CONSENSUS_CLASS_NAME
    )),
    JOB_TITLE("职位",Arrays.asList(
        Attribute.NAME
    )),
    JOB_RANK("职级",Arrays.asList(
        Attribute.NAME
    )),
    ATTRIBUTE("属性值类型",Arrays.asList(
        Attribute.NAME
    )),
    IT_ORANGE_INDUSTRY("IT桔子行业",Arrays.asList(
        Attribute.NAME
    )),
    DEPARTMENT("部门",Arrays.asList(
        Attribute.NAME
    ))
    ;
    private String name;

    public List<Attribute> getAttributes() {
      return attributes;
    }

    public void setAttributes(List<Attribute> attributes) {
      this.attributes = attributes;
    }
    private List<Attribute> attributes;
    Entity(String name, List<Attribute> attributes) {
      this.attributes = attributes;
      this.name = name;
    }


    public String getName() {
      return name;
    }
  }

  private static String typeFormat(Object...orgs) {
    String typeSchemaFormat = "type <%s> {\n" +
          "%s\n" +
        "}\n";
    return String.format(typeSchemaFormat, orgs);
  }

  public static String generateEntityTypeSchema() {
    StringBuilder typeSchema = new StringBuilder();
    Entity[] entities = Entity.values();
    for (Entity entity : entities) {
       StringBuilder attrDefine = new StringBuilder();
       for (Attribute attribute : entity.getAttributes()) {
         attrDefine.append(attributeInTypeFormat(attribute.getName(), attribute.getType()));
       }
      typeSchema.append(typeFormat(entity.getName(), attrDefine.toString()));
    }
    return typeSchema.toString();
  }

  // 属性
  public enum Attribute {
    DGRAPH_TYPE("dgraph.type", "string"),
    ENTITY_TYPE("entity-type", "[string]"),
    NAME("name","string"),
    CODE("code","int"),
    MAJOR_CODE("major-code","string"),
    CORP_TYPE("corp-type","[string]"),
    CORP_ALIAS("corp-alias","[string]"),
    CORP_ENG_NAME("corp-eng-name","string"),
    CONSENSUS_TYPE("consensus-type","int"),
    CONSENSUS_MAX_SCORE("consensus-maxscore","int"),
    CONSENSUS_DESC("consensus-desc","string"),
    CONSENSUS_FACET("consensus-facet","int"),
    CONSENSUS_CLASS_NAME("consensus-classname","string"),
    LOC_CODE("loc-code","string"),
    CITY_TYPE("city-type","string"),
    LOC_CITY_CODE("loc-city-code","string"),
    SCHOOL_TYPE("school-type","[string]"),
    SCHOOL_CODE("school-code","string"),
    IND_CODE("ind-code","string"),
    CERT_CODE("cert-code","string"),
    ESTABLISH_DATE("establish_date", "string"),
    WEIGHT("weight", "float")
    ;

    private String name;
    private String type;

    Attribute(String name, String type) {
      this.name = name;
      this.type = type;
    }

    public String getName() {
      return name;
    }

    public String getType() {
      return type;
    }
  }

  private static String attributeFormat(Object... orgs) {
    String typeSchemaFormat = "%s:%s .\n";
    return String.format(typeSchemaFormat, orgs);
  }

  private static String attributeInTypeFormat(Object... orgs) {
    String typeSchemaFormat = "%s:%s \n";
    return String.format(typeSchemaFormat, orgs);
  }

  public static String generateEntityAttributeSchema() {

    StringBuilder typeSchema = new StringBuilder();
    Attribute[] attributes = Attribute.values();
    for (Attribute attribute : attributes) {
      // predicate dgraph.type is reserved and is not allowed to be modified
      if (attribute.getName().equals(Attribute.DGRAPH_TYPE.getName())) {
        continue;
      }
      typeSchema.append(attributeFormat(attribute.getName(), attribute.getType()));
    }
    return typeSchema.toString();
  }

  // 关系类型
  public enum RelType {
    ENTITY_REL("entity-rel"),
    COMPANY_CORP_TYPE("company-corp-type"),
    SCHOOL_SCHOOL_TYPE("school-school-type"),
    ENTITY_ENTITY_TYPE("entity-entity-type")
    ;
    private String name;
    RelType(String name) {
      this.name = name;
    }

    public String getName() {
      return name;
    }


    @Override
    public String toString() {
      return this.getName();
    }
  }


  // 关系
  public enum Relations {
    NONE("NONE"),
    SUPERIOR_INDUSTRY("superior-industry"),
    LOWER_INDUSTRY("lower-industry"),
    KEYWORD_INDUSTRY("keyword-industry"),
    CONFLICTWORD_INDUSTRY("conflictword-industry"),
    KEYWORD_CERT("keyword-cert"),
    SUPERIOR_CERT("superior-cert"),
    LOWER_CERT("lower-cert"),
    HIGHER_GOLD_CERT("highergold-cert"),
    LOWER_GOLD_CERT("lowergold-cert"),
    FUNC_INDUSTRY("func-industry"),
    INDUSTRY_FUNC("industry-func"),
    CONFLICTFUNC_FUNC("conflictfunc-func"),
    FUNC_CONFLICTFUNC("func-conflictfunc"),
    TOP_FUNC("top-func"),
    DOWN_FUNC("down-func"),
    CONFLICTWORD_JOB_RANK("conflictword-jobrank"),
    KEYWORD_JOB_RANK("keyword-jobrank"),
    INDUSTRY_JOB_TITLE("industry-jobtitle"),
    JOB_TITLE_INDUSTRY("jobtitle-industry"),
    KEYWORD_JOB_TITLE("keyword-jobtitle"),
    FUNC_JOB_TITLE("func-jobtitle"),
    JOB_TITLE_FUNC("jobtitle-func"),
    TOP_ORANGE_INDUSTRY("top-orangeindustry"),
    DOWN_ORANGE_INDUSTRY("down-orangeindustry"),
    KEYWORD_ORANGE_INDUSTRY("keyword-orangeindustry"),
    COMPANY_ORANGE_INDUSTRY("company-orangeindustry"),
    SUPERIOR_DIRECTION("superior-direction"),
    LOWER_DIRECTION("lower-direction"),
    KEYWORD_DIRECTION("keyword-direction"),
    FUNC_DIRECTION("func-direction"),
    DIRECTION_FUNC("direction-func"),
    SUPERIOR_SKILL("superior-skill"),
    LOWER_SKILL("lower-skill"),
    KEYWORD_SKILL("keyword-skill"),
    FUNC_SKILL("func-skill"),
    SKILL_FUNC("skill-func"),
    KEYWORD_ATTRIBUTE("keyword-attribute"),
    KEYWORD_SCHOOL("keyword-school"),
    KEYWORD_LOCATION("keyword-location"),
    IS_SYNONYM("is-synonym"),
    HIGHER_GOLD_COMPANY_TYPE("highergold-companytype"),
    LOWER_GOLD_COMPANY_TYPE("lowergold-companytype"),
    ORANGE_INDUSTRY_COMPANY("orangeindustry-company"),
    KEYWORD_COMPANY("keyword-company"),
    COMPANY_GROUP_COMPANY("companygroup-company"),
    COMPANY_COMPANY_GROUP("company-companygroup"),
    KEYWORD_TOPIC("keyword-topic"),
    MAJOR_RELATES_MAJOR("majorrelates-major"),
    IS_SIMILARITY("is-similarity"),
    KEYWORD_MAJOR("keyword-major"),
    MAJOR_DISCIPLINE_MAJOR("majordiscipline-major"),
    MAJOR_CATEGORY_MAJOR("majorcategory-major"),
    MAJOR_MAJOR_RELATES("major-majorrelates"),
    MAJOR_MAJOR_DISCIPLINE("major-majordiscipline"),
    MAJOR_MAJOR_CATEGORY("major-majorcategory"),
    // keyword 相关关系
    INDUSTRY_KEYWORD("industry-keyword"),
    INDUSTRY_CONFLICTWORD("industry-conflictword"),
    CERT_KEYWORD("cert-keyword"),
    JOB_RANK_CONFLICTWORD("jobrank-conflictword"),
    JOB_RANK_KEYWORD("jobrank-keyword"),
    JOB_TITLE_KEYWORD("jobtitle-keyword"),
    ORANGE_INDUSTRY_KEYWORD("orangeindustry-keyword"),
    DIRECTION_KEYWORD("direction-keyword"),
    SKILL_KEYWORD("skill-keyword"),
    ATTRIUTE_KEYWORD("attribute-keyword"),
    SCHOOL_KEYWORD("school-keyword"),
    LOCATION_KEYWORD("location-keyword"),
    COMPANY_KEYWORD("company-keyword"),
    TOPIC_KEYWORD("topic-keyword"),
    MAJOR_KEYWORD("major-keyword"),
    HAS_DEPARTMENT("has_dept"),
    DEPARTMENT_IN("dept_in"),
    COMPANY_CORPTYPE("company-corptype"),
    CORPTYPE_COMPANY("corptype-company"),
    ENTITY_ENTITY_TYPE("entity-entitytype"),
    ENTITY_TYPE_ENTITY("etype-entity"),
    SCHOOLTYPE_SCHOOL("schooltype-school"),
    SCHOOL_SCHOOLTYPE("school-schooltype")
    ;
    private String name;
    Relations(String name) {
      this.name = name;
    }

    public String getName() {
      return name;
    }
  }

  private static String relationFormat(Object... orgs) {
    String typeSchemaFormat =  "%s:[uid] . \n";
    return String.format(typeSchemaFormat, orgs);
  }
  public static String generateEntityRealtionsSchema() {

    StringBuilder typeSchema = new StringBuilder();
    Relations[] relations = Relations.values();
    for (Relations relation : relations) {
      typeSchema.append(relationFormat(relation.getName()));
    }
    return typeSchema.toString();
  }
}

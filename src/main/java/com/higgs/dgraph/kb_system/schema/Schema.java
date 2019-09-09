package com.higgs.dgraph.kb_system.schema;

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
    NONE("NONE"),
    ENTITY_TYPE_ENTITY("entity-type-entity"),
    SCHOOL_TYPE_ENTITY("school-type-entity"),
    ENTITY("entitytype-entity"),
    KEYWORD("关键词"),
    JOB_FUNCTION("职能"),
    DIRECTION("方向"),
    INDUSTRY("行业"),
    SKILL("技能"),
    TOPIC("主题"),
    CERTIFICATE("证书"),
    MAJOR("专业"),
    SCHOOL("学校"),
    COMPANY("公司"),
    MAJOR_CATEGORY("专业大类"),
    MAJOR_DISCIPLINE("专业学科"),
    LOCATION("城市"),
    KNOW_NOT_RECOGNIZE("已知未识别"),
    CONSENSUS("共识"),
    JOB_TITLE("职位"),
    JOB_RANK("职级"),
    ATTRIBUTE("属性值类型"),
    IT_ORANGE_INDUSTRY("IT桔子行业"),
    DEPARTMENT("部门")
    ;
    private String name;
    Entity(String name) {
      this.name = name;
    }

    public String getName() {
      return name;
    }
  }

  // 属性
  public enum Attribute {
    DGRAPH_TYPE("dgraph.type", "string"),
    ENTITY_TYPE("entity-type", "string"),
    NAME("name","string"),
    CODE("code","long"),
    MAJOR_CODE("major-code","string"),
    CORP_TYPE("corp-type","string"),
    CORP_ALIAS("corp-alias","string"),
    CORP_ENG_NAME("corp-eng-name","string"),
    CONSENSUS_TYPE("consensus-type","long"),
    CONSENSUS_MAX_SCORE("consensus-maxscore","long"),
    CONSENSUS_DESC("consensus-desc","string"),
    CONSENSUS_FACET("consensus-facet","long"),
    CONSENSUS_CLASS_NAME("consensus-classname","string"),
    LOC_CODE("loc-code","string"),
    CITY_TYPE("city-type","string"),
    LOC_CITY_CODE("loc-city-code","string"),
    SCHOOL_TYPE("school-type","string"),
    SCHOOL_CODE("school-code","string"),
    IND_CODE("ind-code","string"),
    CERT_CODE("cert-code","string"),
    ESTABLISH_DATE("establish_date", "string"),
    WEIGHT("weight", "double")
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

}

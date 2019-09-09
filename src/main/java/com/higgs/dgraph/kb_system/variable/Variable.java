package com.higgs.dgraph.kb_system.variable;

import com.higgs.dgraph.kb_system.schema.Schema;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
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
public class Variable {

  public static class RelationPair {
    public RelationPair(String inRel, String outRel) {
      this.inRel = inRel;
      this.outRel = outRel;
    }

    public String getInRel() {
      return inRel;
    }

    public void setInRel(String inRel) {
      this.inRel = inRel;
    }

    public String getOutRel() {
      return outRel;
    }

    public void setOutRel(String outRel) {
      this.outRel = outRel;
    }

    String inRel;
    String outRel;
  }

  public  static String GRAKN_ADDRESS = "172.20.0.8:48555";

  public  static String GRAKN_ADDRESS_KB = "172.20.0.9:48555";

  public  static String LOCAL_GRAKN_ADDRESS_KB = "127.0.0.1:48555";

  public  static String PHONE_CALL_KEY_SPACE = "phone_calls";

  public  static String KEY_SPACE = "kb1";

  public static List<String> entityTypeList = Arrays.asList(
      Schema.Entity.KEYWORD.getName(),
      Schema.Entity.JOB_FUNCTION.getName(),
      Schema.Entity.DIRECTION.getName(),
      Schema.Entity.INDUSTRY.getName(),
      Schema.Entity.SKILL.getName(),
      Schema.Entity.TOPIC.getName(),
      Schema.Entity.CERTIFICATE.getName(),
      Schema.Entity.MAJOR.getName(),
      Schema.Entity.SCHOOL.getName(),
      Schema.Entity.COMPANY.getName(),
      Schema.Entity.MAJOR_CATEGORY.getName(),
      Schema.Entity.MAJOR_DISCIPLINE.getName(),
      Schema.Entity.LOCATION.getName(),
      Schema.Entity.KNOW_NOT_RECOGNIZE.getName(),
      Schema.Entity.NONE.getName(),
      Schema.Entity.CONSENSUS.getName(),
      Schema.Entity.JOB_TITLE.getName(),
      Schema.Entity.JOB_RANK.getName(),
      Schema.Entity.ATTRIBUTE.getName(),
      Schema.Entity.IT_ORANGE_INDUSTRY.getName()
  );


  public static List<RelationPair> relationPairs = Arrays.asList(
      new RelationPair(Schema.Relations.NONE.getName(), Schema.Relations.NONE.getName
          ()),
      new RelationPair(Schema.Relations.IS_SYNONYM.getName(), Schema.Relations.IS_SYNONYM.getName
          ()),
      new RelationPair(Schema.Relations.NONE.getName(), Schema.Relations.NONE.getName
          ()),
      new RelationPair(Schema.Relations.NONE.getName(), Schema.Relations.NONE.getName
          ()),
      new RelationPair(Schema.Relations.LOWER_INDUSTRY.getName(), Schema.Relations.SUPERIOR_INDUSTRY.getName
          ()),
      new RelationPair(Schema.Relations.NONE.getName(), Schema.Relations.NONE.getName
          ()),
      new RelationPair(Schema.Relations.NONE.getName(), Schema.Relations.NONE.getName
          ()),
      new RelationPair(Schema.Relations.DIRECTION_KEYWORD.getName(), Schema.Relations.KEYWORD_DIRECTION.getName
          ()),
      new RelationPair(Schema.Relations.SKILL_KEYWORD.getName(), Schema.Relations.KEYWORD_SKILL.getName
          ()),
      new RelationPair(Schema.Relations.TOPIC_KEYWORD.getName(), Schema.Relations.KEYWORD_TOPIC.getName
          ()),
      new RelationPair(Schema.Relations.CONFLICTFUNC_FUNC.getName(), Schema.Relations.FUNC_CONFLICTFUNC.getName
          ()),
      new RelationPair(Schema.Relations.CERT_KEYWORD.getName(), Schema.Relations.KEYWORD_CERT.getName
          ()),
      new RelationPair(Schema.Relations.MAJOR_MAJOR_RELATES.getName(), Schema.Relations.MAJOR_RELATES_MAJOR
          .getName
          ()),
      new RelationPair(Schema.Relations.SCHOOL_KEYWORD.getName(), Schema.Relations.KEYWORD_SCHOOL.getName
          ()),
      new RelationPair(Schema.Relations.COMPANY_KEYWORD.getName(), Schema.Relations.KEYWORD_COMPANY.getName
          ()),
      new RelationPair(Schema.Relations.MAJOR_CATEGORY_MAJOR.getName(), Schema.Relations.MAJOR_MAJOR_CATEGORY.getName
          ()),
      new RelationPair(Schema.Relations.MAJOR_DISCIPLINE_MAJOR.getName(), Schema.Relations.MAJOR_MAJOR_DISCIPLINE
          .getName
          ()),
      new RelationPair(Schema.Relations.IS_SIMILARITY.getName(), Schema.Relations.IS_SIMILARITY.getName
          ()),
      new RelationPair(Schema.Relations.INDUSTRY_KEYWORD.getName(), Schema.Relations.KEYWORD_INDUSTRY.getName
          ()),
      new RelationPair(Schema.Relations.INDUSTRY_CONFLICTWORD.getName(), Schema.Relations.CONFLICTWORD_INDUSTRY
          .getName
          ()),
      new RelationPair(Schema.Relations.LOCATION_KEYWORD.getName(), Schema.Relations.KEYWORD_LOCATION.getName
          ()),
      new RelationPair(Schema.Relations.LOWER_GOLD_CERT.getName(), Schema.Relations.HIGHER_GOLD_CERT.getName
          ()),
      new RelationPair(Schema.Relations.COMPANY_GROUP_COMPANY.getName(), Schema.Relations.COMPANY_COMPANY_GROUP
          .getName
          ()),
      new RelationPair(Schema.Relations.NONE.getName(), Schema.Relations.NONE.getName
          ()),
      new RelationPair(Schema.Relations.NONE.getName(), Schema.Relations.NONE.getName
          ()),
      new RelationPair(Schema.Relations.JOB_TITLE_KEYWORD.getName(), Schema.Relations.KEYWORD_JOB_TITLE.getName
          ()),
      new RelationPair(Schema.Relations.FUNC_JOB_TITLE.getName(), Schema.Relations.JOB_TITLE_FUNC.getName
          ()),
      new RelationPair(Schema.Relations.DOWN_FUNC.getName(), Schema.Relations.TOP_FUNC.getName
          ()),
      new RelationPair(Schema.Relations.INDUSTRY_FUNC.getName(), Schema.Relations.FUNC_INDUSTRY.getName
          ()),
      new RelationPair(Schema.Relations.INDUSTRY_JOB_TITLE.getName(), Schema.Relations.JOB_TITLE_INDUSTRY.getName
          ()),
      new RelationPair(Schema.Relations.JOB_RANK_KEYWORD.getName(), Schema.Relations.KEYWORD_JOB_RANK
          .getName
          ()),
      new RelationPair(Schema.Relations.JOB_RANK_CONFLICTWORD.getName(), Schema.Relations.CONFLICTWORD_JOB_RANK
          .getName
          ()),
      new RelationPair(Schema.Relations.DOWN_ORANGE_INDUSTRY.getName(), Schema.Relations.TOP_ORANGE_INDUSTRY.getName
          ()),
      new RelationPair(Schema.Relations.ORANGE_INDUSTRY_COMPANY.getName(), Schema.Relations.COMPANY_ORANGE_INDUSTRY
          .getName
          ()),
      new RelationPair(Schema.Relations.ORANGE_INDUSTRY_KEYWORD.getName(), Schema.Relations.KEYWORD_ORANGE_INDUSTRY
          .getName()),
      new RelationPair(Schema.Relations.FUNC_DIRECTION.getName(), Schema.Relations.DIRECTION_FUNC.getName
          ()),
      new RelationPair(Schema.Relations.FUNC_SKILL.getName(), Schema.Relations.SKILL_FUNC.getName
          ()),
      new RelationPair(Schema.Relations.LOWER_DIRECTION.getName(), Schema.Relations.SUPERIOR_DIRECTION.getName
          ()),
      new RelationPair(Schema.Relations.LOWER_SKILL.getName(), Schema.Relations.SUPERIOR_SKILL.getName
          ()),
      new RelationPair(Schema.Relations.LOWER_CERT.getName(), Schema.Relations.SUPERIOR_CERT.getName
          ()),
      new RelationPair(Schema.Relations.LOWER_GOLD_COMPANY_TYPE.getName(), Schema.Relations.HIGHER_GOLD_COMPANY_TYPE
          .getName
          ()),
      new RelationPair(Schema.Relations.MAJOR_KEYWORD.getName(), Schema.Relations.KEYWORD_MAJOR.getName
          ()),
      new RelationPair(Schema.Relations.ATTRIUTE_KEYWORD.getName(), Schema.Relations.KEYWORD_ATTRIBUTE.getName
          ())
  );

  public static String dirFormat(String dataDir, boolean isDir) {
    if (!dataDir.endsWith("/") && isDir) {
      dataDir = dataDir + "/";
    }
    return dataDir;
  }

  public static String dirFormat(String dataDir) {
    if (!dataDir.endsWith("/")) {
      dataDir = dataDir + "/";
    }
    return dataDir;
  }

  public static String getVarValue(String type ,String key)  {
    MessageDigest md = null;
    String var = type + "-" + getMD5(key);
    return var;
  }

  public static String getRelVarValue(String relType ,String... key)  {
    MessageDigest md = null;
    String var = relType;
    List<String> keys = Arrays.asList(key);
    for (int i = 0; i < keys.size();i ++) {
      var = var + "-" + getMD5(keys.get(i));
    }
    return var;
  }

  public static String getMD5(String key)  {
    MessageDigest md = null;
    try {
      md = MessageDigest.getInstance("MD5");
    } catch (NoSuchAlgorithmException e) {
      e.printStackTrace();
    }
    try {
      md.update(key.getBytes("UTF-8"));
    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
    }
    String md5 = new BigInteger(1, md.digest()).toString(16);
    return md5;
  }

}

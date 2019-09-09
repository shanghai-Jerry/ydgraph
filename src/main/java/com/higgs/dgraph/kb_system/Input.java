package com.higgs.dgraph.kb_system;

import java.util.List;

import io.dgraph.DgraphProto;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

/**
 * User: JerryYou
 *
 * Date: 2019-09-05
 *
 * Copyright (c) 2018 devops
 *
 * <<licensetext>>
 */
public abstract class Input {

  private static final Logger logger = LoggerFactory.getLogger(Input.class);

  String path;

  String attributeType;

  public Input(String path) {
    this.path = path;
  }

  public String getDataPath() {
    return path;
  }

  // transform data to Graql
  public abstract List<DgraphProto.NQuad> template(JsonObject data);
  // according data format, transform data to standard json format
  public abstract List<JsonObject> parseDataToJson();

  /**
   * must be uid format
   * @param src
   * @param pred
   * @param value
   * @return
   */
  public DgraphProto.NQuad relationFormat(String src, String pred, String value) {
    DgraphProto.NQuad.Builder builder = DgraphProto.NQuad.newBuilder();
    // logger.info("src:" + src + ",pred:" + pred + ",value:" + value);
    builder.setSubject(src).setPredicate(pred).setObjectId(value);
    return  builder.build();
  }

  public DgraphProto.NQuad attrFormat(String src, String pred, Object value, boolean isExistUid) {
    // logger.info("uniqueId:" + uniqueId+ ", pred:" + pred + ", valueObject:" + value);
    DgraphProto.NQuad.Builder builder = DgraphProto.NQuad.newBuilder();
    if (value instanceof Integer || value instanceof Long) {
      builder.setObjectValue(DgraphProto.Value.newBuilder().setIntVal(Long.valueOf(value
          .toString())).build());
    } else if (value instanceof String) {
      builder.setObjectValue(DgraphProto.Value.newBuilder().setStrVal((String) value).build());
    } else if (value instanceof Double || value instanceof Float) {
      builder.setObjectValue(DgraphProto.Value.newBuilder().setDoubleVal(Double.valueOf(value
          .toString())).build());
    } else if (value instanceof Boolean) {
      builder.setObjectValue(DgraphProto.Value.newBuilder().setBoolVal((Boolean) value).build
          ());
    } else if (value == null) {
      // field 没有设置属性的过滤
      logger.info("value is null");
      return builder.build();
    } else {
      logger.info("value is not match");
      return builder.build();
    }
    if (isExistUid) {
      builder.setSubject(src).setPredicate(pred);
    } else {
      builder.setSubject(String.format("_:%s", src)).setPredicate(pred);
    }
    return builder.build();
  }

}

package com.higgs.dgraph.kb_system;

import com.higgs.dgraph.kb_system.schema.Schema;
import com.higgs.dgraph.kb_system.variable.Variable;

import org.apache.commons.collections.map.HashedMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import io.dgraph.DgraphProto;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

/**
 * User: JerryYou
 *
 * Date: 2019-08-29
 *
 * Copyright (c) 2018 devops
 *
 * <<licensetext>>
 */
public class AttributeInput extends Input {

  private static final Logger logger = LoggerFactory.getLogger(AttributeInput.class);

  public AttributeInput(String path) {
    super(path);
  }

  Map<String, String> uids = new HashedMap();

  public AttributeInput(String path, String attributeType,Map<String, String> uids) {
    super(path);
    this.attributeType = attributeType;
    this.uids = uids;
  }

  @Override
  public List<DgraphProto.NQuad> template(JsonObject data) {
    String name = data.getString("name");
    String valueString = data.getString("attribute_value");
    String [] values = valueString.split(",");
    String var = Variable.getVarValue(Schema.Entity.ENTITY.getName(), name);
    String uid = uids.getOrDefault(var, "");
    List<DgraphProto.NQuad> squads = new ArrayList<>();
    if (uid.isEmpty()) {
      logger.info("attrFormat get uid error");
      return squads;
    }
    for (String value : values) {
      squads.add(this.attrFormat(uid, this.attributeType, value, true));
    }
    return squads;
  }

  @Override
  public List<JsonObject> parseDataToJson() {
    List<JsonObject> items = new ArrayList<>();
    KbParseData.parseAttribute(items, this.getDataPath());
    return items;
  }
}

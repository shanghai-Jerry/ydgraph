package com.higgs.dgraph.kb_system;

import com.higgs.dgraph.kb_system.schema.Schema;
import com.higgs.dgraph.kb_system.variable.Variable;

import java.util.ArrayList;
import java.util.List;

import io.dgraph.DgraphProto;
import io.vertx.core.json.JsonObject;


/**
 * User: JerryYou
 *
 * Date: 2019-08-30
 *
 * Copyright (c) 2018 devops
 *
 * <<licensetext>>
 */
public class EntityInput extends Input {

  String inEntity;

  public EntityInput(String path, String inEntity) {
    super(path);
    this.inEntity = inEntity;
  }
  @Override
  public List<DgraphProto.NQuad> template(JsonObject data) {
    String name = data.getString("name");
    long code = data.getLong("id");
    String var = Variable.getVarValue(this.inEntity, name);
    List<DgraphProto.NQuad> squads = new ArrayList<>();
    squads.add(attrFormat(var, Schema.Attribute.NAME.getName(), name, false));
    squads.add(attrFormat(var, Schema.Attribute.CODE.getName(), code, false));
    // 添加dgraph.type
    squads.add(attrFormat(var, Schema.Attribute.DGRAPH_TYPE.getName(), this.inEntity, false));
    return squads;
  }

  @Override
  public List<JsonObject> parseDataToJson() {
    List<JsonObject> items = new ArrayList<>();
    KbParseData.parseEntity(items, "", this.getDataPath());
    return items;
  }
}

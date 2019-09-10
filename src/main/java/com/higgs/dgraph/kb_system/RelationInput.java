package com.higgs.dgraph.kb_system;


import com.google.protobuf.ByteString;

import com.higgs.dgraph.kb_system.schema.Schema;
import com.higgs.dgraph.kb_system.variable.Variable;

import org.apache.commons.collections.map.HashedMap;
import org.apache.hadoop.hbase.util.Bytes;

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
public class RelationInput extends Input {

  String relType;

  Map<String, String> uids = new HashedMap();

  private static final Logger logger = LoggerFactory.getLogger(RelationInput.class);

  public RelationInput(String path) {
    super(path);
  }

  public RelationInput(String path,String relType, Map<String, String> uids) {
    super(path);
    this.relType = relType;
    this.uids = uids;
  }
  @Override
  public List<DgraphProto.NQuad> template(JsonObject data) {
    String in_value = data.getString("in_value");
    String out_value = data.getString("out_value");
    double weight = data.getDouble("weight");
    int rel_type = data.getInteger("rel_type", 0);
    List<DgraphProto.NQuad> squads = new ArrayList<>();
    String inVar;
    String outVar;
    /* all entity is just entity, no other type for generating key
    if (rel_type == 40 || rel_type == 42) {
      outVar = Variable.getVarValue(Schema.Entity.CORP_TYPE_ENTITY.getName(), out_value);
      inVar = Variable.getVarValue(Schema.Entity.CORP_TYPE_ENTITY.getName(), in_value);
      if (rel_type == 42) {
        inVar = Variable.getVarValue(Schema.Entity.ENTITY.getName(), in_value);
      }
    } else {
      inVar = Variable.getVarValue(Schema.Entity.ENTITY.getName(), in_value);
      outVar = Variable.getVarValue(Schema.Entity.ENTITY.getName(), out_value);
    }*/
    inVar = Variable.getVarValue(Schema.Entity.ENTITY.getName(), in_value);
    outVar = Variable.getVarValue(Schema.Entity.ENTITY.getName(), out_value);
    String inUid = uids.getOrDefault(inVar, "");
    String outUid = uids.getOrDefault(outVar, "");
    if (this.relType.isEmpty()) {
      this.relType = Variable.relationPairs.get(rel_type).getOutRel();
    }
    if (inUid.isEmpty() || outUid.isEmpty()) {
      logger.info("relationFormat get uid error:" + inVar + ":" + in_value + "," + outVar + ":" + out_value);
      return squads;
    }

    DgraphProto.NQuad squad = this.relationFormat(inUid, this.relType, outUid);
    squads.add(squad.toBuilder().addFacets(
        DgraphProto.Facet.newBuilder().setKey(Schema.Attribute.WEIGHT.getName())
            .setValTypeValue(DgraphProto.Facet.ValType.FLOAT_VALUE)
            .setValue(ByteString.copyFrom(Bytes.toBytes(weight)))
            .build())
        .build());
    return squads;

  }

  @Override
  public List<JsonObject> parseDataToJson() {
    List<JsonObject> items = new ArrayList<>();
    KbParseData.parseRelations(items,this.getDataPath());
    return items;
  }
}

package com.higgs.dgraph.kb_system;

import com.higgs.dgraph.Config;
import com.higgs.dgraph.kb_system.schema.Schema;
import com.higgs.dgraph.kb_system.variable.Variable;
import com.higgs.utils.FileUtils;
import com.higgs.utils.TimeUtil;

import org.apache.commons.collections.map.HashedMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import io.dgraph.DgraphClient;
import io.dgraph.DgraphProto;
import io.dgraph.Transaction;
import io.vertx.core.json.JsonObject;

/**
 * User: JerryYou
 *
 * Date: 2019-09-05
 *
 * Copyright (c) 2018 devops
 *
 * <<licensetext>>
 */
public class LoadMain {

  Map<String, String> uids = new HashedMap();

  DgraphClient dClient;

  public LoadMain(DgraphClient dClient) {
    this.dClient = dClient;
  }

  static Logger logger = LoggerFactory.getLogger(KbParseData.class);

  public void alterSchema(String schema) {
    logger.info("alter kb schema ... ");
    DgraphProto.Operation op = DgraphProto.Operation.newBuilder()
        .setSchema(schema).build();
    this.dClient.alter(op);
  }

  /**
   * 删除schema
   */
  public void dropSchema() {
    // Initialize
    logger.info("drop kb schema ... ");
    this.dClient.alter(DgraphProto.Operation.newBuilder()
        .setDropAll(true)
        .build());
  }

  public void connectAndMigrate(Collection<Input> inputs) {
    for (Input input : inputs) {
      long startTime = System.currentTimeMillis();
      logger.info("Insert Loading from [" + input.getDataPath() + "] into dgraph, started_at " +
          new Date().toString());
      long total = loadDataIntoDgraph(input, 10000);
      long endTime = System.currentTimeMillis();
      long cost = (endTime - startTime) / 1000;
      logger.info("Inserted [ " + total +" ] items from [ " + input.getDataPath() + "] into " +
          "dgraph, ended_at " + new Date().toString() + ",cost:" + TimeUtil.costTime(cost) + "\n");
    }
  }

  public long loadDataIntoDgraph(Input input, int batchSize) {
    List<JsonObject> data = input.parseDataToJson();
    List<DgraphProto.NQuad> batchNquads = new ArrayList<>();
    int count = 0;
    int finished = 0;
    Transaction transaction = this.dClient.newTransaction();

    for (JsonObject item : data) {
      count++;
      finished++;
      List<DgraphProto.NQuad> nQuads = input.template(item);
      batchNquads.addAll(nQuads);
      if (count % batchSize == 0) {
        try {
          transaction = this.dClient.newTransaction();
          DgraphProto.Mutation mutation = DgraphProto.Mutation.newBuilder().addAllSet(batchNquads)
              .build();
          DgraphProto.Response assigned = transaction.mutate(mutation);
          transaction.commit();
          if (assigned != null && input instanceof  EntityInput) {
            Map<String, String> tmpuid = assigned.getUidsMap();
            uids.putAll(tmpuid);
          }
          logger.info(Thread.currentThread().getName()+" - Insert Executing: " +
              finished + "/" + data.size());
          count = 0;
          batchNquads.clear();
        } catch (Exception e) {
          logger.info("[ Batch Insert mutation error] => " + e.getMessage());
        } finally {

        }
      }

    }
    if (count > 0) {
      try {
        // commit when last item finished
        transaction = this.dClient.newTransaction();
        DgraphProto.Mutation mutation = DgraphProto.Mutation.newBuilder().addAllSet(batchNquads)
            .build();
        DgraphProto.Response assigned = transaction.mutate(mutation);
        transaction.commit();
        if (assigned != null && input instanceof EntityInput) {
          Map<String, String> tmpuid = assigned.getUidsMap();
          uids.putAll(tmpuid);
        }
        logger.info(Thread.currentThread().getName()+" - total Insert Executing: " +
            finished + "/" + data.size());
        batchNquads.clear();
      } catch (Exception e) {
        logger.info("[ Final Insert mutation error] => " + e.getMessage());
      } finally {
        if (transaction != null) {
          transaction.discard();
        }
      }
    }
    return data.size();
  }

  public static void prepare_data() {
    logger.info("prepare data ....");
    EntityTypeAttributeFormat entityTypeAttributeFormat = new EntityTypeAttributeFormat();
    entityTypeAttributeFormat.convertEntityType(
        "/Users/devops/workspace/kb/kb_system/entity-type.csv",
        "/Users/devops/workspace/kb/kb_system/entity-type-format.csv");
    entityTypeAttributeFormat.convertRelation(
        "/Users/devops/workspace/kb/kb_system",
        "kb_relation_1567074526188.txt");
    entityTypeAttributeFormat.entityFilterCorpType(
        "/Users/devops/workspace/kb/kb_system/kb_entity.csv",
        "/Users/devops/workspace/kb/kb_system/kb_entity_corp_type.csv",
        "/Users/devops/workspace/kb/kb_system/kb_entity_filter.csv");
  }

  public static void main(String[] args) {

    String dir = Variable.dirFormat("/Users/devops/workspace/kb/kb_system", true);

    DgraphClient dClient = Variable.createDgraphClient();

    LoadMain loadMain = new LoadMain(dClient);

    // 数据预处理
    prepare_data();

    loadMain.dropSchema();

    String schema = Config.getKbSchema();

    // logger.info("Schema:\n" + schema);
    loadMain.alterSchema(schema);

    Collection<Input> inputs = new ArrayList<>();

    // 所有实体入库
    List<EntityInput> entityInputs = Arrays.asList(
        new EntityInput(dir + "kb_entity_filter.csv",
            Schema.Entity.ENTITY.getName()),
        new EntityInput(dir + "kb_entity_entity_type.csv",
            Schema.Entity.ENTITY.getName()),
        new EntityInput(dir + "kb_entity_school_type.csv",
            Schema.Entity.ENTITY.getName()),
        new EntityInput(dir + "kb_entity_corp_type.csv",
          Schema.Entity.ENTITY.getName())
    );

    inputs.addAll(entityInputs);

    /**
     * 先入库所有实体，获取uid
     * 后面再入属性和关系
     */
    loadMain.connectAndMigrate(inputs);

    logger.info("all entity finished, uids:" + loadMain.uids.size());

    inputs.clear();

    // 所有实体的相关属性
    List<Input> attributeInputs = Arrays.asList(
        new AttributeInput(dir + "corp_type.csv", Schema.Attribute.CORP_TYPE.getName(), loadMain.uids),
        new AttributeInput(dir + "cert_code.csv", Schema.Attribute.CERT_CODE.getName(), loadMain.uids),
        new AttributeInput(dir + "city_type.csv", Schema.Attribute.CITY_TYPE.getName(), loadMain.uids),
        new AttributeInput(dir + "corp_alias.csv", Schema.Attribute.CORP_ALIAS.getName(), loadMain.uids),
        new AttributeInput(dir + "corp_eng_name.csv", Schema.Attribute.CORP_ENG_NAME.getName(), loadMain.uids),
        new AttributeInput(dir + "ind_code.csv", Schema.Attribute.IND_CODE.getName(), loadMain.uids),
        new AttributeInput(dir + "loc_city_code.csv", Schema.Attribute.LOC_CITY_CODE.getName(), loadMain.uids),
        new AttributeInput(dir + "loc_code.csv", Schema.Attribute.LOC_CODE.getName(), loadMain.uids),
        new AttributeInput(dir + "major_code.csv", Schema.Attribute.MAJOR_CODE.getName(), loadMain.uids),
        new AttributeInput(dir + "school_code.csv", Schema.Attribute.SCHOOL_CODE.getName(), loadMain.uids),
        // use DGRAPH_TYPE for labeled a type
        new AttributeInput(dir + "school_type.csv", Schema.Attribute.SCHOOL_TYPE.getName(), loadMain.uids),
        new AttributeInput(dir + "entity-type-format.csv", Schema.Attribute.ENTITY_TYPE.getName(), loadMain
            .uids)
    );
    inputs.addAll(attributeInputs);

    // 公司 - 公司类型之间的关系
    inputs.add(new Input(dir + "corp_type.csv") {
      @Override
      public List<DgraphProto.NQuad> template(JsonObject data) {
        String in_value = data.getString("in_value");
        String out_value = data.getString("out_value");
        String in = data.getString("in");
        String out = data.getString("out");
        String inVar = Variable.getVarValue(Schema.Entity.ENTITY.getName(), in_value);
        String outVar = Variable.getVarValue(Schema.Entity.ENTITY.getName(), out_value);
        String inUid = loadMain.uids.getOrDefault(inVar,"");
        String outUid = loadMain.uids.getOrDefault(outVar, "");
        List<DgraphProto.NQuad> squads = new ArrayList<>();
        if (inUid.isEmpty() || outUid.isEmpty()) {
          logger.info("relationFormat get uid error:" + inVar + ":" + in_value + ","+outVar + ":"+
              out_value);
          return squads;
        }
        squads.add(this.relationFormat(inUid, out, outUid));
        return squads;
      }

      @Override
      public List<JsonObject> parseDataToJson() {
        List<JsonObject> items = new ArrayList<>();
        KbParseData.parseRelationsInAttribute(items, "",
            Schema.Relations.COMPANY_CORPTYPE.getName(),this.getDataPath());
        return items;
      }
    });

    // 学校 - 学校类型之间的关系
    inputs.add(new Input(dir + "school_type.csv") {
      @Override
      public List<DgraphProto.NQuad> template(JsonObject data) {
        String in_value = data.getString("in_value");
        String out_value = data.getString("out_value");
        String in = data.getString("in");
        String out = data.getString("out");
        String inVar = Variable.getVarValue(Schema.Entity.ENTITY.getName(), in_value);
        String outVar = Variable.getVarValue(Schema.Entity.ENTITY.getName(), out_value);
        String inUid = loadMain.uids.getOrDefault(inVar,"");
        String outUid = loadMain.uids.getOrDefault(outVar, "");
        List<DgraphProto.NQuad> squads = new ArrayList<>();
        if (inUid.isEmpty() || outUid.isEmpty()) {
          logger.info("relationFormat get uid error:" +inVar + ":" + in_value + ","+outVar + ":"+
              out_value);
          return squads;
        }
        squads.add(this.relationFormat(inUid, out, outUid));
        return squads;
      }

      @Override
      public List<JsonObject> parseDataToJson() {
        List<JsonObject> items = new ArrayList<>();
        KbParseData.parseRelationsInAttribute(items, "",
            Schema.Relations.SCHOOL_SCHOOLTYPE.getName(),this.getDataPath());
        return items;
      }
    });

    // 实体 - 实体类型型之间的关系
    inputs.add(new Input(dir + "entity-type-format.csv") {
      @Override
      public List<DgraphProto.NQuad> template(JsonObject data) {
        String in_value = data.getString("in_value");
        String out_value = data.getString("out_value");
        String in = data.getString("in");
        String out = data.getString("out");
        String inVar = Variable.getVarValue(Schema.Entity.ENTITY.getName(), in_value);
        String outVar = Variable.getVarValue(Schema.Entity.ENTITY.getName(), out_value);
        String inUid = loadMain.uids.getOrDefault(inVar,"");
        String outUid = loadMain.uids.getOrDefault(outVar, "");
        List<DgraphProto.NQuad> squads = new ArrayList<>();
        if (inUid.isEmpty() || outUid.isEmpty()) {
          logger.info("relationFormat get uid error:" +inVar + ":" + in_value + ","+outVar + ":"+
              out_value);
          return squads;
        }
        squads.add(this.relationFormat(inUid, out, outUid));
        // 添加dgraph.type属性
        // 添加所有实体的实体类型为：dgraph.type
        squads.add(this.attrFormat(inUid, Schema.Attribute.DGRAPH_TYPE.getName(), out_value, true));
        return squads;
      }

      @Override
      public List<JsonObject> parseDataToJson() {
        List<JsonObject> items = new ArrayList<>();
        KbParseData.parseRelationsInAttribute(items, "",
            Schema.Relations.ENTITY_ENTITY_TYPE.getName(),this.getDataPath());
        return items;
      }
    });

    // 其他的关系
    List<RelationInput> relationInputs = new ArrayList<>();

    relationInputs.add(new RelationInput(dir + "relation_format.csv",
          "", loadMain.uids));

    inputs.addAll(relationInputs);

    loadMain.connectAndMigrate(inputs);

    // 保存uid
    FileUtils.saveFiles(dir + "unique_id_2_uid.txt", loadMain.uids);

    // 更新schema index
    loadMain.alterSchema(Config.updateSchema);

    logger.info("main Insert finished!!");
  }
}

package com.higgs.dgraph.kb_system;

import com.higgs.dgraph.kb_system.schema.Schema;
import com.higgs.dgraph.kb_system.variable.Variable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import io.dgraph.DgraphClient;
import io.dgraph.DgraphGrpc;
import io.dgraph.DgraphProto;
import io.dgraph.Transaction;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
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
public class LoadMainTest {

  DgraphClient dClient;

  public LoadMain getLoadMain() {
    return loadMain;
  }

  public void setLoadMain(LoadMain loadMain) {
    this.loadMain = loadMain;
  }

  LoadMain loadMain;

  public LoadMainTest(DgraphClient dClient) {
    this.dClient = dClient;
    loadMain = new LoadMain(dClient);
  }

  static Logger logger = LoggerFactory.getLogger(KbParseData.class);

  public void getEntityUniqueKey(String type,String keyName) {
    String key = Variable.getVarValue(type, keyName);
    logger.info("get:" + key);
  }

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
  public void mutateTest() {
    DgraphProto.NQuad.Builder builder = DgraphProto.NQuad.newBuilder();
    DgraphProto.NQuad squad =  builder.setSubject("0x120796").setPredicate("company-corptype")
        .setObjectId("0x120eb6")
        .build();
    Transaction transaction = this.dClient.newTransaction();
    try {
      DgraphProto.Mutation mutation = DgraphProto.Mutation.newBuilder().addSet(squad).build();
      DgraphProto.Response assigned = transaction.mutate(mutation);
      transaction.commit();
    } catch (Exception e) {
      logger.info("[Insert mutation error] => " + e.getMessage());
    } finally {
      transaction.close();
    }


  }

  private static DgraphClient createDgraphClient() {
    ManagedChannel channel =
        ManagedChannelBuilder.forAddress("172.20.0.9", 9080).usePlaintext(true).build();
    DgraphGrpc.DgraphStub stub = DgraphGrpc.newStub(channel);

    return new DgraphClient(stub);
  }

  public static void main(String[] args) {

    String dir = Variable.dirFormat("/Users/devops/workspace/kb/kb_system", true);

    DgraphClient dClient = createDgraphClient();

    LoadMainTest loadMainTest = new LoadMainTest(dClient);

    Collection<Input> inputs = new ArrayList<>();

    // 所有实体入库
    List<EntityInput> entityInputs = Arrays.asList(
        new EntityInput(dir + "kb_entity.csv", Schema.Entity.ENTITY.getName()),
        new EntityInput(dir + "kb_entity_entity_type.csv",
            Schema.Entity.ENTITY_TYPE_ENTITY.getName()),
        new EntityInput(dir + "kb_entity_school_type.csv",
            Schema.Entity.SCHOOL_TYPE_ENTITY.getName())
    );

    inputs.addAll(entityInputs);

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
        String inUid = loadMainTest.getLoadMain().uids.getOrDefault(inVar,"");
        String outUid = loadMainTest.getLoadMain().uids.getOrDefault(outVar, "");
        List<DgraphProto.NQuad> squads = new ArrayList<>();
        if (inUid.isEmpty() || outUid.isEmpty()) {
          logger.info("relationFormat get uid error");
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
     /*
     loadMainTest.dropSchema();
     loadMainTest.alterSchema(Config.kb_schema);
     loadMainTest.getLoadMain().connectAndMigrate(inputs);
     FileUtils.saveFiles(dir + "unique_id_2_uid.txt", loadMainTest.getLoadMain().uids);
     */
     // loadMainTest.mutateTest();
    // loadMainTest.alterSchema(Config.updateSchema);
    loadMainTest.getEntityUniqueKey(Schema.Entity.ENTITY.getName(), "java");

    logger.info("main Insert finished!!");
  }
}

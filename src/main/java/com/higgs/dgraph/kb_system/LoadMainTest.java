package com.higgs.dgraph.kb_system;

import com.higgs.dgraph.Config;
import com.higgs.dgraph.kb_system.schema.Schema;
import com.higgs.dgraph.kb_system.variable.Variable;
import com.higgs.utils.Util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.dgraph.DgraphClient;
import io.dgraph.DgraphProto;
import io.dgraph.Transaction;

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

  // 通过查询，来修改数据
  public void QueryMutation(String par) {
    String query = "";
    try {
      query = new String(Files.readAllBytes(Paths.get
          ("src/main/resources/kb_system/query/entity_isexist.query")));

    } catch (IOException e) {
      e.printStackTrace();
    }
    query = String.format(query, par);
    System.out.println("querying ....\n" + query);
    List<DgraphProto.NQuad> nQuads = new ArrayList<>();
    nQuads.add(
        DgraphProto.NQuad.newBuilder().setSubject("uid(entity)")
            .setPredicate(Schema.Attribute.CERT_CODE.getName())
            .setObjectValue(DgraphProto.Value.newBuilder().setStrVal("1001"))
            .build()
    );
    nQuads.add(
        DgraphProto.NQuad.newBuilder().setSubject("uid(entity)")
            .setPredicate(Schema.Attribute.ENTITY_TYPE.getName())
            .setObjectValue(DgraphProto.Value.newBuilder().setStrVal("公司"))
            .build()
    );

    DgraphProto.Mutation mutation =
        DgraphProto.Mutation.newBuilder()
            .addAllSet(nQuads)
            .build();
    DgraphProto.Request request = DgraphProto.Request.newBuilder()
        .setQuery(query)
        .addMutations(mutation)
        .setCommitNow(true)
        .build();
    Transaction transaction = this.dClient.newTransaction();
    DgraphProto.Response res = transaction.doRequest(request);
    Util.println("res", res.getUidsCount());

  }

  public void query(String queryKey) {
    // Query
    String query = "";
    try {
      query = new String(Files.readAllBytes(Paths.get
          ("src/main/resources/kb_system/test.query")));

    } catch (IOException e) {
      e.printStackTrace();
    }
    System.out.println("querying ....\n" + query);
    Map<String, String> vars = new HashMap<>();
    vars.put("$a", queryKey);
    DgraphProto.Response res = dClient.newTransaction()
        .query(String.format(query, queryKey))
        // .queryWithVars(query, vars)
        ;
    // 获取时间
    // res.getLatency()
    Util.formatPrintJson(res.getJson().toStringUtf8());
    Util.parseLatency(res);
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

  public static void main(String[] args) {

    String dir = Variable.dirFormat("/Users/devops/workspace/kb/kb_system", true);

    DgraphClient dClient = Variable.createDgraphClient();

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

    // inputs.addAll(entityInputs);

     /**
     loadMainTest.dropSchema();
     loadMainTest.alterSchema(Config.kb_schema);
     loadMainTest.getLoadMain().connectAndMigrate(inputs);
     FileUtils.saveFiles(dir + "unique_id_2_uid.txt", loadMainTest.getLoadMain().uids);
     */
     // loadMainTest.mutateTest();

    String schema = Config.getKbSchema();

    logger.info("Schema:\n" + schema);

    loadMainTest.getEntityUniqueKey(Schema.Entity.ENTITY.getName(), "java");

    loadMainTest.query("网络通信");

    // loadMainTest.QueryMutation("java");

    // loadMainTest.alterSchema(Config.update_schema_type);

    logger.info("main Insert finished!!");
  }
}

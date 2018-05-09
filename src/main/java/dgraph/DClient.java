package dgraph;

import com.google.gson.Gson;
import com.google.protobuf.ByteString;
import com.google.protobuf.util.JsonFormat;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import client.dgrpah.DgraphClient;
import dgraph.node.EntityNode;
import dgraph.node.NodeUtil;
import dgraph.node.People;
import dgraph.node.Person;
import dgraph.put.Nodeput;
import io.dgraph.DgraphGrpc;
import io.dgraph.DgraphProto;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

/**
 * User: JerryYou
 *
 * Date: 2018-05-08
 *
 * Copyright (c) 2018 devops
 *
 * <<licensetext>>
 */
public class DClient {

  private static final Logger logger = LoggerFactory.getLogger(DClient.class);

  private static JsonFormat.Parser parser = JsonFormat.parser();

  public DgraphClient getDgraphClient() {
    return dgraphClient;
  }

  private DgraphClient dgraphClient;

  public DClient(List<String> adressList) {
    List<DgraphGrpc.DgraphBlockingStub> clients = new ArrayList<>();
    for (String address : adressList) {
      String[] hosts = address.split(":");
      ManagedChannel channel =
          ManagedChannelBuilder.forAddress(hosts[0], Integer.parseInt(hosts[1])).usePlaintext(true)
              .build();
      DgraphGrpc.DgraphBlockingStub blockingStub = DgraphGrpc.newBlockingStub(channel);
      clients.add(blockingStub);
    }
    dgraphClient = new DgraphClient(clients);
  }

  public void dropSchema() {
    // Initialize
    dgraphClient.alter(DgraphProto.Operation.newBuilder()
        .setDropAll(true)
        .build());
  }

  public void alterSchema(String schema) {
    DgraphProto.Operation op = DgraphProto.Operation.newBuilder()
        .setSchema(schema).build();
    dgraphClient.alter(op);
  }
  /**
   * 批量edge的方式写入
   */
  public DgraphProto.Assigned mutiplyEdgeMutation(String edges) {

    DgraphClient.Transaction txn = this.dgraphClient.newTransaction();
    DgraphProto.Assigned assigned = null;
    DgraphProto.Mutation mu = DgraphProto.Mutation.newBuilder()
            .setSetNquads(ByteString.copyFromUtf8(edges))
            .build();
    try {
      assigned = txn.mutate(mu);
      txn.commit();
    } catch (Exception e) {
      logger.info("[mutiplyEdgeMutation Exception] =>" + e.getMessage());
    } finally {
      txn.discard();
    }

    return assigned;
  }

  private String edgeFormat(String src, String pred, String dest) {
    return  String.format("<%s> <%s> <%s> . ", src, pred, dest);
  }

  public void entityAddEdge(List<Nodeput> putList) {
    DgraphClient.Transaction txn = this.dgraphClient.newTransaction();
    int ids = putList.size();
    StringBuffer stringBuffer = new StringBuffer();
    for (int j = 0; j < ids; j++) {
      String uid = putList.get(j).getUid();
      if ("".equals(uid)) {
        continue;
      }
      List<String> predicates = putList.get(j).getPredicates();
      List<Object> values = putList.get(j).getValueObjects();
      int size = predicates.size();
      if (size != values.size()) {
        logger.fatal("add edge predicates length not equal values ");
      }
      for (int i = 0; i < size; i++) {
        String value = String.valueOf(values.get(i).toString());
        String pred = predicates.get(i);
        String result = edgeFormat(uid, pred, value);
        stringBuffer.append(result + "\n");
      }
    }
    mutiplyEdgeMutation(stringBuffer.toString());
  }


  public void entityAddAttr(List<Nodeput> putList) {
    DgraphClient.Transaction txn = this.dgraphClient.newTransaction();
    int ids = putList.size();
    List<DgraphProto.NQuad> quads = new ArrayList<DgraphProto.NQuad>();
    for (int j = 0; j < ids; j++) {
      String uid = putList.get(j).getUid();
      if ("".equals(uid)) {
        continue;
      }
      List<String> predicates = putList.get(j).getPredicates();
      List<Object> values = putList.get(j).getValueObjects();
      int size = predicates.size();
      if (size != values.size()) {
        logger.fatal("add attr predicates length not equal values ");
      }
      for (int i = 0; i < size; i++) {
        Object value = values.get(i);
        DgraphProto.NQuad.Builder builder =
            DgraphProto.NQuad.newBuilder()
                .setSubject(String.format("%s", uid))
                .setPredicate(predicates.get(i));

        if (value instanceof Integer || value instanceof Long) {
          builder.setObjectValue(DgraphProto.Value.newBuilder().setIntVal(Long.valueOf(value.toString())).build());
        } else  if (value instanceof String) {
          builder.setObjectValue(DgraphProto.Value.newBuilder().setStrVal((String) value).build());
        } else if (value instanceof Double || value instanceof Float) {
          builder.setObjectValue(DgraphProto.Value.newBuilder().setDoubleVal(Double.valueOf(value.toString())).build());
        } else if (value instanceof Boolean) {
          builder.setObjectValue(DgraphProto.Value.newBuilder().setBoolVal((Boolean) value).build());
        } else  {
          logger.info("unknow value type");
        }
        quads.add(builder.build());
      }
    }
    DgraphProto.Mutation mu = DgraphProto.Mutation.newBuilder()
        .addAllSet(quads)
        .build();
    try {
      txn.mutate(mu);
      txn.commit();
    } finally {
      txn.discard();
    }
  }

  public DgraphProto.Assigned entityInitial(List<Nodeput> schoolPutList) {
    DgraphClient.Transaction txn = this.dgraphClient.newTransaction();
    int ids = schoolPutList.size();
    List<DgraphProto.NQuad> quads = new ArrayList<DgraphProto.NQuad>();
    for (int j = 0; j < ids; j++) {
      String uniqueId = schoolPutList.get(j).getUniqueId();
      List<String> predicates = schoolPutList.get(j).getPredicates();
      List<Object> values = schoolPutList.get(j).getValueObjects();
      int size = predicates.size();
      if (size != values.size()) {
        logger.fatal("entity inital predicates length not equal values ");
      }
      for (int i = 0; i < size; i++) {
        DgraphProto.NQuad.Builder builder =
            DgraphProto.NQuad.newBuilder()
                .setSubject(String.format("_:%s", uniqueId))
                .setPredicate(predicates.get(i));

        Object value = values.get(i);
        if (value instanceof Integer || value instanceof Long) {
          builder.setObjectValue(DgraphProto.Value.newBuilder().setIntVal(Long.valueOf(value.toString())).build());
        } else  if (value instanceof String) {
          builder.setObjectValue(DgraphProto.Value.newBuilder().setStrVal((String) value).build());
        } else if (value instanceof Double || value instanceof Float) {
          builder.setObjectValue(DgraphProto.Value.newBuilder().setDoubleVal(Double.valueOf(value.toString())).build());
        } else if (value instanceof Boolean) {
          builder.setObjectValue(DgraphProto.Value.newBuilder().setBoolVal((Boolean) value).build());
        } else  {
          logger.info("unknow value type");
        }
        quads.add(builder.build());
      }
    }
    DgraphProto.Mutation mu = DgraphProto.Mutation.newBuilder()
        .addAllSet(quads)
        .build();
    DgraphProto.Assigned ag;
    try {
      ag = txn.mutate(mu);
      txn.commit();
    } finally {
      txn.discard();
    }
    return ag;
  }

  /**
   * 批量对象json的方式写入，写入实体需继承EntityNode
   * @param entities
   * @param <T>
   * @return
   */

  public <T extends EntityNode> DgraphProto.Assigned mutiplyMutationEntity(List<T> entities) {
    DgraphClient.Transaction txnInner = this.dgraphClient.newTransaction();
    DgraphProto.Assigned assigned = null;
    String text = "";
    try {
      int size = entities.size();
      if (size > 0) {
        Gson gson = new Gson();
        text = gson.toJson(entities);
        DgraphProto.Mutation mu = DgraphProto.Mutation.newBuilder()
            .setSetJson(ByteString.copyFromUtf8(text))
            .build();
        assigned = txnInner.mutate(mu);
        txnInner.commit();
      }
    } catch (Exception e) {
      logger.info("[mutiplyMutationEntity Expection]:" + e.getMessage() + ", entity:" + text);
    } finally {
      txnInner.discard();
    }
    return assigned;
  }

  /**
   * 单个string json对象写入
   * @param json
   * @return
   */
  public DgraphProto.Assigned mutation(String json) {
    DgraphClient.Transaction txn = this.dgraphClient.newTransaction();
    DgraphProto.Mutation mu = DgraphProto.Mutation.newBuilder()
        .setSetJson(ByteString.copyFromUtf8(json.toString()))
        .build();
    DgraphProto.Assigned assigned = txn.mutate(mu);
    txn.commit();
    txn.discard();
    return assigned;
  }

  public String QueryById(String did, String className, String methodName) {
    String query =
        "query did($a: string){\n" + "isExist(func: eq(id, $a)) {\n" + "uid\n" + "  }\n" + "}";
    Map<String, String> vars = Collections.singletonMap("$a", did);
    DgraphProto.Response res = dgraphClient.newTransaction().queryWithVars(query, vars);
    Class classNameClass = null;
    Object people = null;
    String ret = "";
    try {
      classNameClass = Class.forName(className);
      // people = classNameClass.newInstance();
      String resp = res.getJson().toStringUtf8();
      people = new Gson().fromJson(resp, Class.forName(className));
      Method[] methods = classNameClass.getMethods();
      // 循环查找想要的方法
      for (Method method : methods) {
        if (methodName.equals(method.getName())) {
          // 调用这个方法，invoke第一个参数是类名，后面是方法需要的参数
          Object result = method.invoke(people);
          return (String) result;
        }
      }
    } catch (IllegalAccessException e) {
      e.printStackTrace();
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
    } catch (InvocationTargetException e) {
      e.printStackTrace();
    }
    return ret;

  }

  public void QueryDemo(String query, Map<String, String> vars) {
    /*
    // Query
    String query =
        "query all($a: string){\n" + "all(func: eq(name, $a)) {\n" + "    name\n" + "  }\n" + "}";
    System.out.println("Query \n:" + query);
    Map<String, String> vars = Collections.singletonMap("$a", "ycj");
    application.Query(query, vars);
     */
    DgraphProto.Response res = dgraphClient.newTransaction().queryWithVars(query, vars);
    // Deserialize
    People ppl = new Gson().fromJson(res.getJson().toStringUtf8(), People.class);
    for (Person p : ppl.getAll()) {
      System.out.println(p.getName());
    }
  }


  private void readFile(String path, HashMap<String, Integer> map) {
    File file = new File(path);
    BufferedReader reader = null;
    try {
      reader = new BufferedReader(new FileReader(file));
      String tempString = null;
      int line = 0;
      // 一次读入一行，直到读入null为文件结束
      while ((tempString = reader.readLine()) != null) {
        String[] split = tempString.split("\t");
        map.put(split[2], Integer.valueOf(split[0].trim()));
      }
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static void main(final String[] args) {
  }
}

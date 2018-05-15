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

  private static  int deadlineSecs = 60;

  public DClient(List<String> adressList) {
    List<DgraphGrpc.DgraphBlockingStub> clients = new ArrayList<>();
    for (String address : adressList) {
      String[] hosts = address.split(":");
      ManagedChannel channel = ManagedChannelBuilder.forAddress(hosts[0], Integer.parseInt
          (hosts[1])).usePlaintext(true).build();
      DgraphGrpc.DgraphBlockingStub blockingStub = DgraphGrpc.newBlockingStub(channel);
      clients.add(blockingStub);
    }
    dgraphClient = new DgraphClient(clients, deadlineSecs);
  }

  public DClient(String[] adressList) {
    List<DgraphGrpc.DgraphBlockingStub> clients = new ArrayList<>();
    for (String address : adressList) {
      String[] hosts = address.split(":");
      ManagedChannel channel = ManagedChannelBuilder.forAddress(hosts[0], Integer.parseInt
          (hosts[1])).usePlaintext(true).build();
      DgraphGrpc.DgraphBlockingStub blockingStub = DgraphGrpc.newBlockingStub(channel);
      clients.add(blockingStub);
    }
    dgraphClient = new DgraphClient(clients, deadlineSecs);
  }

  public void dropSchema() {
    // Initialize
    dgraphClient.alter(DgraphProto.Operation.newBuilder().setDropAll(true).build());
  }

  public void alterSchema(String schema) {
    DgraphProto.Operation op = DgraphProto.Operation.newBuilder().setSchema(schema).build();
    dgraphClient.alter(op);
  }

  /**
   * 批量<uid> <relation> <uid>的方式写入
   */
  public DgraphProto.Assigned mutiplyEdgesMutation(List<String> edges) {
    List<ByteString> newEdges = new ArrayList<>();
    DgraphClient.Transaction txn = this.dgraphClient.newTransaction();
    DgraphProto.Assigned assigned = null;
    for (String edge : edges) {
      logger.info("edge ===> " + edge);
      newEdges.add(ByteString.copyFromUtf8(edge));
    }
    DgraphProto.Mutation mu = DgraphProto.Mutation.newBuilder()
    .setSetNquads(ByteString.copyFrom(newEdges))
        .setCommitNow(true)
        .build();
    try {
      assigned = txn.mutate(mu);
    } catch (Exception e) {
      logger.info("[mutiplyEdgeMutation Exception] =>" + e.getMessage());
      assigned = null;
    } finally {
      txn.discard();
    }

    return assigned;
  }

  /**
   * 批量<uid> <relation> <uid>的方式写入
   */
  @Deprecated
  public DgraphProto.Assigned mutiplyEdgesMutation(String edges) {

    DgraphClient.Transaction txn = this.dgraphClient.newTransaction();
    DgraphProto.Assigned assigned = null;
    DgraphProto.Mutation mu = DgraphProto.Mutation.newBuilder().setSetNquads(ByteString
        .copyFromUtf8(edges))
        .setCommitNow(true)
        .build();
    try {
      assigned = txn.mutate(mu);
    } catch (Exception e) {
      logger.info("[mutiplyEdgeMutation Exception] =>" + e.getMessage());
      assigned = null;
    } finally {
      txn.discard();
    }

    return assigned;
  }

  private String edgeFormat(String src, String pred, String dest) {
    return String.format("<%s> <%s> <%s> . \n", src, pred, dest);
  }

  private String attrFormat(String src, String pred, String dest) {
    return String.format("<%s> <%s> \"%s\" . \n", src, pred, dest);
  }
  /**
   * 形式: <uid> <> <>
   * @param putList
   */
  public void entityAdd(List<Nodeput> putList) {
    int ids = putList.size();
    StringBuffer stringBuffer = new StringBuffer();
    List<String> stringList = new ArrayList<>();
    for (int j = 0; j < ids; j++) {
      Nodeput nodeput = putList.get(j);
      String uid = nodeput.getUid();
      List<String> predicates = nodeput.getPredicates();
      List<Object> values = nodeput.getValueObjects();
      List<String> edge_pred = nodeput.getEdge_predicates();
      List<String> objectIds = nodeput.getObjectIds();
      if ( edge_pred.size() != objectIds.size()) {
        logger.fatal("entity add predicates length not equal values ");
      }
      /*int size = predicates.size();
      if (size != values.size()) {
        logger.fatal("entity add predicates length not equal values ");
      }
      for (int i = 0; i < size; i++) {
        String value = String.valueOf(values.get(i).toString());
        String pred = predicates.get(i);
        String result = attrFormat(uid, pred, value);
        stringBuffer.append(result);
        stringList.add(result);
      }
      */
      // edge feed
      for (int k = 0; k < edge_pred.size(); k++) {
        String edgePredicate = edge_pred.get(k);
        String objectId = objectIds.get(k);
        String result = edgeFormat(uid, edgePredicate, objectId);
        stringBuffer.append(result);
        stringList.add(result);
      }
    }
    if (stringList.size() > 0) {
      logger.info("mutiplyEdgesMutation =====> ");
      mutiplyEdgesMutation(stringList);
    }
  }


  public void entityAddAttrTest(String src, String predicate, String uid) {
    DgraphClient.Transaction txn = this.dgraphClient.newTransaction();
    List<DgraphProto.NQuad> quads = new ArrayList<DgraphProto.NQuad>();

    DgraphProto.NQuad quad = DgraphProto.NQuad.newBuilder().setSubject(String.format("_:%s", src)
    ).setPredicate(predicate).setObjectId(uid).build();
    quads.add(quad);
    DgraphProto.Mutation mu = DgraphProto.Mutation.newBuilder().addAllSet(quads).build();
    try {
      txn.mutate(mu);
      txn.commit();
    } finally {
      txn.discard();
    }
  }

  /**
   *  形式: <_:id> <> <>
   * @param putList
   * @return
   */
  public DgraphProto.Assigned entityInitial(List<Nodeput> putList) {
    DgraphClient.Transaction txn = this.dgraphClient.newTransaction();
    int ids = putList.size();
    List<DgraphProto.NQuad> quads = new ArrayList<>();
    for (int j = 0; j < ids; j++) {
      Nodeput nodeput = putList.get(j);
      String uniqueId = putList.get(j).getUniqueId();
      List<String> predicates = putList.get(j).getPredicates();
      List<Object> values = putList.get(j).getValueObjects();
      List<String> edge_pred = putList.get(j).getEdge_predicates();
      List<String> objectIds = putList.get(j).getObjectIds();
      int size = predicates.size();
      if (size != values.size() || edge_pred.size() != objectIds.size()) {
        logger.fatal("entity inital predicates length not equal values ");
      }
      // value feed : value can not be uid in here
      for (int i = 0; i < size; i++) {
        String pred = predicates.get(i);
        Object value = values.get(i);
        // logger.info("pred:" + pred + ", valueObject:" + value);
        DgraphProto.NQuad.Builder builder = DgraphProto.NQuad.newBuilder().setSubject(String
            .format("_:%s", uniqueId)).setPredicate(pred);
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
          continue;
        } else {
          // 需要处理predicate为uid类型，... 见 edge feed
          continue;
        }
        quads.add(builder.build());
      }
      // edge feed
      for (int k = 0; k < edge_pred.size(); k++) {
        String edgePredicate = edge_pred.get(k);
        String objectId = objectIds.get(k);
        // logger.info("edge pred:" + edgePredicate + ", object id:" + objectId);
        // 存在uid
        DgraphProto.NQuad quad = DgraphProto.NQuad.newBuilder().setSubject(String.format("_:%s",
            uniqueId)).setPredicate(edgePredicate).setObjectId(objectId).build();
        quads.add(quad);
      }
    }
    DgraphProto.Mutation mu = DgraphProto.Mutation.newBuilder().addAllSet(quads).build();
    DgraphProto.Assigned ag = null;
    try {
      ag = txn.mutate(mu);
      txn.commit();
    }  catch (Exception e) {
      logger.info("[entityInitial Expection]:" + e.getMessage());
      ag = null;
    } finally {
      txn.discard();
    }
    return ag;
  }

  /**
   * 批量对象json的方式写入，写入实体需继承EntityNode
   */

  public <T extends EntityNode> DgraphProto.Assigned multiplyMutationEntity(List<T> entities) {
    DgraphClient.Transaction txnInner = this.dgraphClient.newTransaction();
    DgraphProto.Assigned assigned = null;
    String text = "";
    try {
      int size = entities.size();
      if (size > 0) {
        Gson gson = new Gson();
        text = gson.toJson(entities);
        logger.info("text:" + text);
        DgraphProto.Mutation mu = DgraphProto.Mutation.newBuilder().setSetJson(ByteString
            .copyFromUtf8(text)).build();
        assigned = txnInner.mutate(mu);
        txnInner.commit();
      }
    } catch (Exception e) {
      logger.info("[multiplyMutationEntity Expection]:" + e.getMessage() + ", entity:" + text);
      assigned = null;
    } finally {
      txnInner.discard();
    }
    return assigned;
  }

  /**
   * 单个string json对象写入
   */
  public DgraphProto.Assigned mutation(String json) {
    DgraphClient.Transaction txn = this.dgraphClient.newTransaction();
    DgraphProto.Mutation mu = DgraphProto.Mutation.newBuilder().setSetJson(ByteString
        .copyFromUtf8(json.toString())).build();
    DgraphProto.Assigned assigned = txn.mutate(mu);
    txn.commit();
    txn.discard();
    return assigned;
  }

  public String QueryById(String did, String className, String methodName) {
    String query = "query did($a: string){\n" + "isExist(func: eq(id, $a)) {\n" + "uid\n" + "  "
        + "}\n" + "}";
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

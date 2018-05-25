package dgraph;

import com.google.gson.Gson;
import com.google.protobuf.ByteString;
import com.google.protobuf.util.JsonFormat;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import client.dgrpah.DgraphClient;
import dgraph.node.EntityNode;
import dgraph.put.EdgeFacetsPut;
import dgraph.put.Nodeput;
import io.dgraph.DgraphGrpc;
import io.dgraph.DgraphProto;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

/**
 * User: JerryYou
 *
 * Date: 2018-05-08
 *
 * Copyright (c) 2018 devops
 *
 * 基于DgraphClient上的另一个逻辑实现
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
  private int retryCompensation = 100;
  // deadline exceed retry max number default 5
  @Deprecated
  private int retryMaxNumber = 5;

  private AtomicInteger retryCounter = new AtomicInteger(0);

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

  /**
   * 删除schema
   */
  public void dropSchema() {
    // Initialize
    dgraphClient.alter(DgraphProto.Operation.newBuilder().setDropAll(true).build());
  }

  /**
   * 修改schema
   * @param schema schema
   */
  public void alterSchema(String schema) {
    DgraphProto.Operation op = DgraphProto.Operation.newBuilder().setSchema(schema).build();
    dgraphClient.alter(op);
  }

  /**
   * 批量<uid> <relation> <uid>的方式写入
   * @param edges 属性数组
   * @return uid assigned
   */
  public DgraphProto.Assigned multiplyEdgesMutation(List<String> edges) {
    List<ByteString> newEdges = new ArrayList<>();
    DgraphClient.Transaction txn = this.dgraphClient.newTransaction();
    DgraphProto.Assigned assigned = null;
    for (String edge : edges) {
      logger.info("edge ===> " + edge);
      newEdges.add(ByteString.copyFromUtf8(edge));
    }
    DgraphProto.Mutation mu = DgraphProto.Mutation.newBuilder()
    .setSetNquads(ByteString.copyFrom(newEdges))
        .build();
    try {
      assigned = txn.mutate(mu);
      txn.commit();
    } catch (Exception e) {
      logger.info("[multiplyEdgeMutation Exception] =>" + e.getMessage());
      assigned = mutateRetry(mu, e);
    } finally {
      txn.discard();
    }

    return assigned;
  }

  /**
   * 批量<uid> <relation> <uid>的方式写入
   * @param edges 熟悉
   * @return uid assigned
   */
  @Deprecated
  public DgraphProto.Assigned multiplyEdgesMutation(String edges) {

    DgraphClient.Transaction txn = this.dgraphClient.newTransaction();
    DgraphProto.Assigned assigned = null;
    DgraphProto.Mutation mu = DgraphProto.Mutation.newBuilder().setSetNquads(ByteString
        .copyFromUtf8(edges))
        .build();
    try {
      assigned = txn.mutate(mu);
      txn.commit();
    } catch (Exception e) {
      logger.info("[multiplyEdgeMutation Exception] =>" + e.getMessage());
      assigned = null;
    } finally {
      txn.discard();
    }

    return assigned;
  }

  /**
   * 批量<uid> <relation> <uid>的方式format
   * @param src start uid
   * @param pred relation
   * @param dest end uid
   * @return format string
   */
  private String edgeFormat(String src, String pred, String dest) {
    return String.format("<%s> <%s> <%s> . \n", src, pred, dest);
  }

  /**
   * 批量<uid> <relation> "attribute"的方式format
   * @param src start uid
   * @param pred relation
   * @param dest end uid
   * @return format string
   */
  private String attrFormat(String src, String pred, String dest) {
    return String.format("<%s> <%s> \"%s\" . \n", src, pred, dest);
  }

  /**
   * 增加边的facets
   * @param src start uid
   * @param pred relation
   * @param dest end uid
   * @param facets 边的属性集合
   * @return Nquad string
   */
  private String edgeFacetsFormat(String src, String pred, String dest, List<String> facets) {
    StringBuilder stringBuilder = new StringBuilder();
    int facetSize = facets.size();
    for (int i = 0; i < facetSize; i ++) {
      if (i == 0) {
        stringBuilder.append(facets.get(i));
      } else {
        stringBuilder.append("," + facets.get(i));
      }
    }
    return String.format("<%s> <%s> <%s> (%s). \n", src, pred, dest, stringBuilder.toString());
  }

  public void entityAddFacets(List<EdgeFacetsPut> edgeFacetsPutList) {
    List<String> stringList = new ArrayList<>();
    for (EdgeFacetsPut edgeFacetsPut : edgeFacetsPutList) {
      List<String> srcs = edgeFacetsPut.getSrcs();
      List<String> dsts = edgeFacetsPut.getDst();
      List<String> predicates = edgeFacetsPut.getPredicates();
      List<List<String>> facets = edgeFacetsPut.getFacets();
      int size = srcs.size();
      for (int i = 0; i < size; i++) {
        String src = srcs.get(i);
        String dst = dsts.get(i);
        String predicate = predicates.get(i);
        List<String> facet = facets.get(i);
        String result = edgeFacetsFormat(src, predicate, dst, facet);
        stringList.add(result);
      }
    }
    if (stringList.size() > 0) {
      logger.info("entityAddFacets multiplyEdgesMutation =====> ");
      multiplyEdgesMutation(stringList);
    }
  }

  /**
   * 形式: <uid> <> <>
   * @param putList node 属性的上一层抽象
   */
  public void entityAddAttr(List<Nodeput> putList) {
    StringBuilder stringBuffer = new StringBuilder();
    List<String> stringList = new ArrayList<>();
    for (Nodeput nodeput : putList) {
      String uid = nodeput.getUid();
      List<String> predicates = nodeput.getPredicates();
      List<Object> values = nodeput.getValueObjects();
      if (predicates.size() != values.size()) {
        logger.fatal("entity add predicates length not equal values ");
      }
      // 属性值添加
      int size = predicates.size();
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
    }
    if (stringList.size() > 0) {
      logger.info("multiplyEdgesMutation =====> ");
      multiplyEdgesMutation(stringList);
    }
  }
  /**
   * 形式: <uid> <> <>
   * @param putList  node 属性的上一层抽象
   */
  public void entityAdd(List<Nodeput> putList) {
    StringBuffer stringBuffer = new StringBuffer();
    List<String> stringList = new ArrayList<>();
    for (Nodeput nodeput : putList) {
      String uid = nodeput.getUid();
      List<String> predicates = nodeput.getPredicates();
      List<Object> values = nodeput.getValueObjects();
      List<String> edge_pred = nodeput.getEdge_predicates();
      List<String> objectIds = nodeput.getObjectIds();
      if (edge_pred.size() != objectIds.size()) {
        logger.fatal("entity add predicates edge length not equal values ");
      }
      // 属性值重新修改
      int size = predicates.size();
      if (size != values.size()) {
        logger.fatal("entity add predicates attr length not equal values ");
      }
      for (int i = 0; i < size; i++) {
        String value = String.valueOf(values.get(i).toString());
        String pred = predicates.get(i);
        String result = attrFormat(uid, pred, value);
        stringBuffer.append(result);
        stringList.add(result);
      }
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
      logger.info("entityAdd multiplyEdgesMutation =====> ");
      multiplyEdgesMutation(stringList);
    }
  }

  /**
   *
   * @param src  start uid
   * @param predicate relation
   * @param uid end uid
   */
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
   * 形式: <_:id> <> <>
   * rdf 格式
   * @param putList node 属性的上一层抽象
   * @return rdf 形式的实体插入
   */
  public DgraphProto.Assigned entityInitial(List<Nodeput> putList) {
    DgraphClient.Transaction txn = this.dgraphClient.newTransaction();
    List<DgraphProto.NQuad> quads = new ArrayList<>();
    for (Nodeput nodeput : putList) {
      String uniqueId = nodeput.getUniqueId();
      List<String> predicates = nodeput.getPredicates();
      List<Object> values = nodeput.getValueObjects();
      List<String> edge_pred = nodeput.getEdge_predicates();
      List<String> objectIds = nodeput.getObjectIds();
      int size = predicates.size();
      if (size != values.size() || edge_pred.size() != objectIds.size()) {
        logger.fatal("entity inital predicates length not equal values ");
      }
      // value feed : value can not be uid in here
      for (int i = 0; i < size; i++) {
        String pred = predicates.get(i);
        Object value = values.get(i);
        // logger.info("uniqueId:" + uniqueId+ ", pred:" + pred + ", valueObject:" + value);
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
    DgraphProto.Assigned ag;
    try {
      ag = txn.mutate(mu);
      txn.commit();
    }  catch (Exception e) {
      logger.info("[entityInitial Expection]:" + e.getMessage());
      ag = mutateRetry(mu, e);
    } finally {
      txn.discard();
    }
    return ag;
  }

  /**
   * 批量<uid> <relation> <uid>的方式写入
   * exception retry mutate
   * @param mu mutation
   * @param exception exception
   * @return uid assigned
   */
  private DgraphProto.Assigned mutateRetry(DgraphProto.Mutation mu, Exception exception) {
    // retry multiply times as possible
    // io.grpc.StatusRuntimeException: UNAVAILABLE: Channel in TRANSIENT_FAILURE state code: 14
    // io.grpc.StatusRuntimeException: DEADLINE_EXCEEDED code: 4
    // io.grpc.StatusRuntimeException: Please retry again, server is not ready to accept
    // request code: 2
    // 可能的异常: TxnConflictException,
    DgraphProto.Assigned assigned = null;
    String message;
    int code;
    if (exception instanceof StatusRuntimeException) {
      code = ((StatusRuntimeException) exception).getStatus().getCode().value();
      message = exception.getMessage();
      logger.info("[StatusRuntimeException code value]:" + code);
    } else {
      logger.info("[OtherException]:" + exception);
      return assigned;
    }
    // 重试直到非该exception为止
    while("DEADLINE_EXCEEDED".equals(message) && code == 4) {
      message = "";
      code = 0;
      try {
        Thread.sleep(retryCompensation * retryCounter.incrementAndGet());
      } catch (InterruptedException e) {
        logger.info("[Sleep error Exception]:" + e.getMessage());
      }
      DgraphClient.Transaction txn = this.dgraphClient.newTransaction();
      try {
        assigned = txn.mutate(mu);
        txn.commit();
      } catch (Exception e) {
        logger.info("[multiplyEdgeMutation Retry Exception] => " + e.getMessage() +
            ", retry times:" + retryCounter.get());
        message = e.getMessage();
        if (e instanceof StatusRuntimeException) {
          code = ((StatusRuntimeException) exception).getStatus().getCode().value();
        }
        assigned = null;
      } finally {
        txn.discard();
      }
    }
    retryCounter.set(0);
    return assigned;
  }

  /**
   * 批量对象json的方式写入，写入实体需继承EntityNode
   * @param entities 实体数组
   * @param <T> 支持实体泛型，继承自EntityNode
   * @return uid assigned
   */
  public <T extends EntityNode> DgraphProto.Assigned multiplyMutationEntity(List<T> entities) {
    DgraphClient.Transaction txnInner = this.dgraphClient.newTransaction();
    DgraphProto.Assigned assigned;
    String text;
    int size = entities.size();
    if (size <= 0) {
      return null;
    }
    Gson gson = new Gson();
    text = gson.toJson(entities);
    logger.info("text:" + text);
    DgraphProto.Mutation mu = DgraphProto.Mutation.newBuilder().setSetJson(ByteString
        .copyFromUtf8(text)).build();
    try {
      assigned = txnInner.mutate(mu);
      txnInner.commit();
    } catch (Exception e) {
      logger.info("[multiplyMutationEntity Exception]:" + e.getMessage());
      assigned = mutateRetry(mu, e);
    } finally {
      txnInner.discard();
    }
    return assigned;
  }

  /**
   * 单个string json对象写入
   * @param json 单个string json写入dgraph
   * @return uid assigned
   */
  public DgraphProto.Assigned mutation(String json) {
    DgraphClient.Transaction txn = this.dgraphClient.newTransaction();
    DgraphProto.Mutation mu = DgraphProto.Mutation.newBuilder().setSetJson(ByteString
        .copyFromUtf8(json)).build();
    DgraphProto.Assigned assigned = txn.mutate(mu);
    txn.commit();
    txn.discard();
    return assigned;
  }

  /**
   * dgraph的查询
   * @param did 查询id
   * @param className 实体对象类名
   * @param methodName 获取实体的哪个方法名
   * @return uid
   */
  public String QueryById(String did, String className, String methodName){
    String query = "query did($a: string){\n" + "isExist(func: eq(id, $a)) {\n" + "uid\n" + "  "
        + "}\n" + "}";
    Map<String, String> vars = Collections.singletonMap("$a", did);
    DgraphProto.Response res = dgraphClient.newTransaction().queryWithVars(query, vars);
    Class classNameClass;
    Object people;
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
    } catch (IllegalAccessException | ClassNotFoundException e) {
      e.printStackTrace();
    } catch (InvocationTargetException e) {

    }
    return ret;

  }
  public static void main(final String[] args) {
    // ...todo
  }
}

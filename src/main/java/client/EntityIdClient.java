package client;

import com.amazonaws.services.dynamodbv2.xspec.S;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import dgraph.del.NodeDel;
import dgraph.node.EntityNode;
import dgraph.put.EdgeFacetPut;
import dgraph.put.EdgeFacetsPut;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
import kb.rpc.BatchEntityIdRequest;
import kb.rpc.BatchEntityIdResponse;
import kb.rpc.EntityIdRequest;
import kb.rpc.EntityIdResponse;
import kb.rpc.EntityIdServiceGrpc;
import utils.FileUtils;


/**
 * Created by Jerry You on 2018/5/3.
 * hadoop.dgraph uid 自维护客户端
 *
 * 返回的数据（proto）可能需要稍作修改如下：
 * 1.重复names入库，uid需要根据最新一次更新
 * 2.
 */

public class EntityIdClient {
  private static Logger logger = LoggerFactory.getLogger(EntityIdClient.class);
  private final ManagedChannel channel;
  private final EntityIdServiceGrpc.EntityIdServiceBlockingStub blockingStub;

  public EntityIdClient(String host, int port) {
    this(ManagedChannelBuilder.forAddress(host, port).usePlaintext(true));
  }

  public EntityIdClient(ManagedChannelBuilder<?> channelBuilder) {
    channel = channelBuilder.build();
    blockingStub = EntityIdServiceGrpc.newBlockingStub(channel);
  }

  public void shutdown() throws InterruptedException {
    channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
  }

  /**
   * 写入实体id服务,支持多个names
   * @param map
   */
  public void putFeedEntityWithNames(Map<String, List<String>> map, String type) {
    Set<Map.Entry<String, List<String>>> entrySet=  map.entrySet();
    Iterator<Map.Entry<String, List<String>>> iterator = entrySet.iterator();
    int batch = 0;
    List<EntityIdRequest> entityIdRequestList = new ArrayList<EntityIdRequest>();
    while(iterator.hasNext()) {
      Map.Entry<String, List<String>> entry = iterator.next();
      String key = entry.getKey();
      List<String> value = entry.getValue();
      entityIdRequestList.add(EntityIdRequest.newBuilder()
          .addAllName(value)
          .setType(type)
          .setId(Long.parseLong(key.substring(2), 16)).build());
      batch++;
      if (batch > 200) {
        batch = 0;
        BatchEntityIdResponse rep = feedEntity(BatchEntityIdRequest.newBuilder()
            .addAllEntityReq(entityIdRequestList).build());
        if (rep == null) {
          break;
        }
        entityIdRequestList.clear();
      }
    }
    if (batch > 0) {
      feedEntity(BatchEntityIdRequest.newBuilder()
          .addAllEntityReq(entityIdRequestList).build());
    }
  }

  /**
   * 写入实体id服务，only with single name
   * @param map
   */
  @Deprecated
  public void putFeedEntity(Map<String, String> map, String type) {
    Set<Map.Entry<String, String>> entrySet=  map.entrySet();
    Iterator<Map.Entry<String, String>> iterator = entrySet.iterator();
    int batch = 0;
    List<EntityIdRequest> entityIdRequestList = new ArrayList<EntityIdRequest>();
    while(iterator.hasNext()) {
      Map.Entry<String, String> entry = iterator.next();
      String key = entry.getKey();
      String value = entry.getValue();
      entityIdRequestList.add(EntityIdRequest.newBuilder().addName(key)
          .setType(type)
          .setId(Long.parseLong(value.substring(2), 16)).build());
      batch++;
      if (batch > 200) {
        batch = 0;
        BatchEntityIdResponse rep = feedEntity(BatchEntityIdRequest.newBuilder()
            .addAllEntityReq(entityIdRequestList).build());
        if (rep == null) {
          break;
        }
        entityIdRequestList.clear();
      }
    }
    if (batch > 0) {
      feedEntity(BatchEntityIdRequest.newBuilder()
          .addAllEntityReq(entityIdRequestList).build());
    }
  }

  public void checkDelEntityUid(List<NodeDel> nodeDelList, String type) {
    int size = nodeDelList.size();
    List<EntityIdRequest> entityIdRequestList = new ArrayList<EntityIdRequest>();
    for (int i = 0; i < size; i++) {
      NodeDel nodeDel = nodeDelList.get(i);
      String unique_id = nodeDel.getUniqueId();
      entityIdRequestList.add(EntityIdRequest.newBuilder()
          .addName(unique_id).setType(type)
          .build());
    }
    BatchEntityIdRequest req = BatchEntityIdRequest.newBuilder()
        .addAllEntityReq(entityIdRequestList).build();
    BatchEntityIdResponse rep = entityLinkSimple(req);
    if (rep != null) {
      for (int i = 0; i < size; i++) {
        NodeDel nodeDel = nodeDelList.get(i);
        EntityIdResponse entityIdResponse = rep.getEntityResList().get(i);
        long id = entityIdResponse.getId();
        boolean ok = entityIdResponse.getOk();
        String msg = entityIdResponse.getMsg();
        // 如果服务直接返回了matched_name,可直接使用
        // String matchedName = entityIdResponse.getMatchedName();
        if (ok) {
          String values = "0x" + Long.toHexString(id);
          nodeDel.setUid(values);
        } else {
          nodeDel.setUid("");
        }
      }
    }
  }

  public <T extends EntityNode> List<String> checkEntityList(List<T> entityReqs, String type) {
    List<String> uids = new ArrayList<>();
    int outSize = entityReqs.size();
    List<EntityIdRequest> entityIdRequestList = new ArrayList<EntityIdRequest>();
    for (int i = 0; i < outSize; i++) {
      T entityNode = entityReqs.get(i);
      entityIdRequestList.add(EntityIdRequest.newBuilder().addAllName(entityNode.getUnique_ids()).setType(type)
          .build());
    }
    BatchEntityIdRequest req = BatchEntityIdRequest.newBuilder()
        .addAllEntityReq(entityIdRequestList).build();
    BatchEntityIdResponse rep = entityLinkSimple(req);
    if (rep != null) {
      for (int i = 0; i < outSize; i++) {
        EntityIdResponse entityIdResponse = rep.getEntityResList().get(i);
        long id = entityIdResponse.getId();
        boolean ok = entityIdResponse.getOk();
        String msg = entityIdResponse.getMsg();
        // 如果服务直接返回了matched_name,可直接使用
        // String matchedName = entityIdResponse.getMatchedName();
        if (ok) {
          String values = "0x" + Long.toHexString(id);
          uids.add(values);
        } else {
          uids.add("");
        }
      }
    }
    return uids;
  }

  public <T extends EntityNode> void getNoneExistEntityList(List<T> entityReqs, String
      type, List<T> newEntityReqs) {
    int outSize = entityReqs.size();
    List<EntityIdRequest> entityIdRequestList = new ArrayList<EntityIdRequest>();
    for (int i = 0; i < outSize; i++) {
      T entityNode = entityReqs.get(i);
      entityIdRequestList.add(EntityIdRequest.newBuilder().addAllName(entityNode.getUnique_ids()).setType(type)
          .build());
    }
    BatchEntityIdRequest req = BatchEntityIdRequest.newBuilder()
        .addAllEntityReq(entityIdRequestList).build();
    BatchEntityIdResponse rep = entityLinkSimple(req);
    if (rep != null) {
      for (int i = 0; i < outSize; i++) {
        EntityIdResponse entityIdResponse = rep.getEntityResList().get(i);
        long id = entityIdResponse.getId();
        boolean ok = entityIdResponse.getOk();
        String msg = entityIdResponse.getMsg();
        // 如果服务直接返回了matched_name,可直接使用
        // String matchedName = entityIdResponse.getMatchedName();
        if (!ok) {
          newEntityReqs.add(entityReqs.get(i));
        }
      }
    }
  }

  public <T extends EntityNode> void checkEntityListAndPutUid(List<T> entityReqs, String type) {
    int outSize = entityReqs.size();
    List<EntityIdRequest> entityIdRequestList = new ArrayList<EntityIdRequest>();
    for (int i = 0; i < outSize; i++) {
      T entityNode = entityReqs.get(i);
      entityIdRequestList.add(EntityIdRequest.newBuilder().addAllName(entityNode.getUnique_ids()).setType(type).build());
    }
    BatchEntityIdRequest req = BatchEntityIdRequest.newBuilder()
        .addAllEntityReq(entityIdRequestList).build();
    BatchEntityIdResponse rep = entityLinkSimple(req);
    if (rep != null) {
      for (int i = 0; i < outSize; i++) {
        EntityIdResponse entityIdResponse = rep.getEntityResList().get(i);
        long id = entityIdResponse.getId();
        boolean ok = entityIdResponse.getOk();
        String msg = entityIdResponse.getMsg();
        // 如果服务直接返回了matched_name,可直接使用
        // String matchedName = entityIdResponse.getMatchedName();
        if (ok) {
          String values = "0x" + Long.toHexString(id);
          // 写回uid
          T entityNode = entityReqs.get(i);
          entityNode.setUid(values);
        }
      }
    }
  }

  /**
   * 入库完dgraph后的调用，绑定name和id
   * @param req
   * @return
   */
  private BatchEntityIdResponse feedEntity(BatchEntityIdRequest req) {
    BatchEntityIdResponse rep = null;
    try {
      rep = blockingStub.feedEntity(req);
    } catch (StatusRuntimeException e) {
      logger.error("feedEntity rpc failed: {0}", e.getStatus());
    }
    return rep;
  }

  /**
   * 连接其他外部id和dgraph之前的关系(查uid)
   * @param req
   * @return
   */
  private BatchEntityIdResponse entityLinkSimple(BatchEntityIdRequest req) {
    BatchEntityIdResponse rep = null;
    try {
      rep = blockingStub.entityLinkSimple(req);
    } catch (StatusRuntimeException e) {
      logger.error("entityLinkSimple rpc failed: {0}", e.getStatus());
    }
    return rep;
  }

  private Map<String, List<String>> reMappingName(String uidMapDict) {
    Map<String, List<String>> uidMap = new HashMap<>();
    FileUtils.readUidMapDict(uidMapDict, uidMap);
    this.putFeedEntityWithNames(uidMap, "候选人");
    return uidMap;
  }

  public static void main(String[] args) throws Exception {
    EntityIdClient client = new EntityIdClient("172.20.0.14", 26544);
    // client.reMappingName("/Users/devops/Documents/知识图谱/candidate/00/uidmap/part-m-00000");
    try {

      BatchEntityIdResponse rep = client.entityLinkSimple(BatchEntityIdRequest.newBuilder()
            .addEntityReq(EntityIdRequest.newBuilder()
                .addAllName(Arrays.asList("辽宁大学"))
                .setType("学校").build())
          .build());
      if (rep != null) {
        EntityIdResponse entityIdResponse = rep.getEntityResList().get(0);
        long id = entityIdResponse.getId();
        String values = "0x" + Long.toHexString(id);
        boolean ok = entityIdResponse.getOk();
        String msg = entityIdResponse.getMsg();
        String matchName = entityIdResponse.getMatchedName();
        System.out.println("id/value:" + id + "," + values + ",ok:" + ok + ",msg:" + msg + "," +
            "matchName:" + matchName);
      }
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      client.shutdown();
    }

  }
}

package com.higgs.client;

import com.google.gson.Gson;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.higgs.dgraph.del.NodeDel;
import com.higgs.dgraph.node.EntityNode;
import com.higgs.utils.FileUtils;
import com.higgs.utils.Util;
import com.inmind.idmg.serving.dgrpah.query.rpc.DgraphQueryGrpc;
import com.inmind.idmg.serving.dgrpah.query.rpc.QueryRequest;
import com.inmind.idmg.serving.dgrpah.query.rpc.QueryRespond;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import kb.rpc.BatchEntityIdRequest;
import kb.rpc.BatchEntityIdResponse;
import kb.rpc.EntityIdRequest;
import kb.rpc.EntityIdResponse;
import kb.rpc.EntityIdServiceGrpc;


/**
 * Created by Jerry You on 2018/5/3.
 * dgraph query
 *
 */

public class DgraphQueryClient {
  private static Logger logger = LoggerFactory.getLogger(DgraphQueryClient.class);
  private final ManagedChannel channel;
  private final DgraphQueryGrpc.DgraphQueryBlockingStub blockingStub;

  public DgraphQueryClient(String host, int port) {
    this(ManagedChannelBuilder.forAddress(host, port).usePlaintext(true));
  }

  public DgraphQueryClient(ManagedChannelBuilder<?> channelBuilder) {
    channel = channelBuilder.build();
    blockingStub = DgraphQueryGrpc.newBlockingStub(channel);
  }
  private QueryRespond query(QueryRequest queryRequest) {
    QueryRespond queryRespond = null;
    try {
      queryRespond = blockingStub.query(queryRequest);
    } catch (StatusRuntimeException e) {
      logger.error("query rpc failed: {0}", e.getStatus());
    }
    return queryRespond;
  }

  public void shutdown() throws InterruptedException {
    channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
  }

  public static void main(String[] args) throws Exception {
    DgraphQueryClient client = new DgraphQueryClient("127.0.0.1", 26549);
    try {
      QueryRequest request = QueryRequest.newBuilder()
          .setUniqueId("深圳市腾讯计算机系统有限公司")
          .setQueryType(QueryRequest.QueryType.COMPANY_DEPTNAMES_CANDIDATE_MAX_DEGREE)
          .setUniqueIdType(QueryRequest.UniqueIdType.COMPANY)
          .setPage(0)
          // .setPageSize(10)
          .setDeptName("研发部")
          .build();
      QueryRespond queryRespond = client.query(request);
      String ret = queryRespond.getResultJson();
      if (ret != null && !"".equals(ret)) {
        Util.formatPrintJson(ret);
      }
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      client.shutdown();
    }

  }
}

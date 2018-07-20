package com.higgs.client;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.okhttp.OkHttpChannelBuilder;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import kb.rpc.dept.BatchDeptRequest;
import kb.rpc.dept.BatchDeptResponse;
import kb.rpc.dept.DeptNormServiceGrpc;
import kb.rpc.dept.DeptRequest;
import kb.rpc.dept.DeptResponse;

/**
 * User: JerryYou
 *
 * Date: 2018-07-16
 *
 * Copyright (c) 2018 devops
 *
 * <<licensetext>>
 */
public class DeptNormClient {
  private final ManagedChannel channel;
  private final DeptNormServiceGrpc.DeptNormServiceBlockingStub blockingStub;
  private static Logger logger = LoggerFactory.getLogger(DeptNormClient.class);

  /**
   * Construct client connecting to server at {@code host:port}.
   */
  public DeptNormClient(String host, int port) {
    this(OkHttpChannelBuilder.forAddress(host, port).usePlaintext(true));
  }

  public DeptNormClient(String hostAndPort) {
    this(OkHttpChannelBuilder.forAddress(hostAndPort.split(":")[0],
        Integer.parseInt(hostAndPort.split(":")[1])).usePlaintext(true));
  }

  /**
   * Construct client for accessing RouteGuide server using the existing channel.
   */
  DeptNormClient(ManagedChannelBuilder<?> channelBuilder) {
    channel = channelBuilder.build();
    blockingStub = DeptNormServiceGrpc.newBlockingStub(channel);
  }

  public void shutdown() throws InterruptedException {
    channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
  }

  public BatchDeptResponse batchNorm(BatchDeptRequest names) {
    BatchDeptResponse nameNormals = null;
    try {
      nameNormals = blockingStub.normDept(names);
    } catch (Exception e) {
      logger.error("batchNorm error :" + e.getMessage());
    }
    return nameNormals;
  }

  public static void main(String[] args) throws Exception {
    DeptNormClient client = new DeptNormClient("172.20.0.14", 26550);
    List<DeptRequest> deptRequestList = new ArrayList<>();
    List<String> depts = Arrays.asList("搜索应用部门", "人力资源", "人事部门", "战略投资");
    for (String dept : depts) {
      deptRequestList.add(DeptRequest.newBuilder().setName(dept).build());
    }
    try {
      BatchDeptResponse reply = client.batchNorm(BatchDeptRequest.newBuilder().addAllDeptReq(deptRequestList).build());
      int count = reply.getDeptResCount();
      JsonArray jsonArray = new JsonArray();
      for (int i = 0; i < count; i++) {
        DeptResponse deptRes = reply.getDeptRes(i);
        String normed = deptRes.getNormed();
        jsonArray.add(normed);
      }
      logger.info("normed => " + jsonArray);
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      client.shutdown();
    }
  }
}

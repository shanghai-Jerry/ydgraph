package com.higgs.client;

import com.higgs.utils.FileUtils;

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

  public void dumpFile(List<String> depts, String out) {
    List<DeptRequest> deptRequestList = new ArrayList<>();
    for (String dept : depts) {
      deptRequestList.add(DeptRequest.newBuilder().setName(dept).build());
    }
    List<String> result = new ArrayList<>();
    try {
      BatchDeptResponse reply = batchNorm(BatchDeptRequest.newBuilder().addAllDeptReq(deptRequestList).build());
      int count = reply.getDeptResCount();
      for (int i = 0; i < count; i++) {
        DeptResponse deptRes = reply.getDeptRes(i);
        String normed = deptRes.getNormed();
        result.add(depts.get(i) + " => " + normed);
      }
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
    }
    FileUtils.saveFiles(out, result, false);
  }

  public static void main(String[] args) throws Exception {
    DeptNormClient client = new DeptNormClient("172.20.0.14", 26550);

    List<String> depts = new ArrayList<>();
    List<String> lines = new ArrayList<>();
    String srcDir = "/Users/devops/Documents/部门归一化";
    FileUtils.readFile(srcDir + "/dept_name_resume.txt", lines);
    int top = 1000;
    int offset = 10;
    int index = 0;
    int margin = 0;
    int ceilFreq = 1000;
    int floorFreq = 100;
    for (String line : lines) {
      index++;
      String [] sp = line.split(",");
      String key = sp[0];
      int freq = Integer.parseInt(sp[1]);
      // && (offset * top) <= index && index < ((offset + 1) * top)
      if ( floorFreq <= freq && freq <= ceilFreq) {
        depts.add(key);
      } else {
        if (depts.size() == 0) {
          continue;
        } else {
          String out = srcDir + "/normed_" + floorFreq + "-" + ceilFreq + ".txt";
          client.dumpFile(depts, out);
          logger.info("dept size:" + depts.size());
          depts.clear();
          offset++;
          margin++;
          if (margin >= 5) {
            break;
          }
          break;
        }
      }
    }
    client.shutdown();
  }
}

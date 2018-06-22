package com.higgs.client.dgrpah;

import javax.security.sasl.SaslServer;

import intern.Internal;
import intern.ZeroGrpc;
import io.dgraph.DgraphGrpc;
import io.dgraph.DgraphProto;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

/**
 * User: JerryYou
 *
 * Date: 2018-06-22
 *
 * Copyright (c) 2018 devops
 *
 * <<licensetext>>
 */
public class ZeroClient {

  private static Logger logger = LoggerFactory.getLogger(ZeroClient.class);
  private ZeroGrpc.ZeroBlockingStub zeroClient;
  // localhost:5080
  ZeroClient(String address) {
    String[] hosts = address.split(":");
    ManagedChannel channel = ManagedChannelBuilder.forAddress(hosts[0], Integer.parseInt
        (hosts[1])).usePlaintext(true).build();
    ZeroGrpc.ZeroBlockingStub blockingStub = ZeroGrpc.newBlockingStub(channel);
    this.zeroClient = blockingStub;
  }

  public ZeroGrpc.ZeroBlockingStub getZeroClient() {
    return zeroClient;
  }

  public void setZeroClient(ZeroGrpc.ZeroBlockingStub zeroClient) {
    this.zeroClient = zeroClient;
  }

  /**
   * this operation can free uid lease in dgraph.
   * To avoid this kind of exception:
   * Exception:
   * Uid: [24387] cannot be greater than lease: [0]
   */
  public void freeLeaseUidInZero() {
    DgraphProto.AssignedIds assigned = zeroClient.assignUids(Internal.Num.newBuilder().setVal(Long
        .MAX_VALUE).build());
    logger.info("zero assigned ids:" + assigned.getStartId() + " - " + assigned.getEndId());
  }

  public static void main(final String[] args) {
     ZeroClient client = new ZeroClient("172.20.0.68:5080");
     DgraphProto.AssignedIds assigned = client.getZeroClient().assignUids(Internal.Num.newBuilder()
        .setVal(Long.MAX_VALUE).build());
      logger.info("zero assigned ids:" + assigned.getStartId() + " - " + assigned.getEndId());


  }
}

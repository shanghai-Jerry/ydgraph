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


  public void getStartId() {
    DgraphProto.AssignedIds assigned;
    assigned = zeroClient.timestamps(Internal.Num.newBuilder().setVal(1L).build());
    logger.info("timestamp zero assigned ids:" + assigned.getStartId() + " - " + assigned.getEndId());
  }

  /**
   * this operation can free uid lease in dgraph.
   * To avoid this kind of exception:
   * Exception:
   * Uid: [24387] cannot be greater than lease: [0]
   * 获取已使用的num个uid区间, 每次都是申请num个uid（uid上限在每次申请的时候依次增加num）
   * 这个区间内的下一个uid就是新增数据分配的uid
   * 所以在初始化的时候需要找到已分配的最大uid，设置num为该最大uid值就可
   * @param num  uid的个数
   */
  public void freeLeaseUidInZero(long num) {
    DgraphProto.AssignedIds assigned;
    assigned = zeroClient.assignUids(Internal.Num.newBuilder().setVal(num).build());
    logger.info("zero assigned ids:" + assigned.getStartId() + " - " + assigned.getEndId());

  }

  public static void main(final String[] args) {
     ZeroClient client = new ZeroClient("172.20.0.14:5080");
     client.freeLeaseUidInZero(1000);
  }
}

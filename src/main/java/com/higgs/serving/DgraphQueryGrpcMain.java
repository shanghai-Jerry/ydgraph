package com.higgs.serving;

import java.nio.file.Files;
import java.nio.file.Paths;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

/**
 * User: JerryYou
 *
 * Date: 2018-06-25
 *
 * Copyright (c) 2018 devops
 *
 * <<licensetext>>
 */
public class DgraphQueryGrpcMain {
  private static final Logger LOGGER = LoggerFactory.getLogger(DgraphQueryGrpcMain.class);

  public static void main(String[] args) throws Exception {
    if (args.length < 1) {
      System.err.println("Usage: DgraphQueryGrpcMain <Conf>");
      System.exit(-1);
    }
    String configStr = new String(Files.readAllBytes(Paths.get(args[0])));
    JsonObject config = new JsonObject(configStr);
    String eserver = config.getString("eserver", "172.20.0.14:26544");
    String dserver = config.getString("dserver", "172.20.0.14:9080");
    String queryPath = config.getString("queryPath", ".");
    int port = config.getInteger("port", 26549);
    DgraphQueryGrpcServing dgraphQueryGrpcServing = new DgraphQueryGrpcServing(eserver, dserver,
        queryPath);
    LOGGER.info("listen port:" + port + ", config:" + configStr);
    Server server = ServerBuilder.forPort(port)
        .addService(dgraphQueryGrpcServing).build();
    server.start();
    server.awaitTermination();

  }
}

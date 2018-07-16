package com.higgs.serving.dept;

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
 * Date: 2018-07-16
 *
 * Copyright (c) 2018 devops
 *
 * <<licensetext>>
 */
public class DeptNormGrpcMain {

  private static final Logger LOGGER = LoggerFactory.getLogger(DeptNormGrpcMain.class);

  public static void main(String[] args) throws Exception {
    if (args.length < 1) {
      System.err.println("Usage: DeptNormGrpcMain <Conf>");
      System.exit(-1);
    }
    String configStr = new String(Files.readAllBytes(Paths.get(args[0])));
    JsonObject config = new JsonObject(configStr);
    String dictPath = config.getString("dictPath", ".");
    int port = config.getInteger("port", 26549);
    DeptNormGrpcServing deptNormGrpcServing = new DeptNormGrpcServing(dictPath);
    LOGGER.info("listen port:" + port + ", config:" + configStr);
    Server server = ServerBuilder.forPort(port)
        .addService(deptNormGrpcServing).build();
    server.start();
    server.awaitTermination();

  }
}

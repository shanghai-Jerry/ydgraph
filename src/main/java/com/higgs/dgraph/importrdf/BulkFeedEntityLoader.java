package com.higgs.dgraph.importrdf;


import com.higgs.client.EntityIdClient;
import com.higgs.dgraph.node.NodeUtil;
import com.higgs.utils.TimeUtil;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicLong;
import java.util.zip.GZIPInputStream;

import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

/**
 * User: JerryYou
 *
 * Date: 2018-07-11
 *
 * Copyright (c) 2018 devops
 *
 * <<licensetext>>
 */

public class BulkFeedEntityLoader {

  private static Logger logger = LoggerFactory.getLogger(BulkFeedEntityLoader.class);
  private EntityIdClient entityIdClient;
  private AtomicLong totalTime = new AtomicLong(0);
  private AtomicLong counter = new AtomicLong(0);

  ExecutorService executor;
  ExecutorCompletionService executorCompletionService;

  public BulkFeedEntityLoader(String serverAddress, int numberThread) {
    entityIdClient = new EntityIdClient(serverAddress);
    executor = Executors.newFixedThreadPool(numberThread);
    executorCompletionService = new ExecutorCompletionService(executor);
  }

  public void loading(List<String> rdf) {
    executorCompletionService.submit(new DataHandlerCallable(entityIdClient, NodeUtil.deepCopy
        (rdf)));
  }

  private void handleResult(int tasks, int batchSize) {
    long started = System.currentTimeMillis();
    for (int i = 0; i < tasks; i++) {
      try {
        Future<Long> success = executorCompletionService.take();
        long ret = success.get();
        long newCounter = counter.addAndGet(ret);
        counter.set(newCounter);
      } catch (InterruptedException e) {
        logger.info("[ExecutorCompletionService Take error] -> " + e.getMessage());
      } catch (ExecutionException e) {
        logger.info("[ExecutorCompletionService Future Get error] -> " + e.getMessage());
      }
    }
    long totalData = counter.get();
    long end = System.currentTimeMillis();
    long time = end - started;
    long total = totalTime.addAndGet(time);
    totalTime.set(total);
    if (totalData % (batchSize * 1000L) == 0) {
      logger.info("total spend:" + TimeUtil.consumeTime(totalTime.get()) + ", totalCount:" +
          totalData);
    }
  }

  public void processFiles(String rdfDir, int batchSize, int numberThread) throws IOException {
    File dirFile = new File(rdfDir);
    if (dirFile.isDirectory()) {
      File[] files = dirFile.listFiles();
      for (File file : files) {
        processFile(file.getPath(), batchSize, numberThread);
      }
    } else {
      processFile(dirFile.getAbsolutePath(), batchSize, numberThread);
    }
  }

  public void processFile(String rdfDir, int batchSize, int numberThread) throws IOException {
    int batch = 0;
    int tasks = 0;
    List<String> rdf = new ArrayList<>();
    if (rdfDir.endsWith("rdf.gz")) {
      GZIPInputStream gzipInputStream = new GZIPInputStream(new FileInputStream(new File(rdfDir)));
      Scanner sc = new Scanner(gzipInputStream);
      while (sc.hasNextLine()) {
        String line = sc.nextLine();
        if (line.contains("<unique_id>")) {
          rdf.add(line);
          batch++;
        }
        if (batch >= batchSize) {
          tasks++;
          loading(rdf);
          if (tasks % numberThread == 0) {
            handleResult(tasks, batchSize);
            tasks = 0;
          }
          rdf.clear();
          batch = 0;
        }
      }
      if (batch > 0) {
        tasks++;
        loading(rdf);
      }
      handleResult(tasks, batchSize);

    } else if (rdfDir.endsWith(".rdf")) {
      BufferedReader bufferedReader = new BufferedReader(new FileReader(new File(rdfDir)));
      String line = bufferedReader.readLine();
      while (line != null) {
        if (line.contains("<unique_id>")) {
          rdf.add(line);
          batch++;
        }
        if (batch >= batchSize) {
          tasks++;
          loading(rdf);
          if (tasks % numberThread == 0) {
            handleResult(tasks, batchSize);
            tasks = 0;
          }
          rdf.clear();
          batch = 0;
        }
        line = bufferedReader.readLine();
      }
      if (batch > 0) {
        tasks++;
        loading(rdf);
      }
      handleResult(tasks, batchSize);
    }
    executor.shutdown();
    logger.info(rdfDir + " => total tasks:" + tasks);
  }


  public static void main(String[] args) {

    if (args.length < 4) {
      System.out.println("Usage: java -jar xxx.jar <RDFDir: *.rdf.gz or *.rdf> " +
          "<EntityServer> <BatchSize> <NumberThread> ");
      return;
    }
    String rdfDir = args[0];
    String eserver = args[1];
    int batchSize = Integer.parseInt(args[2]);
    int numberThread = Integer.parseInt(args[3]);
    logger.info("eserver:" + eserver + ", rdf:" + rdfDir + ", batchSize:" + batchSize);
    BulkFeedEntityLoader bulkFeedEntityLoader = new BulkFeedEntityLoader(eserver, numberThread);
    try {
      bulkFeedEntityLoader.processFiles(rdfDir, batchSize, numberThread);
    } catch (IOException e) {
      logger.info("[BulkFeedEntityLoader] => " + e.getMessage());
    }

  }
}
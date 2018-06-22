package com.higgs.dgraph.export;


import com.higgs.dgraph.DClient;

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
 * Date: 2018-06-21
 *
 * Copyright (c) 2018 devops
 *
 * <<licensetext>>
 */
public class BulkLoader {

  private static final Logger logger = LoggerFactory.getLogger(BulkLoader.class);
  private DClient dClient;

  private AtomicLong counter = new AtomicLong(0);

  ExecutorService executor = Executors.newFixedThreadPool(5);
  ExecutorCompletionService executorCompletionService = new ExecutorCompletionService(executor);

  public BulkLoader(String serverAddress) {
    String[] strings = new String[]{serverAddress};
    dClient = new DClient(strings);
  }

  public void loading(List<String> rdf) {
    executorCompletionService.submit(new DataHandlerCallable(dClient, rdf));
  }

  public int processFile(String rdfDir, int batchSize) throws IOException {
    int batch = 0;
    int tasks = 0;
    List<String> rdf = new ArrayList<>();
    if (rdfDir.endsWith("rdf.gz")) {
      GZIPInputStream gzipInputStream =  new GZIPInputStream(new FileInputStream(new File(rdfDir)));
      Scanner sc = new Scanner(gzipInputStream);
      while(sc.hasNextLine()) {
        String line = sc.nextLine();
        rdf.add(line + "\n");
        batch++;
        if (batch >= batchSize) {
          tasks++;
          loading(rdf);
          rdf.clear();
          batch = 0;
        }
      }
      if (batch > 0) {
        tasks++;
        loading(rdf);
      }
    } else if (rdfDir.endsWith(".rdf")) {
      BufferedReader bufferedReader = new BufferedReader(new FileReader(new File(rdfDir)));
      String line = bufferedReader.readLine();
      while (line != null) {
        rdf.add(line + "\n");
        batch++;
        if (batch >= batchSize) {
          tasks++;
          loading(rdf);
          rdf.clear();
          batch = 0;
        }
        line = bufferedReader.readLine();
      }
      if (batch > 0) {
        tasks++;
        loading(rdf);
      }
    }
    executor.shutdown();
    return tasks;
  }

  public static void main(String[] args) throws IOException {
    if (args.length < 4) {
      System.out.println("Usage: java -jar xxx.jar <RDFDir: *.rdf.gz or *.rdf> <SchemaFile> " +
          "<DgserverAddress> <BatchSize> ");
      return;
    }
    String rdfDir = args[0];
    String dserver = args[2];
    int batchSize = Integer.parseInt(args[3]);
    logger.info("dserver:" + dserver + ", rdf:" + rdfDir + ", batchSize:" + batchSize);
    BulkLoader bulkLoader = new BulkLoader(dserver);
    long started = System.currentTimeMillis();
    int tasks = bulkLoader.processFile(rdfDir, batchSize);
    for (int i = 0; i < tasks; i++) {
      try {
        Future<Long> success = bulkLoader.executorCompletionService.take();
        long ret = success.get();
        bulkLoader.counter.addAndGet(ret);
      } catch (InterruptedException e) {
        logger.info("[ExecutorCompletionService Take error] -> " + e.getMessage());
      } catch (ExecutionException e) {
        logger.info("[ExecutorCompletionService Future Get error] -> " + e.getMessage());
      }
      long end = System.currentTimeMillis();
      logger.info("spend:" + (end - started) + "ms, totalCount:" + bulkLoader.counter.get());
    }
  }

}

package com.higgs.dgraph.bulk;

import com.higgs.dgraph.DClient;
import com.higgs.dgraph.node.NodeUtil;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;
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
  private AtomicLong totalTime = new AtomicLong(0);
  private AtomicBoolean isFull = new AtomicBoolean(false);

  ExecutorService executor;
  ExecutorCompletionService executorCompletionService;

  public BulkLoader(String serverAddress, int numberThread) {
    String[] strings = new String[]{serverAddress};
    dClient = new DClient(strings);
    executor = Executors.newFixedThreadPool(numberThread);
    executorCompletionService = new ExecutorCompletionService(executor);

  }

  private String consumeTime(long totalMills) {
    SimpleDateFormat formatter = new SimpleDateFormat("mm分:ss秒:S");
    String hms = formatter.format(totalMills);
    return hms;
  }

  public void loading(List<String> rdf) {
    executorCompletionService.submit(new DataHandlerCallable(dClient, NodeUtil.deepCopy(rdf)));
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
      logger.info("total spend:" + consumeTime(total) + " ms, totalCount:" + totalData);
    }
  }

  private void lock() {
    isFull.set(true);
  }

  private void unlock() {
    isFull.set(false);
  }
  public int processFile(String rdfDir, int batchSize, int numberThread) throws IOException {
    int batch = 0;
    int tasks = 0;
    List<String> rdf = new ArrayList<>();
    if (rdfDir.endsWith("rdf.gz")) {
      // TODO ... 存在编码的读取问题
      GZIPInputStream gzipInputStream =  new GZIPInputStream(new FileInputStream(new File(rdfDir)));
      Scanner sc = new Scanner(gzipInputStream);
      while(sc.hasNextLine()) {
        String line = sc.nextLine();
        rdf.add(line + "\n");
        batch++;
        if (batch >= batchSize) {
          tasks++;
          loading(rdf);
          if (tasks % numberThread == 0) {
            logger.info("task full, pls wait !!");
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
        rdf.add(line + "\n");
        batch++;
        if (batch >= batchSize) {
          tasks++;
          loading(rdf);
          if (tasks % numberThread == 0) {
            logger.info("task full, pls wait !!");
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
      handleResult(tasks,batchSize);
    }
    executor.shutdown();
    return tasks;
  }

  public static void main(String[] args) throws IOException {
    if (args.length < 5) {
      System.out.println("Usage: java -jar xxx.jar <RDFDir: *.rdf.gz or *.rdf> <SchemaFile> " +
          "<DgserverAddress> <BatchSize> <NumberThread> ");
      return;
    }
    String rdfDir = args[0];
    String dserver = args[2];
    int batchSize = Integer.parseInt(args[3]);
    int numberThread = Integer.parseInt(args[4]);
    logger.info("dserver:" + dserver + ", rdf:" + rdfDir + ", batchSize:" + batchSize);
    BulkLoader bulkLoader = new BulkLoader(dserver, numberThread);
    bulkLoader.processFile(rdfDir, batchSize, numberThread);
  }

}

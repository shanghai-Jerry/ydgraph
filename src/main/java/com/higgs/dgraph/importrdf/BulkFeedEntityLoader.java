package com.higgs.dgraph.importrdf;


import com.higgs.client.EntityIdClient;
import com.higgs.dgraph.node.NodeUtil;
import com.higgs.utils.TimeUtil;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
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
  private AtomicLong totalTask = new AtomicLong(0);
  private AtomicLong finishedTask = new AtomicLong(0);
  private int epoch = 10;
  private int batchSize = 2000;
  private int numberThread = 10;
  private double threshold = 0.90;

  ExecutorService executor;
  ExecutorCompletionService executorCompletionService;

  public BulkFeedEntityLoader(String serverAddress, int numberThread, int batch, int epoch, double threshold) {
    entityIdClient = new EntityIdClient(serverAddress);
    executor = Executors.newFixedThreadPool(numberThread * 2);
    executorCompletionService = new ExecutorCompletionService(executor);
    this.batchSize = batch;
    this.epoch = epoch;
    this.threshold = threshold;
    this.numberThread = numberThread;
  }

  public void loading(List<String> rdf) {
    executorCompletionService.submit(new DataHandlerCallable(entityIdClient, NodeUtil.deepCopy
        (rdf)));
  }

  private void handleResult(long tasks, int batchSize, double threshold) {
    long started = System.currentTimeMillis();
    for (int i = 0; i < tasks; i++) {
      try {
        Future<Long> success = executorCompletionService.poll();
        if (success != null) {
          long ret = success.get();
          long newCounter = counter.addAndGet(ret);
          counter.set(newCounter);
          long runed = finishedTask.addAndGet(1);
          finishedTask.set(runed);
        }
      } catch (InterruptedException e) {
        logger.info("[ExecutorCompletionService Take error] -> " + e.getMessage());
      } catch (ExecutionException e) {
        logger.info("[ExecutorCompletionService Future Get error] -> " + e.getMessage());
      }
    }
    // 线程池占用率超过threshold， 使用阻塞
    long totalTs = totalTask.get();
    long finishedTs = finishedTask.get();
    long rest = totalTs - finishedTs;
    double per = rest * 1.0 / (this.numberThread * 2);
    if (threshold == 1) {
      for (int i = 0; i < rest; i++) {
        try {
          Future<Long> success = executorCompletionService.take();
          long ret = success.get();
          long newCounter = counter.addAndGet(ret);
          counter.set(newCounter);
          long runed = finishedTask.addAndGet(1);
          finishedTask.set(runed);
        } catch (InterruptedException e) {
          logger.info("[ExecutorCompletionService Take error] -> " + e.getMessage());
        } catch (ExecutionException e) {
          logger.info("[ExecutorCompletionService Future Get error] -> " + e.getMessage());
        }
      }
    } else if (per > threshold) {
      int number = 0;
      double needFetchNumber = rest * 0.5;
      logger.info("over threshold rest =>" + rest + ", per:" + per);
      while (number <= needFetchNumber) {
        try {
          Future<Long> success = executorCompletionService.take();
          long ret = success.get();
          long newCounter = counter.addAndGet(ret);
          counter.set(newCounter);
          long runed = finishedTask.addAndGet(1);
          finishedTask.set(runed);

        } catch (InterruptedException e) {
          e.printStackTrace();
        } catch (ExecutionException e) {
          e.printStackTrace();
        }
        totalTs = totalTask.get();
        finishedTs = finishedTask.get();
        rest = totalTs - finishedTs;
        number = number + 1;
      }
      logger.info("exit with threshold rest => " + rest);
    }
    long totalData = counter.get();
    long end = System.currentTimeMillis();
    long time = end - started;
    long total = totalTime.addAndGet(time);
    totalTime.set(total);
    if (totalData % (batchSize * this.epoch) == 0) {
      logger.info("total spend:" + TimeUtil.consumeTime(totalTime.get()) + ", totalCount:" +
          totalData + ", totalTask:" + totalTask.get() + ", finishedTask:" + finishedTask.get());
    }
  }

  public void processFiles(String rdfDir, int batchSize, int numberThread) throws IOException {
    File dirFile = new File(rdfDir);
    if (dirFile.isDirectory()) {
      File[] files = dirFile.listFiles();
      for (File file : files) {
        processFile(file.getPath());
      }
    } else {
      processFile(dirFile.getAbsolutePath());
    }
  }

  public void processFile(String rdfDir) throws IOException {
    int batch = 0;
    List<String> rdf = new ArrayList<>();
    if (rdfDir.endsWith("rdf.gz")) {
      // TODO ... 存在编码的读取问题
      GZIPInputStream gzipInputStream = new GZIPInputStream(new FileInputStream(new File(rdfDir)));
      InputStreamReader isr = new InputStreamReader(gzipInputStream, "UTF-8");
      BufferedReader bufferedReader = new BufferedReader(isr);
      String line = bufferedReader.readLine();
      while (line != null) {
        if (line.contains("<unique_id>")) {
          rdf.add(line);
          batch++;
        }
        if (batch >= this.batchSize) {
          long ts = totalTask.addAndGet(1);
          totalTask.set(ts);
          loading(rdf);
          handleResult(this.numberThread, this.batchSize, this.threshold);
          rdf.clear();
          batch = 0;
        }
      }
      if (batch > 0) {
        long ts = totalTask.addAndGet(1);
        totalTask.set(ts);
        loading(rdf);
      }
      // check running task, make sure all task finished
      long total = totalTask.get();
      long finished = finishedTask.get();
      long restTask = total - finished;
      handleResult(restTask, this.batchSize, 1);
    } else if (rdfDir.endsWith(".rdf")) {
      BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(rdfDir),
          "UTF-8"));
      String line = bufferedReader.readLine();
      while (line != null) {
        if (line.contains("<unique_id>")) {
          rdf.add(line);
          batch++;
        }
        if (batch >= batchSize) {
          long ts = totalTask.addAndGet(1);
          totalTask.set(ts);
          loading(rdf);
          handleResult(this.numberThread, this.batchSize, this.threshold);
          rdf.clear();
          batch = 0;
        }
        line = bufferedReader.readLine();
      }
      if (batch > 0) {
        long ts = totalTask.addAndGet(1);
        totalTask.set(ts);
        loading(rdf);
      }
      // check running task, make sure all task finished
      long total = totalTask.get();
      long finished = finishedTask.get();
      long restTask = total - finished;
      handleResult(restTask, this.batchSize, 1);
    }
    executor.shutdown();
    logger.info("total spend:" + TimeUtil.consumeTime(totalTime.get()) + ", totalCount:" + counter.get());
    logger.info(rdfDir + " => totaTasks:" + totalTask.get() + ", finished:" + finishedTask.get());
  }


  public static void main(String[] args) {

    if (args.length < 6) {
      System.out.println("Usage: java -jar xxx.jar <RDFDir: *.rdf.gz or *.rdf> " +
          "<EntityServer> <BatchSize> <NumberThread> <Epoch> <threshold>");
      return;
    }
    String rdfDir = args[0];
    String eserver = args[1];
    int batchSize = Integer.parseInt(args[2]);
    int numberThread = Integer.parseInt(args[3]);
    int epoch = Integer.parseInt(args[4]);
    double threshold = Double.parseDouble(args[5]);
    logger.info("eserver:" + eserver + ", rdf:" + rdfDir + ", batchSize:" + batchSize + ",numberThread:" + numberThread + ",epoch:" + epoch + ",threshold:" + threshold);
    BulkFeedEntityLoader bulkFeedEntityLoader = new BulkFeedEntityLoader(eserver, numberThread, batchSize, epoch, threshold);
    try {
      bulkFeedEntityLoader.processFile(rdfDir);
    } catch (IOException e) {
      logger.info("[BulkFeedEntityLoader] => " + e.getMessage());
    }

  }

}

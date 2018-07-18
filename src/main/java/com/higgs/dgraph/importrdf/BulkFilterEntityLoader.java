package com.higgs.dgraph.importrdf;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

/**
 * User: JerryYou
 *
 * Date: 2018-07-11
 *
 * Copyright (c) 2018 devops
 *
 * 过滤处理rdf文件中出现的乱码
 *
 * <<licensetext>>
 */

public class BulkFilterEntityLoader {

  private static Logger logger = LoggerFactory.getLogger(BulkFilterEntityLoader.class);
  private int batchSize;
  private String outDir;

  public BulkFilterEntityLoader(int batSize, String outDir) {
    this.batchSize = batSize;
    this.outDir = outDir;
  }
  public void processFiles(String rdfDir) throws IOException {
    File dirFile = new File(rdfDir);
    if (dirFile.isDirectory()) {
      File[] files = dirFile.listFiles();
      for (File file : files) {
        processFile(file.getAbsolutePath());
      }
    } else {
      processFile(dirFile.getAbsolutePath());
    }
  }

  public void writeFile(List<String> rdf, BufferedWriter bufferedWriter) {
    try {
      for (String line : rdf) {
        bufferedWriter.write(line + "\n");
      }
      bufferedWriter.flush();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private String handleText(String line) {
    return line.replace("\\x", "\\u00");
  }

  private boolean needFilter(String line) {
    if (line.contains("\\")) {
      return true;
    }
    return false;
  }

  public void processFile(String rdfDir) throws IOException {
    int batch = 0;
    String outFile = this.outDir + "/" + new File(rdfDir).getName();
    Scanner sc = null;
    BufferedWriter bufferedWriter = null;
    List<String> rdf = new ArrayList<>();
    if (rdfDir.endsWith("rdf.gz")) {
      try {
        bufferedWriter = new BufferedWriter(new OutputStreamWriter(new GZIPOutputStream(new
            FileOutputStream(outFile)),"UTF-8"));
        // TODO ... 存在编码的读取问题
        GZIPInputStream gzipInputStream = new GZIPInputStream((new FileInputStream(rdfDir)));
        sc = new Scanner(gzipInputStream);
        while (sc.hasNextLine()) {
          String line = sc.nextLine();
          if (needFilter(line)) {
            continue;
          }
          rdf.add(handleText(line));
          batch++;
          if (batch >= this.batchSize) {
            writeFile(rdf, bufferedWriter);
            rdf.clear();
            batch = 0;
          }
        }
        if (batch > 0) {
          writeFile(rdf, bufferedWriter);
        }
      } catch (IOException e) {
        e.printStackTrace();
      } finally {
        if (sc != null) {
          sc.close();
        }
        if (bufferedWriter != null) {
          bufferedWriter.close();
        }
      }

    } else if (rdfDir.endsWith(".rdf")) {
      BufferedReader bufferedReader = null;
      try {
        bufferedWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outFile), "UTF-8"));
        bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(rdfDir),
            "UTF-8"));
        String line = bufferedReader.readLine();
        while (line != null) {
          if (needFilter(line)) {
            line = bufferedReader.readLine();
            continue;
          }
          rdf.add(handleText(line));
          batch++;
          if (batch >= batchSize) {
            writeFile(rdf, bufferedWriter);
            rdf.clear();
            batch = 0;
          }
          line = bufferedReader.readLine();
        }
        if (batch > 0) {
          writeFile(rdf, bufferedWriter);
        }
      } catch (IOException e) {
        e.printStackTrace();
      } finally {
        if (bufferedReader != null) {
          bufferedReader.close();
        }
        if (bufferedWriter != null) {
          bufferedWriter.close();
        }
      }
    }
    logger.info(rdfDir + " == finished!!");
  }


  public static void main(String[] args) {

    if (args.length < 3) {
      System.out.println("Usage: java -jar xxx.jar <RDFDir: *.rdf.gz or *.rdf> <BatchSize> <OutDir> ");
      return;
    }
    String rdfDir = args[0];
    int batchSize = Integer.parseInt(args[1]);
    String outDir = args[2];
    logger.info("rdf:" + rdfDir + ", batchSize:" + batchSize);
    BulkFilterEntityLoader bulkFilterEntityLoader = new BulkFilterEntityLoader(batchSize, outDir);
    try {
      bulkFilterEntityLoader.processFiles(rdfDir);
    } catch (IOException e) {
      logger.info("[BulkFilterEntityLoader] => " + e.getMessage());
    }

  }
}

package com.higgs.dgraph.export;

import com.higgs.dgraph.DClient;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.zip.GZIPInputStream;

import io.dgraph.DgraphProto;
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

  private long errorNumber = 0;

  private long successedEdges = 0;

  public BulkLoader(String[] serverAddrs) {
    dClient = new DClient(serverAddrs);
  }

  public void formatRdf(List<String> rdfs, List<String> formatRdfs) {
    for (String rdf : rdfs) {
      formatRdfs.add(rdf.replace("_:uid", "0x"));
    }
  }

  public void loading(List<String> rdfs) {
    long started = System.currentTimeMillis();
    List<String> formatRdfs = new ArrayList<>();
    formatRdf(rdfs, formatRdfs);
    DgraphProto.Assigned assigned = dClient.multiplyEdgesMutation(formatRdfs, false);
    if (assigned == null) {
      errorNumber = errorNumber + formatRdfs.size();
    } else {
      successedEdges = successedEdges + formatRdfs.size();
    }
    long end = System.currentTimeMillis();
    logger.info("successedEdges:" + successedEdges + ", errorNumber:" + errorNumber + ", spend:"
        + (end - started) + " ms/" + formatRdfs.size());
  }

  public static void main(String[] args) throws IOException {
    if (args.length < 4) {
      System.out.println("Usage: java -jar xxx.jar <RDFDir> <SchemaFile> <DgserverAddress> " +
          "<BatchSize> ");
      return;
    }
    String rdfDir = args[0];
    String dserver = args[2];
    logger.info("dserver:" + dserver);
    BulkLoader bulkLoader = new BulkLoader(dserver.split(","));
    int batchSize = Integer.parseInt(args[3]);
    int batch = 0;
    GZIPInputStream gzipInputStream =  new GZIPInputStream(new FileInputStream(new File(rdfDir)));
    Scanner sc = new Scanner(gzipInputStream);
    List<String> rdf = new ArrayList<>();
    while(sc.hasNextLine()) {
      String line = sc.nextLine();
      rdf.add(line + "\n");
      batch++;
      if (batch >= batchSize) {
        bulkLoader.loading(rdf);
        rdf.clear();
        batch = 0;
      }
    }
    if (batch > 0) {
      bulkLoader.loading(rdf);
    }
  }
}

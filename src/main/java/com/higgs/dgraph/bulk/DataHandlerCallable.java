package com.higgs.dgraph.bulk;

import com.higgs.dgraph.DClient;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import io.dgraph.bigchange.DgraphProto;
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
public class DataHandlerCallable implements Callable<Long> {

  private List<String> data;

  private DClient dClient;

  private long errorNumber = 0;

  private long successedEdges = 0;

  private static final Logger logger = LoggerFactory.getLogger(DataHandlerCallable.class);

  public DataHandlerCallable(DClient dClient, List<String> data) {
    this.dClient = dClient;
    this.data = data;
  }

  private void formatRdf(List<String> rdfs, List<String> formatRdfs) {
    for (String rdf : rdfs) {
      formatRdfs.add(rdf.replace("_:uid", "0x"));
    }
  }

  private long processor() {
    long started = System.currentTimeMillis();
    List<String> formatRdf = new ArrayList<>();
    formatRdf(this.data, formatRdf);
    int size = formatRdf.size();
    DgraphProto.Assigned assigned = dClient.multiplyEdgesMutation(formatRdf, true);
    if (assigned == null) {
      errorNumber = errorNumber + size;
    } else {
      successedEdges = successedEdges + size;
    }
    long end = System.currentTimeMillis();
    logger.info("map:" + Thread.currentThread().getId() + ", successedEdges:" + successedEdges +
        ", errorNumber:" + errorNumber + ", spend:" + (end - started) + " ms/" + size);
    this.data.clear();
    formatRdf.clear();
    return successedEdges;
  }
  @Override
  public Long call() throws Exception {
    return processor();
  }
}

package com.higgs.dgraph.bulk;

/**
 * User: JerryYou
 *
 * Date: 2018-06-22
 *
 * Copyright (c) 2018 devops
 *
 * <<licensetext>>
 */
public class BulkLoaderOptions {

  String rdfDir;
  String schemaFile;
  String serverAddr;
  String batchSize;
  String numberThread;

  public BulkLoaderOptions(String rdfDir, String schemaFile, String serverAddr, String batchSize,
                           String numberThread) {
    this.rdfDir = rdfDir;
    this.schemaFile = schemaFile;
    this.serverAddr = serverAddr;
    this.batchSize = batchSize;
    this.numberThread = numberThread;
  }

  public String getRdfDir() {
    return rdfDir;
  }

  public void setRdfDir(String rdfDir) {
    this.rdfDir = rdfDir;
  }

  public String getSchemaFile() {
    return schemaFile;
  }

  public void setSchemaFile(String schemaFile) {
    this.schemaFile = schemaFile;
  }

  public String getServerAddr() {
    return serverAddr;
  }

  public void setServerAddr(String serverAddr) {
    this.serverAddr = serverAddr;
  }

  public String getBatchSize() {
    return batchSize;
  }

  public void setBatchSize(String batchSize) {
    this.batchSize = batchSize;
  }

  public String getNumberThread() {
    return numberThread;
  }

  public void setNumberThread(String numberThread) {
    this.numberThread = numberThread;
  }
}

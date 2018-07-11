package com.higgs.dgraph.importrdf;

import com.higgs.client.EntityIdClient;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;

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
public class DataHandlerCallable implements Callable<Long> {

  private List<String> data;

  private int batchSize = 0;

  private EntityIdClient entityIdClient;

  private long errorNumber = 0;

  private long successedEdges = 0;

  private static Logger logger = LoggerFactory.getLogger(DataHandlerCallable.class);

  public DataHandlerCallable(EntityIdClient entityIdClient, List<String> data) {
    this.entityIdClient = entityIdClient;
    this.data = data;
    // this.batchSize = batchSize;
  }

  private void formatRdf(List<String> rdfs, Map<String, Map<String, String>> formatMap) {

    for (String info : rdfs) {
      String[] sp = info.split("<unique_id>");
      if (sp.length != 2) {
        continue;
      }
      String uid = "0x" + info.substring(info.indexOf("<") + 6, info.indexOf(">"));
      String value = sp[1]
          .replace("\"", "")
          .trim();
      value = value.substring(0, value.indexOf("^^"));
      int index = value.indexOf(":");
      String type = value.substring(0, index);
      String unique_id = value.substring(index + ":".length());
      if (formatMap.containsKey(type)) {
        Map<String, String> valueMap = formatMap.get(type);
        if (valueMap.containsKey(uid)) {
          logger.info("WFK, it's not possible!!");
        } else {
          valueMap.put(uid, unique_id);
        }
      } else {
        Map<String, String> valueMap = new HashMap<>();
        valueMap.put(uid, unique_id);
        formatMap.put(type, valueMap);
      }
    }
  }

  private void feedUidMap(Map<String, Map<String, String>> formatMap) {
    Set<Map.Entry<String, Map<String, String>>> entrySet = formatMap.entrySet();
    Iterator<Map.Entry<String, Map<String, String>>> iterator = entrySet.iterator();
    while (iterator.hasNext()) {
      Map.Entry<String, Map<String, String>> entry = iterator.next();
      String key = entry.getKey();
      Map<String, String> values = entry.getValue();
      batchPutEntityId(values, key);
      successedEdges = successedEdges + values.size();
    }
  }

  private void batchPutEntityId(Map<String, String> map, String type) {
    entityIdClient.putFeedEntityWithUidNameMap(map, type);
  }

  private long processor() {
    Map<String, Map<String, String>> formatMap = new HashMap<>();
    formatRdf(this.data, formatMap);
    int size = formatMap.size();
    if (size > 0) {
      feedUidMap(formatMap);
    }
    this.data.clear();
    formatMap.clear();
    return successedEdges;
  }

  @Override
  public Long call() {
    return processor();
  }
}

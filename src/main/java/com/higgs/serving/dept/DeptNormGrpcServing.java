package com.higgs.serving.dept;

import com.hankcs.algorithm.AhoCorasickDoubleArrayTrie;
import com.higgs.utils.FileUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import io.grpc.stub.StreamObserver;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import kb.rpc.dept.BatchDeptRequest;
import kb.rpc.dept.BatchDeptResponse;
import kb.rpc.dept.DeptNormServiceGrpc;
import kb.rpc.dept.DeptRequest;
import kb.rpc.dept.DeptResponse;

/**
 * User: JerryYou
 *
 * Date: 2018-07-16
 *
 * Copyright (c) 2018 devops
 *
 * <<licensetext>>
 */
public class DeptNormGrpcServing extends DeptNormServiceGrpc.DeptNormServiceImplBase {

  private static Logger logger = LoggerFactory.getLogger(DeptNormServiceGrpc.class);

  public DeptNormGrpcServing(String dictPath) {
    init(dictPath);
  }

  private AhoCorasickDoubleArrayTrie<String> acdat = new AhoCorasickDoubleArrayTrie<>();
  private TreeMap<String, String> map = new TreeMap<>();

  public void addKey(String item, String match) {
    map.put(item, match);
    acdat.build(map);
  }
  private void init(String dictPath) {
    List<String> dict = new ArrayList<>();
    FileUtils.readFile(dictPath, dict);
    for(String line : dict) {
      String []sp = line.split("\t");
      if (sp.length != 3) {
        logger.info("line length not equal 3 => " + line);
        continue;
      }
      String match = sp[1];
      String []items = sp[2].split(" ");
      for (String item : items) {
        if (!"".equals(item.trim())) {
          map.put(item.toLowerCase(), match);
        }
      }
      // match self
      map.put(match.toLowerCase(), match);
    }
    acdat.build(map);
  }

  public void demo(String text) {
    List<AhoCorasickDoubleArrayTrie.Hit<String>> wordList = acdat.parseText(text);
    for (AhoCorasickDoubleArrayTrie.Hit<String> word : wordList) {
      logger.info("matched:" + word.toString());
    }
  }

  private void success(StreamObserver<BatchDeptResponse> responseObserver, BatchDeptResponse respond) {
    responseObserver.onNext(respond);
    responseObserver.onCompleted();
  }

  @Override
  public void normDept(BatchDeptRequest request, StreamObserver<BatchDeptResponse>
      responseObserver) {
    // 没有匹配上的部门的，归类为其他
    List<DeptResponse> deptResponses = new ArrayList<>();
    List<DeptRequest> requests = request.getDeptReqList();
    for (DeptRequest req : requests) {
      String name = req.getName();
      DeptResponse.Builder builder = DeptResponse.newBuilder();
      if (name == null || "".equals(name)) {
        builder.setMsg("name must be set");
        builder.setOk(false);
        deptResponses.add(builder.build());
        continue;
      }
      List<AhoCorasickDoubleArrayTrie.Hit<String>> wordList = acdat.parseText(name);
      if (wordList.size() == 0) {
        builder.setOk(false);
        builder.setMsg("No Matched");
      } else {
        builder.setOk(true);
        String matchLongest = "";
        for (AhoCorasickDoubleArrayTrie.Hit<String> hit : wordList) {
          String matched = hit.value;
          if (matched.length() > matchLongest.length()) {
            matchLongest = matched;
          }
        }
        builder.setNormed(matchLongest);
      }
      deptResponses.add(builder.build());
    }
    BatchDeptResponse.Builder builder = BatchDeptResponse.newBuilder();
    builder.addAllDeptRes(deptResponses);
    success(responseObserver, builder.build());
  }



  public static void main(String[] args) {
    String dict = "/Users/devops/Documents/部门归一化/dept_dict.txt";
    DeptNormGrpcServing deptNormGrpcServing = new DeptNormGrpcServing(dict);
    deptNormGrpcServing.demo(" ");
  }

}

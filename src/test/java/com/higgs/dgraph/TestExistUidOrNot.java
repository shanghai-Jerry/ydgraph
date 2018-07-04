package com.higgs.dgraph;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.higgs.dgraph.node.Candidate;
import com.higgs.dgraph.query.CandidateQuery;
import io.dgraph.DgraphProto;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import com.higgs.utils.FileUtils;

/**
 * User: JerryYou
 *
 * Date: 2018-05-24
 *
 * Copyright (c) 2018 devops
 *
 * 检查put entity到dgraph中返回的uid是否成功dump到log中，可以恢复（尤其当server突然挂了的时候）
 *
 * <<licensetext>>
 */
public class TestExistUidOrNot {

  private static final Logger logger = LoggerFactory.getLogger(TestExistUidOrNot.class);
  DClient dClient = new DClient(Config.TEST_HOSTNAME);

  public void query(String query, List<String> uids, List<String> errorUids) {
    // 多个uid需要按uid排序，这样获取的结果顺序和uid list顺序一致
    Collections.sort(uids, new Comparator<String>() {
      @Override
      public int compare(String o1, String o2) {
        return o1.compareTo(o2);
      }
    });
    // Query
    StringBuilder stringBuilder = new StringBuilder();
    int index = 0;
    for (String uid : uids) {
      if (index == 0) {
        stringBuilder.append(uid);
      } else {
        stringBuilder.append("," + uid);
      }
      index = index + 1;
    }
    String queryFormat = String.format(query,stringBuilder.toString());
    // logger.info("uids:" + stringBuilder.toString());
    DgraphProto.Response res = dClient.getDgraphClient().newTransaction().query(queryFormat);
    // 获取时间
    // res.getLatency()
    CandidateQuery candidateQuery = new Gson().fromJson(res.getJson().toStringUtf8(),
        CandidateQuery.class);
    checkoutUid(candidateQuery, uids, errorUids);
    // Util.println("latency", res.getLatency().toString());
    // Util.println("resp", res.getJson().toStringUtf8());
    // Util.println("candidate", candidateQuery.getCandidates().get(0).getName());
  }

  public void checkoutUid(CandidateQuery candidateQuery, List<String> queryUids, List<String>
      errorUids) {
    List<Candidate> candidates = candidateQuery.getCandidates();
    if (candidates.size() != queryUids.size()) {
      logger.fatal("size not equal ... ");
      return;
    }
    for (int index = 0; index < candidates.size(); index++) {
      Candidate candidate = candidates.get(index);
      String queryUid = queryUids.get(index);
      String name = candidate.getName();
      String  uid = candidate.getUid();
      String gender = candidate.getGender();
      // logger.info("checkout:Name:" + name + ",uid:" + uid + ",queryUid:" + queryUid);
      if ((name == null && (gender == null)) || !queryUid.equals(uid) || "".equals
          (queryUid)) {
        errorUids.add(queryUid + "\t" + uid + "\t" + name + "\t" + gender);
      }
    }
  }
  public void queryDemo(String dir, String subDir) {

    String filePath;
    if ("".equals(subDir)) {
     filePath = "/Users/devops/workspace/shell/com.higgs.dgraph/" + dir;
     subDir = "uidmap";
    } else {
      filePath = "/Users/devops/workspace/shell/com.higgs.dgraph/" + dir + "/" + subDir;
    }
    String savePath = "src/main/resources/" + dir + "_none_exist_in_" + subDir + ".txt";
    String query = "{\ncandidates(func:uid(%s)) { \nname\n uid\n gender\n}\n}";
    FileUtils.deleteFile(savePath);
    List<String> dict = new ArrayList<>();
    List<String> errorUids = new ArrayList<>();
    FileUtils.readFiles(filePath, dict);
    int count = 0;
    int number = 0;
    List<String> uids = new ArrayList<>();
    for (String line : dict) {
      String[] lineSplits = line.split("\t");
      String names = lineSplits[0];
      String uid = lineSplits[1];
      uids.add(uid);
      count = count + 1;
      number = number + 1;
      if (number % 10000 == 0) {
        logger.info("number:" + number);
      }
      if (count >= 100) {
        query(query, uids, errorUids);
        FileUtils.saveFile(savePath, errorUids, true);
        count = 0;
        uids.clear();
        errorUids.clear();
      }
    }
    if (count > 0) {
      query(query, uids, errorUids);
      FileUtils.saveFile(savePath, errorUids, true);
    }
  }

  public static void main(String[] args) {
    TestExistUidOrNot testExistUidOrNot = new TestExistUidOrNot();
    String dir = "alpha_final_distinct_3";
    String subDir = "uidmap2";
    testExistUidOrNot.queryDemo(dir, subDir);

  }
}

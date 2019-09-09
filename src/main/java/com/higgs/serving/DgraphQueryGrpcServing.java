package com.higgs.serving;


import com.higgs.client.EntityIdClient;
import com.higgs.dgraph.DClient;
import com.higgs.dgraph.enumtype.EntityType;
import com.higgs.dgraph.node.NodeUtil;
import com.higgs.utils.FileUtils;
import com.higgs.utils.Util;
import com.inmind.idmg.serving.dgrpah.query.rpc.DgraphQueryGrpc;
import com.inmind.idmg.serving.dgrpah.query.rpc.QueryRequest;
import com.inmind.idmg.serving.dgrpah.query.rpc.QueryRespond;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.dgraph.bigchange.DgraphProto;
import io.grpc.stub.StreamObserver;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;


/**
 * User: JerryYou
 *
 * Date: 2018-06-25
 *
 * Copyright (c) 2018 devops
 *
 * <<licensetext>>
 */
public class DgraphQueryGrpcServing extends DgraphQueryGrpc.DgraphQueryImplBase {

  private static final Logger LOGGER = LoggerFactory.getLogger(DgraphQueryGrpcServing.class);
  private EntityIdClient entityIdClient;
  private DClient dClient;
  private String queryDir;
  private static Map<Integer, String> types = new HashMap<>();

  static {
    types.put(EntityType.COMPANY.getIndex(), EntityType.COMPANY.getName());
    types.put(EntityType.INDUSTRY.getIndex(), EntityType.INDUSTRY.getName());
    types.put(EntityType.CANDIDATE.getIndex(), EntityType.CANDIDATE.getName());
    types.put(EntityType.COMPANY_DEPT.getIndex(), EntityType.COMPANY_DEPT.getName());
    types.put(EntityType.MAJOR.getIndex(), EntityType.MAJOR.getName());
  }

  public DgraphQueryGrpcServing(String entityIdServer, String dgraphServer, String queryDir) {
    String[] eSplit = entityIdServer.split(":");
    this.entityIdClient = new EntityIdClient(eSplit[0], Integer.parseInt(eSplit[1]));
    this.dClient = new DClient(dgraphServer.split(","));
    if (new File(queryDir).exists()) {
      this.queryDir = queryDir;
    } else {
      LOGGER.info("Query dir not exits!! pls check settings");
      System.exit(-1);
    }
  }

  private void success(StreamObserver<QueryRespond> responseObserver, QueryRespond respond) {
    responseObserver.onNext(respond);
    responseObserver.onCompleted();
  }

  private void success(StreamObserver<QueryRespond> responseObserver, String resultJson) {
    success(responseObserver, resultJson, 0);
  }

  private void success(StreamObserver<QueryRespond> responseObserver, String resultJson, long
      processNs) {
    QueryRespond.Builder builder = QueryRespond.newBuilder();
    builder.setProcessingNs(processNs);
    if ("".equals(resultJson)) {
      responseObserver.onNext(builder.build());
    } else {
      builder.setResultJson(resultJson);
      responseObserver.onNext(builder.build());
    }
    responseObserver.onCompleted();
  }

  private DgraphProto.Response getResponse(String query) {
    DgraphProto.Response res = dClient.getDgraphClient()
        .newTransaction()
        .query(query);
    Util.parseLatency(res);
    return res;
  }

  private QueryRespond getReturnRespond(DgraphProto.Response response) {
    QueryRespond.Builder builder = QueryRespond.newBuilder();
    if (response == null) {
      return builder.build();
    }
    String json = response.getJson().toStringUtf8();
    long processNs = response.getLatency().getProcessingNs();
    builder.setResultJson(json).setProcessingNs(processNs);
    return builder.build();
  }

  @Deprecated
  private String getResultJson(String query) {
    DgraphProto.Response res = dClient.getDgraphClient()
        .newTransaction()
        .query(query);
    Util.parseLatency(res);
    return res.getJson().toStringUtf8();
  }

  @Deprecated
  private String getResultJson(String query, Map<String, String> vars) {
    DgraphProto.Response res = dClient.getDgraphClient()
        .newTransaction()
        .queryWithVars(query, vars);
    Util.parseLatency(res);
    return res.getJson().toStringUtf8();
  }

  @Deprecated
  public void queryDepre(QueryRequest request, StreamObserver<QueryRespond> responseObserver) {
    int queryType = request.getQueryTypeValue();
    String uniqueId = request.getUniqueId();
    int type = request.getUniqueIdTypeValue();
    String deptName = request.getDeptName();
    long pageSize = request.getPageSize();
    if (pageSize == 0) {
      pageSize = 10;
    }
    long offset = request.getPage() * pageSize;
    List<String> uidList = this.entityIdClient.getUidListWithName(Arrays.asList(uniqueId),
        types.getOrDefault(type, "NONE"));
    String resultJson = "";
    if (uidList.size() > 0) {
      String uid = uidList.get(0);
      String query = FileUtils.readQueryFile(this.queryDir + "/" + queryType + ".query");
      Map<String, String> vars = new HashMap<>();
      LOGGER.info(" [Query] => \n" + query);
      if ("".equals(query)) {
        success(responseObserver, resultJson, 0);
        return;
      }
      switch (queryType) {
        case QueryRequest.QueryType.COMPANY_DEPTNAMES_CANDIDATE_AGE_VALUE:
          vars.put("$a", uid);
          vars.put("$b", "1998");
          vars.put("$c", "1993");
          vars.put("$d", "1988");
          vars.put("$e", "1983");
          vars.put("$page_size", String.valueOf(pageSize));
          vars.put("$offset", String.valueOf(offset));
          resultJson = getResultJson(query, vars);
          break;
        case QueryRequest.QueryType.COMPANY_DEPTNAMES_CANDIDATE_MAX_DEGREE_VALUE:
          vars.put("$a", uid);
          vars.put("$page_size", String.valueOf(pageSize));
          vars.put("$offset", String.valueOf(offset));
          resultJson = getResultJson(query, vars);
          break;
        case QueryRequest.QueryType.COMPANY_DEPTNAMES_CANDIDATE_GENDER_VALUE:
          vars.put("$a", uid);
          vars.put("$page_size", String.valueOf(pageSize));
          vars.put("$offset", String.valueOf(offset));
          resultJson = getResultJson(query, vars);
          break;
        case QueryRequest.QueryType.COMPANY_DEPTNAMES_CANDIDATE_SALARY_VALUE:
          break;
        case QueryRequest.QueryType.COMPANY_DEPTNAMES_CANDIDATE_NUMBER_VALUE:
          vars.put("$a", uid);
          // vars.put("$page_size", String.valueOf(pageSize));
          // vars.put("$offset", String.valueOf(offset));
          resultJson = getResultJson(query, vars);
          break;
        case QueryRequest.QueryType.COMPANY_DEPTNAME_NUMBER_VALUE:
          vars.put("$a", uid);
          // vars.put("$page_size", String.valueOf(pageSize));
          // vars.put("$offset", String.valueOf(offset));
          resultJson = getResultJson(query, vars);
          break;
        case QueryRequest.QueryType.COMPANY_CANDIDATE_NUMBER_VALUE:
          vars.put("$a", uid);
          // vars.put("$page_size", String.valueOf(pageSize));
          // vars.put("$offset", String.valueOf(offset));
          resultJson = getResultJson(query, vars);
          break;
        case QueryRequest.QueryType.COMPANY_DEPTNAME_CANDIDATE_NUMBER_VALUE:
          if (!"".equals(deptName)) {
            List<String> deptNameUid = this.entityIdClient.getUidListWithName(
                Arrays.asList(NodeUtil.generateEntityUniqueId(uniqueId, deptName)), EntityType
                    .COMPANY_DEPT.getName());
            vars.put("$a", uid);
            if (deptNameUid.size() > 0) {
              vars.put("$b", deptNameUid.get(0));
            } else {
              LOGGER.info(" [check dept uid error ] ");
              break;
            }
            resultJson = getResultJson(query, vars);
          }
        case QueryRequest.QueryType.NONE_VALUE:
          break;
        default:
          break;
      }
      success(responseObserver, resultJson);
    } else {
      success(responseObserver, resultJson);
    }
  }

  private boolean checkValue(String value) {
    if (value == null || "".equals(value)) {
      return false;
    }
    return true;
  }

  @Override
  public void query(QueryRequest request, StreamObserver<QueryRespond> responseObserver) {
    QueryRespond queryRespond = QueryRespond.newBuilder().getDefaultInstanceForType();
    int queryType = request.getQueryTypeValue();
    String uniqueId = request.getUniqueId();
    int type = request.getUniqueIdTypeValue();
    String deptName = request.getDeptName();
    long pageSize = request.getPageSize();
    if (pageSize == 0) {
      pageSize = 10;
    }
    long offset = request.getPage() * pageSize;
    String resultJson = "";
    String queryFileName = queryType + ".query";
    String deptNameUid = "";
    String baseQuery;
    String query;
    List<String> uidList = this.entityIdClient.getUidListWithName(Arrays.asList(uniqueId),
        types.getOrDefault(type, "NONE"));
    if (uidList.size() == 0) {
      LOGGER.info(" [check entity uid error ] ");
      success(responseObserver, resultJson);
      return;
    }
    String uid = uidList.get(0);
    baseQuery = FileUtils.readQueryFile(this.queryDir + "/" + queryFileName);
    if (!"".equals(deptName)) {
      List<String> deptNameUidList = this.entityIdClient.getUidListWithName(
          Arrays.asList(NodeUtil.generateEntityUniqueId(uniqueId, deptName)), EntityType
              .COMPANY_DEPT.getName());
      if (deptNameUidList.size() == 0) {
        LOGGER.info(" [check dept uid error ] ");
        success(responseObserver, resultJson);
        return;
      }
      deptNameUid = deptNameUidList.get(0);
      query = String.format(baseQuery, uid, String.format("@filter(uid(%s))", deptNameUid), "");
    } else {
      query = String.format(baseQuery, uid, "", "");
    }
    LOGGER.info(" [Query] => \n" + query);
    if ("".equals(query)) {
      success(responseObserver, resultJson);
      return;
    }
    switch (queryType) {
      case QueryRequest.QueryType.COMPANY_DEPTNAMES_CANDIDATE_AGE_VALUE:
        queryRespond = getReturnRespond(getResponse(query));
        break;
      case QueryRequest.QueryType.COMPANY_DEPTNAMES_CANDIDATE_MAX_DEGREE_VALUE:
        queryRespond = getReturnRespond(getResponse(query));
        break;
      case QueryRequest.QueryType.COMPANY_DEPTNAMES_CANDIDATE_GENDER_VALUE:
        queryRespond = getReturnRespond(getResponse(query));
        break;
      case QueryRequest.QueryType.COMPANY_DEPTNAMES_CANDIDATE_SALARY_VALUE:
        if (!checkValue(deptName)) {
          success(responseObserver, "No deptName!!");
          return;
        }
        query = String.format(baseQuery, uid, String.format("@filter(uid(%s))", deptNameUid),
            String.format("@facets(eq(company_uid, \"%s\"))", deptNameUid));
        LOGGER.info("[Final Query] => " + query);
        queryRespond = getReturnRespond(getResponse(query));
        break;
      case QueryRequest.QueryType.COMPANY_DEPTNAMES_CANDIDATE_NUMBER_VALUE:
        queryRespond = getReturnRespond(getResponse(query));
        break;
      case QueryRequest.QueryType.COMPANY_DEPTNAME_NUMBER_VALUE:
        queryRespond = getReturnRespond(getResponse(query));
        break;
      case QueryRequest.QueryType.COMPANY_CANDIDATE_NUMBER_VALUE:
        queryRespond = getReturnRespond(getResponse(query));
        break;
      case QueryRequest.QueryType.COMPANY_DEPTNAME_CANDIDATE_NUMBER_VALUE:
        if (checkValue(deptName)) {
          queryRespond = getReturnRespond(getResponse(query));
        }
        break;
      case QueryRequest.QueryType.NONE_VALUE:
        break;
      default:
        break;
    }
    success(responseObserver, queryRespond);
  }
}

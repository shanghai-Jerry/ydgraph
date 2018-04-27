package dgraph;

import com.google.gson.Gson;
import com.google.protobuf.ByteString;
import com.google.protobuf.util.JsonFormat;

import com.sangupta.murmur.Murmur2;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.dgraph.DgraphGrpc;
import io.dgraph.DgraphProto;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

class  Person {
  String name;
  String age;
  String otherAttri;
  Person() {}
}

class People {
  List<Person> all;
  People() {}
}

public class DgraphClient {

  // produ client ip : 172.20.0.8
  // test client ip : 172.20.0.68
  private static final String TEST_HOSTNAME = "172.20.0.68";
  private static final int TEST_PORT = 9080;
  private static  HashMap<String, Integer> schoolDict = new HashMap<String, Integer>();

  private static JsonFormat.Parser parser = JsonFormat.parser();
  private static final long MURMUR_SEED = 0x7f3a21eaL;
  private static long generateMurMurHashId(String src) {
    byte[] bytes = src.getBytes();
    long murmurId = Murmur2.hash64(bytes, bytes.length, MURMUR_SEED);
    return murmurId;
  }

  private  int maxUid = 0;

  private io.dgraph.DgraphClient dgraphClient;

  public DgraphClient(String host, int port) {
    ManagedChannel channel =
        ManagedChannelBuilder.forAddress(host, port).usePlaintext(true).build();
    DgraphGrpc.DgraphBlockingStub blockingStub = DgraphGrpc.newBlockingStub(channel);
    dgraphClient = new io.dgraph.DgraphClient(Collections.singletonList(blockingStub));
  }

  private void dropSchema() {
    // Initialize
    dgraphClient.alter(DgraphProto.Operation.newBuilder().setDropAll(true).build());
  }

  private void readFile(String path, HashMap<String, Integer> map) {
    File file = new File(path);
    BufferedReader reader = null;
    try {
      reader = new BufferedReader(new FileReader(file));
      String tempString = null;
      int line = 0;
      // 一次读入一行，直到读入null为文件结束
      while ((tempString = reader.readLine()) != null) {
        String [] split = tempString.split("\t");
        map.put(split[2], Integer.valueOf(split[0].trim()));
      }
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void InitDict(String path) {
      readFile(path, schoolDict);
      System.out.println("size:" + schoolDict.size());
  }


  public void createSchema(String schema) {
    // DgraphProto.AssignedIds.newBuilder().setStartId(1).setEndId(80000).build();
    DgraphProto.Operation op = DgraphProto.Operation.newBuilder()
        .setSchema(schema).build();
    dgraphClient.alter(op);
  }

  public void mutation(List<String> jsons) {
    io.dgraph.DgraphClient.Transaction txn = dgraphClient.newTransaction();
    dgraphClient.newTransaction().discard();
    try {
      for (String json : jsons) {
        // Run mutation
        // System.out.println("json:" + json);
        DgraphProto.Mutation mu = DgraphProto.Mutation.newBuilder()
            .setSetJson(ByteString.copyFromUtf8(json.toString()))
            .build();
        txn.mutate(mu);
      }
      txn.commit();
    } finally {
      txn.discard();
    }
  }
  public void Query(String query,  Map<String, String> vars) {
    /*
    // Query
    String query =
        "query all($a: string){\n" + "all(func: eq(name, $a)) {\n" + "    name\n" + "  }\n" + "}";
    System.out.println("Query \n:" + query);
    Map<String, String> vars = Collections.singletonMap("$a", "ycj");
    application.Query(query, vars);
     */
    DgraphProto.Response res = dgraphClient.newTransaction().queryWithVars(query, vars);
    // Deserialize
    People ppl = new Gson().fromJson(res.getJson().toStringUtf8(), People.class);
    for (Person p : ppl.all) {
      System.out.println(p.name);
    }
  }

  private void fixJsonObject(JsonObject jsonObject, ArrayList<String> keys) {
    jsonObject.remove("tags");
    for (String key: keys) {
      jsonObject.remove(key);
    }
  }

  public void jsonObjectAddUid(JsonObject jsonObject) {
    JsonArray industries = jsonObject.getJsonArray("industries", new JsonArray());
    int industrieSzie = industries.size();
    for(int i = 0; i < industrieSzie; i++) {
      JsonObject dict = industries.getJsonObject(i);
      String title = dict.getString("title", "UNKNOW");
    }
    // pastworkExpr
    JsonArray workExprs = jsonObject.getJsonArray("pastWorkExperiences",  new JsonArray());
    int workExprSize = workExprs.size();
    for (int i = 0; i < workExprSize; i++) {
      JsonObject work = workExprs.getJsonObject(i);
      String jobTitle = work.getString("jobTitle", "UNKNOW");
      long jobTitleUid = generateMurMurHashId(jobTitle);
      JsonObject org = work.getJsonObject("org");
      String suggest = org.getString("suggest", "UNKNOW");
    }
    // edu
    JsonArray eduExprs = jsonObject.getJsonArray("educationExperiences",  new JsonArray());
    int eduExprSize = eduExprs.size();
    for (int i = 0; i < eduExprSize; i++) {
      JsonObject edu = eduExprs.getJsonObject(i);
      JsonObject school = edu.getJsonObject("school");
      String title = school.getString("title", "UNKNOW");

      int uid = 0;
      if (!title.equals("UNKNOW")) {
        if (schoolDict.containsKey(title)) {
          uid = schoolDict.get(title);
          if (uid > maxUid) {
            maxUid = uid;
          }
          school.put("uid",uid);
        } else {
          school.put("uid",uid);
        }
        if (title.equals("江南大学")) {
          System.out.println("江南大学 uid:" + schoolDict.get(title));
        }
      }
    }
  }

  public static void main(final String[] args) {
    // Set schema
    String schema =
            "uid:int . \n" +
            "chineseName:string @index(exact,term) . \n" +
            "gender:int @index(int) . \n"  +
            "currentJobTitle:string @index(exact,term) .\n" +
            // "industries: uid @reverse . \n" +
            "code:int @index(int) . \n" +
            "title:string @index(exact,term) . \n" +
            "seniority:int @index(int) . \n" +
            "salary:float @index(float) . \n" +
            "educationDegree:int @index(int) . \n" +
            // "pastWorkExperiences:uid @reverse . \n" +
            // "org:uid @reverse . \n" +
            "suggest:string @index(exact,term) .\n" +
            "jobTitle: string @index(exact,term) .\n" +
            // "educationExperiences:uid @reverse . \n" +
            // "school:uid @reverse . \n" +
            "age:int @index(int) . \n";
    DgraphClient dgraphClient = new DgraphClient(TEST_HOSTNAME, TEST_PORT);
    dgraphClient.InitDict("/Users/devops/workspace/hbase-demo/src/main/resources/school_dict.txt");
    // dgraphClient.dropSchema();
    // dgraphClient.createSchema(schema);
    Gson gson = new Gson(); // For JSON encode/decode
    Person p = new Person();
    p.name = "ycj";
    // Serialize it
    // String json = gson.toJson(p);
    ArrayList list = new ArrayList<String>();
    File file = new File("/Users/devops/workspace/hbase-demo/src/main/resources/part-m-00371");
    BufferedReader reader = null;
    try {
      reader = new BufferedReader(new FileReader(file));
      String tempString = null;
      int line = 0;
      // 一次读入一行，直到读入null为文件结束
      while ((tempString = reader.readLine()) != null) {
        // 显示行号
        String json = tempString.split("\t")[1];
        // System.out.println("json:" + json);
        JsonObject jsonObject = new JsonObject(json);
        ArrayList<String> arrayList = new ArrayList<String>();
        arrayList.add("tags");
        dgraphClient.fixJsonObject(jsonObject, arrayList);
        dgraphClient.jsonObjectAddUid(jsonObject);
        list.add(jsonObject.toString());
        if (line % 100 == 0) {
          dgraphClient.mutation(list);
          list.clear();
          line = 0;
        }
        line++;
      }
      if (line > 0) {
        dgraphClient.mutation(list);
      }
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
    System.out.println("maxUid:" + dgraphClient.maxUid);
    System.out.println("finished");
  }
}

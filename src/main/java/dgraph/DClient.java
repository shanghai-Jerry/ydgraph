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
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dgraph.node.People;
import dgraph.node.Person;
import io.dgraph.DgraphClient;
import io.dgraph.DgraphGrpc;
import io.dgraph.DgraphProto;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

public class DClient {

  private static final String TEST_HOSTNAME = "172.20.0.68";
  private static final int TEST_PORT = 9080;
  private static final Logger logger = LoggerFactory.getLogger(DClient.class);

  private static  HashMap<String, Integer> schoolDict = new HashMap<String, Integer>();

  private static JsonFormat.Parser parser = JsonFormat.parser();
  private static final long MURMUR_SEED = 0x7f3a21eaL;
  private static long generateMurMurHashId(String src) {
    byte[] bytes = src.getBytes();
    long murmurId = Murmur2.hash64(bytes, bytes.length, MURMUR_SEED);
    return murmurId;
  }

  private  int maxUid = 0;

  public DgraphClient getDgraphClient() {
    return dgraphClient;
  }

  private io.dgraph.DgraphClient dgraphClient;

  public DClient(String host, int port) {
    ManagedChannel channel =
        ManagedChannelBuilder.forAddress(host, port).usePlaintext(true).build();
    DgraphGrpc.DgraphBlockingStub blockingStub = DgraphGrpc.newBlockingStub(channel);
    dgraphClient = new io.dgraph.DgraphClient(Collections.singletonList(blockingStub));
  }

  public void dropSchema() {
    // Initialize
    dgraphClient.alter(DgraphProto.Operation.newBuilder().setDropAll(true).build());
  }

  public void alterSchema(String schema) {
    // DgraphProto.AssignedIds.newBuilder().setStartId(1).setEndId(80000).build();
    DgraphProto.Operation op = DgraphProto.Operation.newBuilder()
        .setSchema(schema).build();
    dgraphClient.alter(op);
  }

  public DgraphProto.Assigned mutationEntityEdgeSet(io.dgraph.DgraphClient.Transaction txn,
                                                    String id, String predicate,String idRelat) {
    DgraphProto.NQuad quad =
        DgraphProto.NQuad.newBuilder()
            .setSubject(String.format("_:%s", id))
            .setPredicate(predicate)
            .setObjectValue(DgraphProto.Value.newBuilder().setStrVal(String.format("_:%s",idRelat))
                .build())
            .build();
    DgraphProto.Mutation mu = DgraphProto.Mutation.newBuilder().addSet(quad).build();
    DgraphProto.Assigned ag = txn.mutate(mu);
    return ag;
  }

  public String mutationEntityAttriSet(io.dgraph.DgraphClient.Transaction txn, String id,
                                       String predicate,String value) {
    DgraphProto.NQuad quad =
        DgraphProto.NQuad.newBuilder()
            .setSubject(String.format("_:%s", id))
            .setPredicate(predicate)
            .setObjectValue(DgraphProto.Value.newBuilder().setStrVal(String.format("%s",value)).build())
            .build();
    DgraphProto.Mutation mu = DgraphProto.Mutation.newBuilder().addSet(quad).build();
    DgraphProto.Assigned ag = txn.mutate(mu);
    return ag.getUidsOrThrow(id);
  }

  public List<DgraphProto.Assigned> mutiplyMutation(io.dgraph.DgraphClient.Transaction txn, List<String> jsons) {
   List<DgraphProto.Assigned> assignedList = new ArrayList<DgraphProto.Assigned>();
    for (String json : jsons) {
      assignedList.add(mutation(txn, json));
    }
    return assignedList;
  }

  public DgraphProto.Assigned mutation(io.dgraph.DgraphClient.Transaction txn, String json) {
    DgraphProto.Mutation mu = DgraphProto.Mutation.newBuilder()
        .setSetJson(ByteString.copyFromUtf8(json.toString()))
        .build();
    DgraphProto.Assigned assigned = txn.mutate(mu);
    return assigned;
  }
  public String QueryById(String did, String className, String methodName) {
    String query =
        "query did($a: string){\n" + "isExist(func: eq(id, $a)) {\n" + "uid\n" + "  }\n" + "}";
    Map<String, String> vars = Collections.singletonMap("$a", did);
    DgraphProto.Response res = dgraphClient.newTransaction().queryWithVars(query, vars);
    Class classNameClass = null;
    Object people = null;
    String ret = "";
    try {
        classNameClass = Class.forName(className);
        // people = classNameClass.newInstance();
        String resp = res.getJson().toStringUtf8();
        people = new Gson().fromJson(resp, Class.forName(className));
        Method[] methods = classNameClass.getMethods();
        // 循环查找想要的方法
        for(Method method : methods) {
          if (methodName.equals(method.getName())) {
            // 调用这个方法，invoke第一个参数是类名，后面是方法需要的参数
            Object result = method.invoke(people);
            return (String)result;
          }
        }
      } catch (IllegalAccessException e) {
        e.printStackTrace();
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
    } catch (InvocationTargetException e) {
      e.printStackTrace();
    }
    return ret;

  }
  public void QueryDemo(String query,  Map<String, String> vars) {
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
    for (Person p : ppl.getAll()) {
      System.out.println(p.getName());
    }
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
    DClient dClient = new DClient(TEST_HOSTNAME, TEST_PORT);
    // dClient.dropSchema();
    // dClient.createSchema(schema);
  }
}

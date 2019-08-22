package com.higgs.grakn;

import com.google.gson.stream.JsonReader;

import com.higgs.utils.FileUtils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import grakn.client.GraknClient;
import graql.lang.query.GraqlInsert;
import io.vertx.core.json.JsonObject;

import static graql.lang.Graql.parse;

/**
 * User: JerryYou
 *
 * Date: 2019-08-22
 *
 * Copyright (c) 2018 devops
 *
 * <<licensetext>>
 */
public class DataMigration {

  abstract static class Input {
    String path;

    public Input(String path) {
      this.path = path;
    }

    String getDataPath() {
      return path;
    }

    // transform data to Graql
    abstract String template(JsonObject data);

    // according data format, transform data to standard json format
    abstract List<JsonObject> parseDataToJson() throws IOException;
  }

  static void connectAndMigrate(Collection<Input> inputs) {
    HgraknClient hgraknClient = new HgraknClient(Variable.GRAKN_ADDRESS);
    GraknClient.Session session = hgraknClient.getClient().session(Variable.KEY_SPACE);
    for (Input input : inputs) {
      System.out.println("Loading from [" + input.getDataPath() + "] into Grakn ...");
      loadDataIntoGrakn(input, session);
    }

    session.close();
    hgraknClient.close();
  }

  private static void loadDataIntoGrakn(Input input, GraknClient.Session session) {
    List<JsonObject> items = null;
    try {
      items = input.parseDataToJson();
    } catch (IOException e) {
      e.printStackTrace();
    }
    int count = 0;
    for (JsonObject item : items) {
      count++;
      GraknClient.Transaction transaction = session.transaction().write();
      String graqlInsertQuery = input.template(item);
      //
      transaction.execute((GraqlInsert) parse(graqlInsertQuery));
      transaction.commit();
      if (count % 1000 == 0) {
        System.out.println("Executing Graql Insert : " + count + "/" + items.size());
      }
    }
    System.out.println("\nInserted " + items.size() + " items from [ " + input.getDataPath() + "] into Grakn.\n");
  }

  public static Reader getReader(String relativePath) throws FileNotFoundException {
    return new InputStreamReader(new FileInputStream(relativePath));
  }

  static Collection<Input> customDataFormatInput(){
      Collection<Input> inputs = new ArrayList<>();
      inputs.add(new Input("/path/file") {
        @Override
        public String template(JsonObject call) {
          // match caller
          String graqlInsertQuery = "";
          return graqlInsertQuery;
        }

        @Override
        List<JsonObject> parseDataToJson() {
          List<JsonObject> items = new ArrayList<>();
          List<String> contents = new ArrayList<>();
          FileUtils.readFiles(this.getDataPath(), contents);
          for(String content : contents) {
            items.add(new JsonObject(content));
          }
          return items;
        }
      });
      return inputs;
  }

  static Collection<Input> initialiseInputs(String dataDir) {
    Collection<Input> inputs = new ArrayList<>();
    int index = 0;
    if (!dataDir.endsWith("/")) {
      dataDir = dataDir + "/";
    }
    // define template for constructing a company Graql insert query
    inputs.add(new Input(dataDir + "companies") {
      @Override
      public String template(JsonObject company) {
        return "insert $company isa company, has name " + company.getString("name") + ";";
      }
      @Override
      List<JsonObject> parseDataToJson() throws IOException {
        List<JsonObject> items = new ArrayList<>();
        JsonReader jsonReader = new JsonReader(getReader(this.getDataPath() + ".json")); // 1
        jsonReader.beginArray();
        while (jsonReader.hasNext()) {
          jsonReader.beginObject();
          JsonObject item = new JsonObject();
          while (jsonReader.hasNext()) {
            String key = jsonReader.nextName();
            switch (jsonReader.peek()) {
              case STRING:
                item.put(key, jsonReader.nextString()); // 2
                break;
              case NUMBER:
                item.put(key, jsonReader.nextInt()); // 2
                break;
            }
          }
          jsonReader.endObject();
          items.add(item); // 3
        }
        jsonReader.endArray();
        return items;
      }
    });
    // define template for constructing a person Graql insert query
    inputs.add(new Input("people") {
      @Override
      public String template(JsonObject person) {
        // insert person
        String graqlInsertQuery = "insert $person isa person, has phone-number " + person.getString("phone_number");

        if (! person.containsKey("first_name")) {
          // person is not a customer
          graqlInsertQuery += ", has is-customer false";
        } else {
          // person is a customer
          graqlInsertQuery += ", has is-customer true";
          graqlInsertQuery += ", has first-name " + person.getString("first_name");
          graqlInsertQuery += ", has last-name " + person.getString("last_name");
          graqlInsertQuery += ", has city " + person.getString("city");
          graqlInsertQuery += ", has age " + person.getInteger("age");
        }

        graqlInsertQuery += ";";
        return graqlInsertQuery;
      }
      @Override
      List<JsonObject> parseDataToJson() throws IOException {
        List<JsonObject> items = new ArrayList<>();
        JsonReader jsonReader = new JsonReader(getReader(this.getDataPath() + ".json")); // 1
        jsonReader.beginArray();
        while (jsonReader.hasNext()) {
          jsonReader.beginObject();
          JsonObject item = new JsonObject();
          while (jsonReader.hasNext()) {
            String key = jsonReader.nextName();
            switch (jsonReader.peek()) {
              case STRING:
                item.put(key, jsonReader.nextString()); // 2
                break;
              case NUMBER:
                item.put(key, jsonReader.nextInt()); // 2
                break;
            }
          }
          jsonReader.endObject();
          items.add(item); // 3
        }
        jsonReader.endArray();
        return items;
      }
    });
    // define template for constructing a contract Graql insert query
    inputs.add(new Input("contracts") {
      @Override
      public String template(JsonObject contract) {
        // match company
        String graqlInsertQuery = "match $company isa company, has name " + contract.getString("company_name") + ";";
        // match person
        graqlInsertQuery += " $customer isa person, has phone-number " + contract.getString("person_id")
            + ";";
        // insert contract
        graqlInsertQuery += " insert (provider: $company, customer: $customer) isa contract;";
        return graqlInsertQuery;
      }
      @Override
      List<JsonObject> parseDataToJson() throws IOException {
        List<JsonObject> items = new ArrayList<>();
        JsonReader jsonReader = new JsonReader(getReader(this.getDataPath() + ".json")); // 1
        jsonReader.beginArray();
        while (jsonReader.hasNext()) {
          jsonReader.beginObject();
          JsonObject item = new JsonObject();
          while (jsonReader.hasNext()) {
            String key = jsonReader.nextName();
            switch (jsonReader.peek()) {
              case STRING:
                item.put(key, jsonReader.nextString()); // 2
                break;
              case NUMBER:
                item.put(key, jsonReader.nextInt()); // 2
                break;
            }
          }
          jsonReader.endObject();
          items.add(item); // 3
        }
        jsonReader.endArray();
        return items;
      }
    });
    // define template for constructing a call Graql insert query
    inputs.add(new Input("calls") {
      @Override
      public String template(JsonObject call) {
        // match caller
        String graqlInsertQuery = "match $caller isa person, has phone-number " + call.getString("caller_id") + ";";
        // match callee
        graqlInsertQuery += " $callee isa person, has phone-number " + call.getString("callee_id") + ";";
        // insert call
        graqlInsertQuery += " insert $call(caller: $caller, callee: $callee) isa call;" +
            " $call has started-at " + call.getString("started_at") + ";" +
            " $call has duration " + call.getInteger("duration") + ";";
        return graqlInsertQuery;
      }

      @Override
      List<JsonObject> parseDataToJson() throws IOException {
        List<JsonObject> items = new ArrayList<>();
        JsonReader jsonReader = new JsonReader(getReader(this.getDataPath() + ".json")); // 1
        jsonReader.beginArray();
        while (jsonReader.hasNext()) {
          jsonReader.beginObject();
          JsonObject item = new JsonObject();
          while (jsonReader.hasNext()) {
            String key = jsonReader.nextName();
            switch (jsonReader.peek()) {
              case STRING:
                item.put(key, jsonReader.nextString()); // 2
                break;
              case NUMBER:
                item.put(key, jsonReader.nextInt()); // 2
                break;
            }
          }
          jsonReader.endObject();
          items.add(item); // 3
        }
        jsonReader.endArray();
        return items;
      }
    });
    return inputs;
  }

  public static void main(String[] args) {
    Collection<Input> inputs = initialiseInputs("/Users/devops/workspace/kb/phone_calls");
    connectAndMigrate(inputs);
  }
}

package demo;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import io.vertx.core.json.JsonObject;

public class Demo {
  public static void main(String[] args) throws Exception {
    /*
    String info = "123##########456";
    int lastIndexOf = info.lastIndexOf("##########");
    String docId = info.substring(lastIndexOf + "##########".length());
    String json = info.substring(0, lastIndexOf);
    System.out.println(docId + "\t" + json);*/


    ArrayList list = new ArrayList<String>();
    File file = new File("/Users/devops/workspace/hbase-Demo/src/StartMain/resources/test");
    BufferedReader reader = null;
    try {
      reader = new BufferedReader(new FileReader(file));
      String tempString = null;
      int line = 0;
      // 一次读入一行，直到读入null为文件结束
      while ((tempString = reader.readLine()) != null) {
        // 显示行号
        int index = tempString.indexOf("\t");
        System.out.println("index:" + index);
        String json = tempString.substring(index + "\t".length());
        JsonObject jsonObject = new JsonObject(json);
        System.out.println("json:" + json);
        line++;
      }
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }

  }
}

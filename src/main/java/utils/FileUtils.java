package utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class FileUtils {

  public static void readFiles(String filePath, List<String> dict) {
    BufferedReader reader = null;
    try {
      reader = new BufferedReader(new InputStreamReader(new FileInputStream(filePath), "utf-8"));
      String line;
      line = reader.readLine();
      while (line != null) {
        dict.add(line);
        line = reader.readLine();
      }
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      if (reader != null) {
        try {
          reader.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }
  }

  public static void saveFile(String filePath, Map<String, String> map) {
    PrintWriter printWriter  = null;
    try {
      printWriter = new PrintWriter(new FileOutputStream(new File(filePath), false));
      Set<Map.Entry<String, String>> entrySet=  map.entrySet();
      Iterator<Map.Entry<String, String>> iterator = entrySet.iterator();
      int count = 0;
      while(iterator.hasNext()) {
        Map.Entry<String, String> entry = iterator.next();
        String key = entry.getKey();
        String value = entry.getValue();
        printWriter.write(key + "\t" + value);
        printWriter.write("\n");
        count++;
        if (count >= 200) {
          printWriter.flush();
          count = 0;
        }
      }
      printWriter.flush();
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } finally {
      if (printWriter != null) {
        printWriter.close();
      }
    }
  }
}

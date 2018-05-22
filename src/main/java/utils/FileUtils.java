package utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class FileUtils {

  public static void readUidMapDict(String filePath, Map<String, List<String>> uidMap) {
    BufferedReader reader = null;
    try {
      reader = new BufferedReader(new InputStreamReader(new FileInputStream(filePath), "utf-8"));
      String line;
      line = reader.readLine();
      while (line != null) {
        String[] lineSpilt = line.split(",");
        if (lineSpilt.length == 2) {
          uidMap.put(lineSpilt[1].trim(), Arrays.asList(lineSpilt[0].trim()));
        } else {
          System.out.println("length error !!");
        }
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

  public static void readDict(String filePath, Map<String, String> uidMap) {
    BufferedReader reader = null;
    try {
      reader = new BufferedReader(new InputStreamReader(new FileInputStream(filePath), "utf-8"));
      String line;
      line = reader.readLine();
      while (line != null) {
        String[] lineSpilt = line.split("\t");
        if (lineSpilt.length == 2) {
          uidMap.put(lineSpilt[0], lineSpilt[1]);
        }
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

  public static void saveFile(String filePath, Map<String, List<String>> map) {
    PrintWriter printWriter = null;
    try {
      printWriter = new PrintWriter(new FileOutputStream(new File(filePath), false));
      Set<Map.Entry<String, List<String>>> entrySet = map.entrySet();
      Iterator<Map.Entry<String, List<String>>> iterator = entrySet.iterator();
      int count = 0;
      while (iterator.hasNext()) {
        Map.Entry<String, List<String>> entry = iterator.next();
        String key = entry.getKey();
        List<String> values= entry.getValue();
        StringBuffer sb = new StringBuffer();
        for (String value : values) {
          sb.append(value + ",");
        }
        printWriter.write(sb.toString() + "\t" + key );
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

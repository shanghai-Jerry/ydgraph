package com.higgs.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

public class FileUtils {

  private static final Logger LOGGER = LoggerFactory.getLogger(FileUtils.class);
  /**
   * 读取对应的quey语句文件
   *
   * @param filePath fileName对应QueryType的枚举值
   * @return query
   * @throws IOException 读取文件异常
   */
  public static String readQueryFile(String filePath) {
    try {
      return new String(Files.readAllBytes(Paths.get(filePath)));
    } catch (IOException e) {
      LOGGER.info("[readQueryFile error] => " + e.getMessage());
    }
    return "";
  }

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
  public static void readAllFiles(String dirPath, List<String> dict) {
    try {
      Files.walkFileTree(Paths.get(dirPath), new SimpleFileVisitor<Path>() {
        @Override
        @SuppressWarnings({"IllegalCatch", "NestedTryDepth"})
        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {

          String fileName = file.getFileName().toString();
          if (fileName.endsWith(".doc") || fileName.endsWith(".docx")) {
            // ..todo
          } else if (fileName.endsWith(".pdfx")) {
            // ..todo
          } else if (fileName.endsWith(".zip")) {
            ZipInputStream zin = new ZipInputStream(new FileInputStream(file.toFile()), Charset.forName("GBK"));
            try {
              ZipEntry entry = null;
              while ((entry = zin.getNextEntry()) != null) {
                String name = entry.getName();
                try {

                  if (!entry.isDirectory() && (name.endsWith(".doc") || name.endsWith(".docx"))) {
                    // ..todo
                  }
                } finally {
                  try {
                    zin.closeEntry();
                  } catch (Exception ex) {
                    System.err.println("got error for zip file:" + fileName + ",and entry:" + name);
                  }
                }
              }
            } catch (Exception ex) {
              System.err.println("process zip file error :" + file.toAbsolutePath().toString());
            } finally {
              zin.close();
            }
          } else {
            // ..todo
          }
          return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult postVisitDirectory(Path dir, IOException e) throws IOException {
          if (e != null) {
            System.err.println("found error after visit directory:" + e);
          }
          return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
          System.err.println("skipped:" + file);
          return FileVisitResult.CONTINUE;
        }
      });
    } catch (IOException e) {
      Util.println("IOException", e.getMessage());
    }
  }

  public static void readFiles(String dirPath, List<String> dict) {
    File dirFile = new File(dirPath);
    if (dirFile.isDirectory()) {
      File []files = dirFile.listFiles();
      for (File file : files) {
        readFiles(file.getPath(), dict);
      }
    } else {
      // Util.println("filePath", dirFile.getAbsolutePath());
      readFile(dirFile.getAbsolutePath(), dict);
    }
  }

  public static void readFile(String filePath, List<String> dict) {
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
          sb.append(value + " ");
        }
        printWriter.write("200\t"  + key  + "\t" + sb.toString());
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
  public static void saveFiles(String filePath, Map<String, String> map) {
    PrintWriter printWriter = null;
    try {
      printWriter = new PrintWriter(new FileOutputStream(new File(filePath), false));
      Set<Map.Entry<String, String>> entrySet = map.entrySet();
      Iterator<Map.Entry<String, String>> iterator = entrySet.iterator();
      int count = 0;
      while (iterator.hasNext()) {
        Map.Entry<String, String> entry = iterator.next();
        String key = entry.getKey();
        String value = entry.getValue();
        printWriter.write(key + "\t" + value );
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
  public static void saveFiles(String filePath, List<String> dict, boolean append) {
    PrintWriter printWriter = null;
    try {
      printWriter = new PrintWriter(new FileOutputStream(new File(filePath), append));
      int count = 0;
      for (String line : dict) {
        printWriter.write(line);
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
  public static void saveFileWith(String filePath, Map<String, Integer> map) {
    PrintWriter printWriter = null;
    try {
      printWriter = new PrintWriter(new FileOutputStream(new File(filePath), false));
      Set<Map.Entry<String, Integer>> entrySet = map.entrySet();
      Iterator<Map.Entry<String, Integer>> iterator = entrySet.iterator();
      int count = 0;
      while (iterator.hasNext()) {
        Map.Entry<String, Integer> entry = iterator.next();
        String key = entry.getKey();
        String value = entry.getValue().toString();
        printWriter.write(key + "\t" + value );
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
  public static void saveFile(String filePath, List<String> stringList, boolean isAppend) {
    PrintWriter printWriter = null;
    try {
      printWriter = new PrintWriter(new FileOutputStream(new File(filePath), isAppend));
      int count = 0;
      for (String string : stringList) {
        printWriter.write(string);
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

  public static void deleteFile(String filePath) {
    new File(filePath).delete();
  }
}

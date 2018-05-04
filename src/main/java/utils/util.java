package utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import client.EntityIdClient;
import dgraph.node.EntityNode;
import dgraph.node.School;

public class util {

  public static String longToHex(long i) {
    return Long.toHexString(i);
  }
  public static long hexToLong(String str) {
    if (str.startsWith("0x")) {
      return Long.parseLong(str.substring(2),16);
    } else  {
      return  Long.parseLong(str,16);
    }
  }

  public static void getUidMap(List<String> key, List<String> value, Map<String, String>
      resultMap) {
    int size = key.size();
    if (size != value.size()) {
      return;
    }
    for (int i = 0; i < size; i++) {
      resultMap.put(key.get(i), value.get(i));
    }
  }

  public static void mapCombiner(Map<String, String> map, Map<String, String> resultMap) {
    Set<Map.Entry<String, String>> entrySet=  map.entrySet();
    Iterator<Map.Entry<String, String>> iterator = entrySet.iterator();
    while(iterator.hasNext()) {
      Map.Entry<String, String> entry = iterator.next();
      String key = entry.getKey();
      String value = entry.getValue();
      resultMap.put(key, value);
    }
  }
}

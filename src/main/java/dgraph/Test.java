package dgraph;

import java.util.Arrays;
import java.util.Map;

import dgraph.node.Label;
import dgraph.node.NodeUtil;
import dgraph.node.School;
import utils.FileUtils;

/**
 * Created by lolaliva on 2018/5/10.
 */
public class Test {

  DClient dClient = new DClient(Config.TEST_VM_HOSTNAME);

  public void test_one() {
    School school = new School();
    String name = "清华大学";
    String type = "学校";
    school.setName(name);
    school.setType(type);
    school.setUnique_id(name);
    Label label = new Label();
    label.setLabel_name("学校类型");
    label.setUid("0x118c");
    school.setHas_label(label);
    // school.getAttrValueMap(new ArrayList<String>(), new ArrayList<>());
    // school.getEdgeValueMap(new ArrayList<String>(), new ArrayList<String>(), "getUid");
    // school.getValueMap(new ArrayList<String>(), new ArrayList<Object>(), new ArrayList<String>
    // (), new ArrayList<String>(), "getUid");
    // school.getEdgeValueMap(new ArrayList<String>(), new ArrayList<String>());
    Map<String, String> uid = NodeUtil.insertEntity(dClient, Arrays.asList(school));
    FileUtils.saveFile("src/main/resources/test_uid_map.txt", uid);
  }

  private void test_two() {
    dClient.entityAddAttrTest("分析化学", "has_label", "0x118d");
  }

  public static void main(String[] arg) {
    Test test = new Test();
    // test.test_two();
    test.test_one();

  }


}

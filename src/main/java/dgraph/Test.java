package dgraph;

import java.util.ArrayList;
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

  Demo demo = new Demo(dClient);

  public void test_one() {
    demo.init();
    School school = new School();
    String name = "清华大学";
    String type = "学校";
    school.setName(name);
    school.setType(type);
    school.setUnique_id(name);
    Label label = new Label();
    label.setLabel_name("学校类型");
    label.setUid("0x118c");
    label.setUnique_id("学校类型");
    label.setSchool(Arrays.asList(school));
    // school.setHas_label(label);
    // school.getAttrValueMap(new ArrayList<String>(), new ArrayList<>());
    // label.getEdgeValueMap(new ArrayList<String>(), new ArrayList<String>(), "getUid");
    // label.getValueMap(new ArrayList<String>(), new ArrayList<Object>(), new ArrayList<String>(),new ArrayList<String>(), "getUid");
    Map<String, String> schoolEntityUidMap = NodeUtil.insertEntity(dClient, label.getSchool());
    NodeUtil.putEntityUid(label.getSchool(), schoolEntityUidMap);
    // school.getEdgeValueMap(new ArrayList<String>(), new ArrayList<String>());
    Map<String, String> uid = NodeUtil.insertEntity(dClient, Arrays.asList(label));
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

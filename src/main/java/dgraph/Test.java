package dgraph;

import dgraph.node.Label;
import dgraph.node.NodeUtil;
import dgraph.node.School;
import utils.FileUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by lolaliva on 2018/5/10.
 */
public class Test {

  public static void main(String[] arg) {
    DClient dClient = new DClient(Config.TEST_VM_HOSTNAME);

    School school = new School();
    String name = "北京大学";
    school.setName(name);
    school.setUnique_id(name);
    Label label = new Label();
    label.setHas_label(label);
    label.setLabel_name("学校类型");
    school.setHas_label(label);
    // school.getAttrValueMap(new ArrayList<String>(), new ArrayList<Object>());
    Map<String, String> uid = NodeUtil.insertEntity(dClient, Arrays.asList(school));
    FileUtils.saveFile("src/main/resources/test_uid_map.txt", uid);
  }


}

package dgraph;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import dgraph.node.Company;
import dgraph.node.Industry;
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
    label.setSchool(school);
    /*Map<String, String> schoolEntityUidMap = NodeUtil.insertEntity(dClient, Arrays.asList(label.getSchool()));
    FileUtils.saveFile("src/main/resources/test_school_uid_map.txt", schoolEntityUidMap);
    NodeUtil.putEntityUid(Arrays.asList(label.getSchool()), schoolEntityUidMap, new ArrayList<School>());*/
    Map<String, String> uid = NodeUtil.insertEntity(dClient, Arrays.asList(label));
    FileUtils.saveFile("src/main/resources/test_uid_map.txt", uid);
  }

  private void test_two() {
    dClient.entityAddAttrTest("分析化学", "has_label", "0x118d");
  }

  private void test_tree() {
    Label label = new Label();
    label.setLabel_name("公司类型");
    // "公司类型": "0x118b"
    label.setUid("0x15");
    Label label2 = new Label();
    label2.setLabel_name("公司类型");
    // "公司类型": "0x118b"
    label2.setUid("0x15");
    Industry industry = new Industry();
    String industryType = "行业";
    String industryName = "互联网";
    String industryCode = "8001";
    industry.setType(industryType);
    industry.setUid("0x13");
    industry.setUnique_id(industryName);
    industry.setCode(Integer.parseInt(industryCode));
    industry.setName(industryName);
    Company company = new Company();
    String name = "腾讯有限公司";
    String location = "深圳";
    String type = "公司";
    company.setName(name);
    company.setUnique_id(name);
    company.setLocation(location);
    company.setType(type);
    company.setIndustry(Arrays.asList(industry));
    label.setCompany(company);
    Company company2 = new Company();
    String name2 = "百度有限公司";
    String location2 = "北京";
    String type2 = "公司";
    company2.setName(name2);
    company2.setUnique_id(name2);
    company2.setLocation(location2);
    company2.setType(type2);
    company2.setIndustry(Arrays.asList(industry));
    label2.setCompany(company2);
    Map<String, String> industryEntityUidMap = NodeUtil.insertEntity(dClient, label.getCompany().getIndustry());
    FileUtils.saveFile("src/main/resources/test_industry_uid_map.txt", industryEntityUidMap);
    // 子行业实体uid放回
    NodeUtil.putEntityUid(label.getCompany().getIndustry(), industryEntityUidMap);
    Map<String, String> companyEntityUidMap = NodeUtil.insertEntity(dClient, Arrays.asList(label.getCompany()));
    // 子公司实体uid放回
    NodeUtil.putEntityUid(Arrays.asList(label.getCompany()), companyEntityUidMap);
    FileUtils.saveFile("src/main/resources/test_company_uid_map.txt", companyEntityUidMap);
    Map<String, String> uid = NodeUtil.insertEntity(dClient, Arrays.asList(label, label2));
    FileUtils.saveFile("src/main/resources/test_label_uid_map.txt", uid);
  }

  private void getLabels(List<Label> labelList) {
    List<Label> labels = new ArrayList<>();
    Label label = new Label();
    label.setLabel_name("公司类型");
    // "公司类型": "0x118b"
    label.setUid("0x15");
    Label label2 = new Label();
    label2.setLabel_name("公司类型");
    // "公司类型": "0x118b"
    label2.setUid("0x15");
    Industry industry = new Industry();
    String industryType = "行业";
    String industryName = "互联网";
    String industryCode = "8001";
    industry.setType(industryType);
    industry.setUnique_id(industryName);
    industry.setCode(Integer.parseInt(industryCode));
    industry.setName(industryName);
    Company company = new Company();
    String name = "腾讯有限公司";
    String location = "深圳";
    String type = "公司";
    company.setName(name);
    company.setUnique_id(name);
    company.setLocation(location);
    company.setType(type);
    company.setIndustry(Arrays.asList(industry));
    label.setCompany(company);
    Company company2 = new Company();
    String name2 = "百度有限公司";
    String location2 = "北京";
    String type2 = "公司";
    company2.setName(name2);
    company2.setUnique_id(name2);
    company2.setLocation(location2);
    company2.setType(type2);
    company2.setIndustry(Arrays.asList(industry));
    label2.setCompany(company2);
    labels.add(label);
    labels.add(label2);
    labelList.add(label);
    // labelList.add(label2);
  }

  private void test_five() {
    List<Label> labelList = new ArrayList<>();
    getLabels(labelList);
    Map<String, String> companyEntityUidMap = NodeUtil.insertEntity(dClient,getCompany(labelList));
    // 子公司实体uid放回
    NodeUtil.putEntityUid(getCompany(labelList), companyEntityUidMap);
    FileUtils.saveFile("src/main/resources/test_company_uid_map.txt", companyEntityUidMap);
    ExecutorService ex = Executors.newFixedThreadPool(5);
    for (Label label : labelList) {
      for (int i = 0; i < 5; i++) {
        ex.execute(new TestRunnable(dClient, label));
      }
    }
    try {
      ex.awaitTermination(5, TimeUnit.MINUTES);
      ex.shutdown();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }
  private List<Company> getCompany(List<Label> labels) {
    List<Company> companyList = new ArrayList<>();
    for (Label label : labels) {
      Company company = label.getCompany();
      companyList.add(company);
    }
    return companyList;
  }
  public static void main(String[] arg) {
    Test test = new Test();
    // test.demo.init();
    // test.test_one();
    // test.test_two();
    // test.test_tree();
    test.test_five();

  }


}

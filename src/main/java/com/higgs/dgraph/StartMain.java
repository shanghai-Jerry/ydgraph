package com.higgs.dgraph;

import com.higgs.client.EntityIdClient;
import com.higgs.dgraph.enumtype.EntityType;
import com.higgs.dgraph.node.AgeNode;
import com.higgs.dgraph.node.DegreeNode;
import com.higgs.dgraph.node.EntityNode;
import com.higgs.dgraph.node.GenderNode;
import com.higgs.dgraph.node.NodeUtil;
import com.higgs.dgraph.node.SalaryNode;
import com.higgs.utils.FileUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * User: JerryYou
 *
 * Date: 2018-05-08
 *
 * Copyright (c) 2018 devops
 *
 * <<licensetext>>
 */
public class StartMain {

  DClient dClient = new DClient(Config.addressList);
  EntityIdClient client = new EntityIdClient(Config.ENTITY_ID_HOST, Config.ENTITY_ID_SERVICE_PORT);

  public void withJson() {
    SchoolToDgraph schoolToDgraph = new SchoolToDgraph();
    String dict = "src/main/resources/industry_dump_dict.txt";
    int needCheck = 0;
    IndustryToDgraph industryToDgraph = new IndustryToDgraph();
    industryToDgraph.initWithJson(dict);
    String schoolPath = "src/main/resources/school_dump_dict.txt";
    schoolToDgraph.initWithJson(schoolPath);
    String majorPath = "src/main/resources/major_dict.txt";
    MajorToDgraph majorToDgraph = new MajorToDgraph();
    majorToDgraph.initWithJson(majorPath);
    System.out.println("finished");
  }

  /**
   * 生成一些基础实体的rdf文件
   * ID => type:MD5
   * @param industryDict
   * @param majorDict
   * @param schoolDict
   * @param out
   */
  public void initWithRDFfile(String industryDict, String majorDict, String schoolDict, String
      out) {
    // with rdf
    // 行业
    IndustryToDgraph industryToDgraph = new IndustryToDgraph();
    industryToDgraph.initIndustry(industryDict, true);
    industryToDgraph.generateRDF(out);
    // 专业
    MajorToDgraph majorToDgraph = new MajorToDgraph();
    majorToDgraph.init(majorDict, true);
    majorToDgraph.generateRDF(out);
    // 学校
    SchoolToDgraph schoolToDgraph = new SchoolToDgraph();
    schoolToDgraph.init(schoolDict, true);
    schoolToDgraph.generateRDF(out);
    // 年龄
    List<AgeNode>  ageNodes = new ArrayList<>();
    for (String age : NodeUtil.ages) {
      AgeNode ageNode = new AgeNode();
      String unique = age.trim();
      ageNode.setName(age);
      ageNode.setType(EntityType.AGE.getName());
      ageNode.setUnique_id(EntityType.AGE.getName() + ":" + NodeUtil.generateEntityUniqueId
          (unique));
      ageNode.setUnique_ids(Arrays.asList(unique));
      ageNodes.add(ageNode);
    }
    List<String> entityNquads = NodeUtil.getEntityNquads(ageNodes, new ArrayList<>());
    FileUtils.saveFile(out+ "/age_rdf.txt",  entityNquads, false);

    // 学历
    List<DegreeNode> degreeNodes = new ArrayList<>();
    for(String degree : NodeUtil.degrees) {
      String uniqueid = degree.trim();
      DegreeNode degreeNode = new DegreeNode();
      degreeNode.setName(degree);
      degreeNode.setType(EntityType.DEGREE.getName());
      degreeNode.setUnique_id(EntityType.DEGREE.getName() + ":" + NodeUtil.generateEntityUniqueId
          (uniqueid));
      degreeNode.setUnique_ids(Arrays.asList(uniqueid));
      degreeNodes.add(degreeNode);
    }
    entityNquads = NodeUtil.getEntityNquads(degreeNodes, new ArrayList<>());
    FileUtils.saveFile(out + "/degree_rdf.txt",  entityNquads, false);
    // 性别
    List<GenderNode> genderNodes = new ArrayList<>();
    for(String gender : NodeUtil.genders) {
      String unique = gender.trim();
      GenderNode genderNode = new GenderNode();
      genderNode.setName(gender);
      genderNode.setType(EntityType.GENDER.getName());
      genderNode.setUnique_id(EntityType.GENDER.getName() + ":" + NodeUtil.generateEntityUniqueId
          (unique));
      genderNode.setUnique_ids(Arrays.asList(unique));
      genderNodes.add(genderNode);
    }
    entityNquads = NodeUtil.getEntityNquads(genderNodes, new ArrayList<>());
    FileUtils.saveFile(out + "/gender_rdf.txt",  entityNquads, false);
    // 薪资
    List<SalaryNode> salaryNodes = new ArrayList<>();
    for (String salary : NodeUtil.salaries) {
      String unique = salary.trim();
      SalaryNode salaryNode = new SalaryNode();
      salaryNode.setName(unique);
      salaryNode.setType(EntityType.SALARY.getName());
      salaryNode.setUnique_id(EntityType.SALARY.getName() + ":" + NodeUtil.generateEntityUniqueId
          (unique));
      salaryNode.setUnique_ids(Arrays.asList(unique));
      salaryNodes.add(salaryNode);
    }
    entityNquads = NodeUtil.getEntityNquads(salaryNodes, new ArrayList<>());
    FileUtils.saveFile(out + "/monthlySalary_rdf.txt",  entityNquads, false);
    // 薪资：年薪
    List<SalaryNode> annualSalaryNodes = new ArrayList<>();
    for (String salary: NodeUtil.annualSalary) {
      String unique = salary.trim();
      SalaryNode salaryNode = new SalaryNode();
      salaryNode.setName(unique);
      salaryNode.setType(EntityType.ANNUAL.getName());
      salaryNode.setUnique_id(EntityType.ANNUAL.getName() + ":" + NodeUtil.generateEntityUniqueId
          (unique));
      salaryNode.setUnique_ids(Arrays.asList(unique));
      annualSalaryNodes.add(salaryNode);
    }
    entityNquads = NodeUtil.getEntityNquads(annualSalaryNodes, new ArrayList<>());
    FileUtils.saveFile(out+ "/annualSalary_rdf.txt",  entityNquads, false);
    // 工作年限
    List<EntityNode> seniortyNodes = new ArrayList<>();
    for (String senior : NodeUtil.seniors) {
      String unique = senior.trim();
      EntityNode entityNode = new EntityNode();
      entityNode.setType(EntityType.SENIORITY.getName());
      entityNode.setUnique_id(EntityType.SENIORITY.getName() + ":" + NodeUtil
          .generateEntityUniqueId(unique));
      entityNode.setUnique_ids(Arrays.asList(unique));
      seniortyNodes.add(entityNode);
    }
    entityNquads = NodeUtil.getEntityNquads(seniortyNodes, new ArrayList<>());
    FileUtils.saveFile(out + "/seniors_rdf.txt",  entityNquads, false);
  }

  /**
   * 更新一些基础实体的信息
   */
  public void update() {

    // 年龄
    List<AgeNode> ageNodes = new ArrayList<>();
    List<String> ageUniqueIds = Arrays.asList("20岁以下", "20-25岁", "25-30岁", "30-35岁",
        "35岁以上");
    for (String age : ageUniqueIds) {
      AgeNode ageNode = new AgeNode();
      String unique = EntityType.AGE.getName() + ":" + age;
      ageNode.setName(age);
      ageNode.setUnique_id(unique);
      ageNode.setUnique_ids(Arrays.asList(unique));
      ageNodes.add(ageNode);
    }
    client.putEntityListUid(ageNodes, EntityType.AGE.getName());
    NodeUtil.updateEntity(dClient, ageNodes);

    // 学历
    List<DegreeNode> degreeNodes = new ArrayList<>();
    List<String> degreeUniques = Arrays.asList("未知","大专","本科","硕士","博士","博士后","MBA","大专以下");
    for(String degree : degreeUniques) {
      String uniqueid = EntityType.DEGREE.getName() + ":" + degree;
      DegreeNode degreeNode = new DegreeNode();
      degreeNode.setName(degree);
      degreeNode.setUnique_id(uniqueid);
      degreeNode.setUnique_ids(Arrays.asList(uniqueid));
      degreeNodes.add(degreeNode);
    }
    client.putEntityListUid(degreeNodes, EntityType.DEGREE.getName());
    NodeUtil.updateEntity(dClient, degreeNodes);

    // 性别
    List<GenderNode> genderNodes = new ArrayList<>();
    List<String> genders = Arrays.asList("男", "女");
    for(String gender : genders) {
      String unique = EntityType.GENDER.getName() + ":" + gender;
      GenderNode genderNode = new GenderNode();
      genderNode.setName(gender);
      genderNode.setUnique_id(unique);
      genderNode.setUnique_ids(Arrays.asList(unique));
      genderNodes.add(genderNode);
    }
    client.putEntityListUid(genderNodes, EntityType.GENDER.getName());

    NodeUtil.updateEntity(dClient, genderNodes);

    // 薪资:月薪
    List<SalaryNode> salaryNodes = new ArrayList<>();
    List<String> strings = Arrays.asList("3000元以下","3000-7000元",	"7000-15000元",	"15000-20000元	" +
        "20000-30000元	","30000-50000元	","50000元以上");
    for (String salary: strings) {
      String unique = EntityType.SALARY.getName() + ":" + salary;
      SalaryNode salaryNode = new SalaryNode();
      salaryNode.setName(unique);
      salaryNode.setUnique_id(unique);
      salaryNode.setUnique_ids(Arrays.asList(unique));
      salaryNodes.add(salaryNode);
    }
    client.putEntityListUid(salaryNodes, EntityType.SALARY.getName());

    NodeUtil.updateEntity(dClient, salaryNodes);

    // 薪资：年薪
    List<SalaryNode> annualSalaryNodes = new ArrayList<>();
    List<String>  annualSalary = Arrays.asList("10万以下","10-20万",	"20-30万",
        "30-50万	", "50-100万","100万以上");
    for (String salary: annualSalary) {
      String unique = EntityType.ANNUAL.getName() + ":" + salary;
      SalaryNode salaryNode = new SalaryNode();
      salaryNode.setName(unique);
      salaryNode.setUnique_id(unique);
      salaryNode.setUnique_ids(Arrays.asList(unique));
      annualSalaryNodes.add(salaryNode);
    }
    client.putEntityListUid(annualSalaryNodes, EntityType.ANNUAL.getName());

    NodeUtil.updateEntity(dClient, annualSalaryNodes);

    // 工作年限
    List<String> seniors = Arrays.asList( "1年" , "1-2年"	, "2-3年", "3-5年"	, "5-10年"	, "10年以上");
    List<EntityNode> seniortyNodes = new ArrayList<>(seniors.size());
    NodeUtil.updateEntityNode(seniortyNodes, seniors, EntityType.SENIORITY.getName(), dClient,
        client);

  }

  /**
   * ID => type:unique_id
   * 初始化一些基础实体
   */
  public void init() {
    // 行业
    String dict = "src/main/resources/industry_dump_dict.txt";
    IndustryToDgraph industryToDgraph = new IndustryToDgraph(dClient, client);
    // with rdf
    industryToDgraph.initWithRdf(dict,true);

    // 专业
    String dictPath = "src/main/resources/major_dict.txt";
    MajorToDgraph majorToDgraph = new MajorToDgraph(dClient, client);
    majorToDgraph.initWithRdf(dictPath, true);

    // 学校
    SchoolToDgraph schoolToDgraph = new SchoolToDgraph(dClient, client);
    String schoolPath = "src/main/resources/school_dump_dict.txt";
    schoolToDgraph.initWithRdf(schoolPath, true);

    // 年龄
    List<AgeNode> ageNodes = new ArrayList<>();
    List<String> ageUniqueIds = Arrays.asList("20岁以下", "20-25岁", "25-30岁", "30-35岁",
        "35岁以上");
    for (String age : ageUniqueIds) {
      AgeNode ageNode = new AgeNode();
      String unique = EntityType.AGE.getName() + ":" + age;
      ageNode.setName(age);
      ageNode.setUnique_id(unique);
      ageNode.setUnique_ids(Arrays.asList(unique));
      ageNodes.add(ageNode);
    }
    Map<String,  List<String>> uidMap = NodeUtil.insertEntity(dClient, ageNodes);
    client.putFeedEntityWithUidNamesMap(uidMap, EntityType.AGE.getName());

    // 学历
    List<DegreeNode> degreeNodes = new ArrayList<>();
    List<String> degreeUniques = Arrays.asList("未知","大专","本科","硕士","博士","博士后","MBA","大专以下");
    for(String degree : degreeUniques) {
      String uniqueid = EntityType.DEGREE.getName() + ":" + degree;
      DegreeNode degreeNode = new DegreeNode();
      degreeNode.setName(degree);
      degreeNode.setUnique_id(uniqueid);
      degreeNode.setUnique_ids(Arrays.asList(uniqueid));
      degreeNodes.add(degreeNode);
    }
    uidMap = NodeUtil.insertEntity(dClient, degreeNodes);
    client.putFeedEntityWithUidNamesMap(uidMap, EntityType.DEGREE.getName());

    // 性别
    List<GenderNode> genderNodes = new ArrayList<>();
    List<String> genders = Arrays.asList("男", "女");
    for(String gender : genders) {
      String unique = EntityType.GENDER.getName() + ":" + gender;
      GenderNode genderNode = new GenderNode();
      genderNode.setName(gender);
      genderNode.setUnique_id(unique);
      genderNode.setUnique_ids(Arrays.asList(unique));
      genderNodes.add(genderNode);
    }
    uidMap = NodeUtil.insertEntity(dClient, genderNodes);
    client.putFeedEntityWithUidNamesMap(uidMap, EntityType.GENDER.getName());

    // 薪资:月薪
    List<SalaryNode> salaryNodes = new ArrayList<>();
    List<String> strings = Arrays.asList("3000元以下","3000-7000元",	"7000-15000元",	"15000-20000元	" +
        "20000-30000元	","30000-50000元	","50000元以上");
    for (String salary: strings) {
      String unique = EntityType.SALARY.getName() + ":" + salary;
      SalaryNode salaryNode = new SalaryNode();
      salaryNode.setName(unique);
      salaryNode.setUnique_id(unique);
      salaryNode.setUnique_ids(Arrays.asList(unique));
      salaryNodes.add(salaryNode);
    }
    uidMap = NodeUtil.insertEntity(dClient, salaryNodes);
    client.putFeedEntityWithUidNamesMap(uidMap, EntityType.SALARY.getName());

    // 薪资：年薪
    List<SalaryNode> annualSalaryNodes = new ArrayList<>();
    List<String>  annualSalary = Arrays.asList("10万以下","10-20万",	"20-30万",
        "30-50万	", "50-100万","100万以上");
    for (String salary: annualSalary) {
      String unique = EntityType.ANNUAL.getName() + ":" + salary;
      SalaryNode salaryNode = new SalaryNode();
      salaryNode.setName(unique);
      salaryNode.setUnique_id(unique);
      salaryNode.setUnique_ids(Arrays.asList(unique));
      annualSalaryNodes.add(salaryNode);
    }
    uidMap = NodeUtil.insertEntity(dClient, salaryNodes);
    client.putFeedEntityWithUidNamesMap(uidMap, EntityType.ANNUAL.getName());


    // 工作年限
    List<String> seniors = Arrays.asList( "1年" , "1-2年"	, "2-3年", "3-5年"	, "5-10年"	, "10年以上");
    List<EntityNode> seniortyNodes = new ArrayList<>(seniors.size());
    NodeUtil.initEntityNode(seniortyNodes, seniors, EntityType.SENIORITY.getName(), dClient,
        client);


  }
  public static void main(String[] args) {

    StartMain startMain = new StartMain();
    startMain.update();
  }



}

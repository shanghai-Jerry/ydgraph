package dgraph;

import java.util.ArrayList;
import java.util.List;

import client.EntityIdClient;
import dgraph.node.Industry;
import dgraph.node.Major;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
/**
 * User: JerryYou
 *
 * Date: 2018-05-08
 *
 * Copyright (c) 2018 devops
 *
 * <<licensetext>>
 */
public class IndustryToDgraph {

  private static final Logger logger = LoggerFactory.getLogger(IndustryToDgraph.class);
  private DClient dClient;
  private EntityIdClient entityIdClient;

  public IndustryToDgraph() {
    dClient = new DClient(Config.TEST_HOSTNAME, Config.TEST_PORT);
    entityIdClient = new EntityIdClient(Config.EntityId_Host, Config.EntityIdService_PORT);
  }

  public void getIndustry(List<String> dictLines, List<Industry> industries) {
    for (String line : dictLines) {
      List<String> pNames = new ArrayList<String>();
      List<String> names = new ArrayList<>();
      Industry industry = new Industry();
      Industry partentIndustry = new Industry();
      String[] lineSplits = line.split("\t");
      if (lineSplits.length != 5) {
        System.out.println("line:" + line + ",line length:" + lineSplits.length);
      }
      String pName = lineSplits[0];
      String pCode = lineSplits[1];
      pNames.add(pName);
      pNames.add(pCode);
      String name = lineSplits[2];
      String code = lineSplits[3];
      names.add(name);
      names.add(code);
      industry.setNames(names);
      industry.setCode(Integer.parseInt(name));
      industry.setName(name);
      partentIndustry.setName(pName);
      partentIndustry.setCode(Integer.parseInt(pCode));
      partentIndustry.setNames(pNames);
      industry.setPartent_industry(partentIndustry);
      industries.add(industry);
    }
  }

  public void initWithJson(String dictPath) {

  }

  public static  void main(String []args) {

  }
}

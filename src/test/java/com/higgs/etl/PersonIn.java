package com.higgs.etl;

import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.seg.Segment;
import com.hankcs.hanlp.seg.common.Term;
import com.higgs.utils.FileUtils;
import com.higgs.utils.Util;
import com.lieluobo.kaka.KakaTokenizer;
import com.lieluobo.kaka.WordT;

import org.apache.jasper.tagplugins.jstl.core.Out;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

/**
 * User: JerryYou
 *
 * Date: 2018-07-20
 *
 * Copyright (c) 2018 devops
 *
 * <<licensetext>>
 */

public class PersonIn {

  Logger logger = LoggerFactory.getLogger(DeptDict.class);

  // bazel build kaka/proto:libtokenizer.so
  // In Mac: use java -Djava.library.path=/data/dept_norm  -jar xxx.jar has problem
  // but In Linux, it's ok
  static {
    System.load("/Users/devops/workspace/gitlab/dept_norm/libtokenizer.so");
  }

  String filePath = "/Users/devops/smile_in_life.txt";

  private KakaTokenizer tokenizer = null;

  public KakaTokenizer getTokenizer() {
    return tokenizer;
  }

  HashSet<Term> nameTerms = new HashSet<>();

  public void setTokenizer(KakaTokenizer tokenizer) {
    this.tokenizer = tokenizer;
  }

  private void recoCNname(String token, String sentence, boolean longSeg) {
    Segment segment = HanLP.newSegment().enableNameRecognize(true);
    if (longSeg) {
      List<String> kakaSegment = kakaSegment(sentence);
      for (String kaka : kakaSegment) {
        if (kaka.length() <= 1) {
          continue;
        }
        List<Term> termList = segment.seg(kaka);
        nameTerms.addAll(termList);
      }
    } else {
      List<Term> termList = segment.seg(token);
      nameTerms.addAll(termList);
    }
  }

  public List<String> kakaSegment(String sentence) {
    List<String> segs = this.tokenizer.tokenizeString(sentence, true);
    return  segs;
  }

  public void dealingText(String outPutPath) {
    List<String> dict = new ArrayList<>();
    FileUtils.readFiles(filePath, dict);
    int count = 0;
    StringBuilder allText = new StringBuilder();
    for (String ling : dict) {
      count = count + 1;
      allText.append(ling);
      if (count > 100) {
        recoCNname("", allText.toString(), true);
        allText = new StringBuilder();
        count = 0;
      }
    }
    if (count > 0) {
      recoCNname("", allText.toString(), true);
    }
    dealOutput(nameTerms, outPutPath);
  }

  public void dealOutput(HashSet<Term> nameTerms, String filePath)  {
    HashSet<String> nrOutPut = new HashSet<>();
    HashSet<String> nzoutPut = new HashSet<>();
    for (Term term : nameTerms) {
      String nature = term.nature.name();
      if ("nr".equals(nature)) {
        nrOutPut.add(term.word);
      }
      if ("nz".equals(nature)) {
        nzoutPut.add(term.word);
      }
    }
    FileUtils.saveFiles(filePath + "/nr.txt", new ArrayList<>(nrOutPut), false);
    FileUtils.saveFiles(filePath+"/nz.txt", new ArrayList<>(nzoutPut), false);

  }

  public static void main(String[] arg) {
    Logger logger = LoggerFactory.getLogger(PersonIn.class);
    PersonIn personIn = new PersonIn();
    KakaTokenizer.initConfig("/var/local/kakaseg/conf.json");
    personIn.setTokenizer(KakaTokenizer.newInstance());
    String out = "/Users/devops/NR";
    personIn.dealingText(out);
    personIn.kakaSegment("门下，你就说\n" + "到‘强逼’两字。他只道我门下个个似你一般");
  }
}

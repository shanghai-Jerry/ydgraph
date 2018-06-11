package com.higgs.dgraph.query;

import java.util.List;

import com.higgs.dgraph.node.Candidate;

/**
 * User: JerryYou
 *
 * Date: 2018-05-24
 *
 * Copyright (c) 2018 devops
 *
 * <<licensetext>>
 */
public class CandidateQuery {
  List<Candidate> candidates;

  public List<Candidate> getCandidates() {
    return candidates;
  }

  public void setCandidates(List<Candidate> candidates) {
    this.candidates = candidates;
  }
}

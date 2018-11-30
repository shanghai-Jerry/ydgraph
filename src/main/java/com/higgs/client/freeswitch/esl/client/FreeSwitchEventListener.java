package com.higgs.client.freeswitch.esl.client;

/**
 * User: JerryYou
 *
 * Date: 2018-11-29
 *
 * Copyright (c) 2018 devops
 *
 * <<licensetext>>
 */


import com.google.common.base.Throwables;

import org.apache.hadoop.mapreduce.Reducer;
import org.freeswitch.esl.client.IEslEventListener;
import org.freeswitch.esl.client.inbound.Client;
import org.freeswitch.esl.client.outbound.SocketClient;
import org.freeswitch.esl.client.transport.event.EslEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;

public class FreeSwitchEventListener {

  private static Logger logger = LoggerFactory.getLogger(FreeSwitchEventListener.class);

  public static void main(String[] args) {
    try {

      final Client inboudClient = new Client();

      inboudClient.addEventListener(new IEslEventListener() {
        @Override
        public void eventReceived(EslEvent eslEvent) {
          logger.info("Received:" + eslEvent.getEventHeaders().get("ASR-Response"));
        }

        @Override
        public void backgroundJobResultReceived(EslEvent eslEvent) {
          logger.info("Received:" + eslEvent.getEventHeaders().get("ASR-Response"));
        }
      });
      inboudClient.connect("172.20.0.14", 8021, "ClueCon", 10);
      inboudClient.setEventSubscriptions("plain", "asr_res_event");

    } catch (Throwable t) {
      Throwables.propagate(t);
    }
  }

}
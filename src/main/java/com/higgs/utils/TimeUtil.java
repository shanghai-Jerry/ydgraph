package com.higgs.utils;

import java.text.SimpleDateFormat;

/**
 * User: JerryYou
 *
 * Date: 2018-06-22
 *
 * Copyright (c) 2018 devops
 *
 * <<licensetext>>
 */
public class TimeUtil {

  public static  String consumeTime(long totalMills) {
    SimpleDateFormat formatter = new SimpleDateFormat("mm分:ss秒:S");
    String hms = formatter.format(totalMills);
    return hms;
  }
}

package com.higgs.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Month;
import java.time.Period;

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

  public static DateFormat getDataFormator() {
    return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
  }


  public static String consumeTime(long totalMills) {
    SimpleDateFormat formatter = new SimpleDateFormat("mm分:ss秒:S");
    String hms = formatter.format(totalMills);
    return hms;
  }

  /**
   * 使用java 8的Period的对象计算两个LocalDate对象的时间差，严格按照年、月、日计算，如：2018-03-12 与 2014-05-23 相差 3 年 9 个月 17 天
   * @param year
   * @param month
   * @param dayOfMonth
   */
  public static void calculateTimeDifferenceByPeriod(int year, Month month, int dayOfMonth) {
    LocalDate today = LocalDate.now();
    System.out.println("Today：" + today);
    LocalDate oldDate = LocalDate.of(year, month, dayOfMonth);
    System.out.println("OldDate：" + oldDate);
    Period p = Period.between(oldDate, today);
    System.out.printf("目标日期距离今天的时间差：%d 年 %d 个月 %d 天\n", p.getYears(), p.getMonths(), p.getDays());
  }

}

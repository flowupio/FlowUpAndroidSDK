/*
 * Copyright (C) 2016 Go Karumi S.L.
 */

package com.flowup.utils;

public class MetricNameUtils {

  public static String replaceDots(String value) {
    return value.replace(".", "-");
  }

  public static String replaceDashes(String value) {
    return value.replace("-", ".");
  }

  public static String findCrossMetricInfoAtPosition(int index, String metricName) {
    String[] metricNames = metricName.split(".");
    if (metricNames.length > index) {
      return replaceDots(metricNames[index]);
    }
    return null;
  }

}

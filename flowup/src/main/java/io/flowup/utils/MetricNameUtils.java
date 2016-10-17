/*
 * Copyright (C) 2016 Go Karumi S.L.
 */

package io.flowup.utils;

public class MetricNameUtils {

  public static String replaceDots(String value) {
    return value.replace(".", "-");
  }

  public static String replaceDashes(String value) {
    return value.replace("-", ".");
  }

  public static String[] split(String metricName) {
    return metricName.split("\\.");
  }
}

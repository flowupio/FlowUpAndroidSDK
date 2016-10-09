/*
 * Copyright (C) 2016 Go Karumi S.L.
 */

package com.flowup.metricnames;

import com.flowup.utils.MetricNameUtils;

import static com.flowup.utils.MetricNameUtils.replaceDashes;

public class MetricNamesExtractor {

  public String getAppPackage(String metricName) {
    return MetricNameUtils.replaceDashes(findCrossMetricInfoAtPosition(0, metricName));
  }

  public String getUUID(String metricName) {
    return findCrossMetricInfoAtPosition(1, metricName);
  }

  public String getDeviceModel(String metricName) {
    return findCrossMetricInfoAtPosition(2, metricName);
  }

  public int getNumberOfCores(String metricName) {
    try {
      return Integer.valueOf(findCrossMetricInfoAtPosition(3, metricName));
    } catch (NumberFormatException e) {
      return 1;
    }
  }

  public String getScreenDensity(String metricName) {
    return findCrossMetricInfoAtPosition(4, metricName);
  }

  public String getScreenSize(String metricName) {
    return findCrossMetricInfoAtPosition(5, metricName);
  }

  public String getOSVersion(String metricName) {
    return findCrossMetricInfoAtPosition(6, metricName);
  }

  public String getVersionName(String metricName) {
    return replaceDashes(findCrossMetricInfoAtPosition(7, metricName));
  }

  public boolean getIsBatterSaverOn(String metricName) {
    return Boolean.valueOf(findCrossMetricInfoAtPosition(8, metricName));
  }

  public String getScreenName(String metricName) {
    return findCrossMetricInfoAtPosition(11, metricName);
  }

  public long getTimestamp(String metricName) {
    try {
      return Long.valueOf(findCrossMetricInfoAtPosition(12, metricName));
    } catch (NumberFormatException e) {
      return 0;
    }
  }

  private String findCrossMetricInfoAtPosition(int index, String metricName) {
    String[] metricNames = metricName.split("\\.");
    if (metricNames.length > index) {
      return metricNames[index];
    }
    return null;
  }
}

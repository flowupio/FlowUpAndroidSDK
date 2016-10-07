/*
 * Copyright (C) 2016 Go Karumi S.L.
 */

package com.flowup.reporter.model;

public class MetricReport {

  private final long timestamp;
  private final String appVersionName;
  private final String osVersion;
  private final boolean batterySaverOn;

  public MetricReport(long timestamp, String appVersionName, String osVersion,
      boolean batterySaverOn) {
    this.timestamp = timestamp;
    this.appVersionName = appVersionName;
    this.osVersion = osVersion;
    this.batterySaverOn = batterySaverOn;
  }

  public long getTimestamp() {
    return timestamp;
  }

  public String getAppVersionName() {
    return appVersionName;
  }

  public String getOsVersion() {
    return osVersion;
  }

  public boolean isBatterySaverOn() {
    return batterySaverOn;
  }
}

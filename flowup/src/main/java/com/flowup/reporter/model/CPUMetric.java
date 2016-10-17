/*
 * Copyright (C) 2016 Go Karumi S.L.
 */

package com.flowup.reporter.model;

import com.google.gson.annotations.SerializedName;

public class CPUMetric extends Metric {

  @SerializedName("consumption") private final int cpuUsage;

  public CPUMetric(long timestamp, String appVersionName, String osVersion, boolean batterySaverOn,
      int cpuUsage) {
    super(timestamp, appVersionName, osVersion, batterySaverOn);
    this.cpuUsage = cpuUsage;
  }

  public int getCpuUsage() {
    return cpuUsage;
  }

  @Override public String toString() {
    return "CPUMetric{" + "cpuUsage=" + cpuUsage + '}';
  }
}

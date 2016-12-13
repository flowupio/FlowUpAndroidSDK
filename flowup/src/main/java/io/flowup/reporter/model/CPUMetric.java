/*
 * Copyright (C) 2016 Go Karumi S.L.
 */

package io.flowup.reporter.model;

import com.google.gson.annotations.SerializedName;

public class CPUMetric extends Metric {

  @SerializedName("consumption") private final int cpuUsage;

  public CPUMetric(long timestamp, String appVersionName, String osVersion, boolean batterySaverOn,
      boolean isInBackground, int cpuUsage) {
    super(timestamp, appVersionName, osVersion, batterySaverOn, isInBackground);
    this.cpuUsage = cpuUsage;
  }

  public int getCpuUsage() {
    return cpuUsage;
  }

  @Override public String toString() {
    return "CPUMetric{" + "cpuUsage=" + cpuUsage + '}';
  }
}

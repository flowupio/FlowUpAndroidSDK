/*
 * Copyright (C) 2016 Go Karumi S.L.
 */

package com.flowup.reporter.model;

import com.google.gson.annotations.SerializedName;

public class MemoryMetric extends Metric {

  @SerializedName("consumption") private final int memoryUsage;

  public MemoryMetric(long timestamp, String appVersionName, String osVersion,
      boolean batterySaverOn, int memoryUsage) {
    super(timestamp, appVersionName, osVersion, batterySaverOn);
    this.memoryUsage = memoryUsage;
  }

  public int getMemoryUsage() {
    return memoryUsage;
  }
}

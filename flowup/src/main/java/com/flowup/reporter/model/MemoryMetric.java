/*
 * Copyright (C) 2016 Go Karumi S.L.
 */

package com.flowup.reporter.model;

import com.google.gson.annotations.SerializedName;

public class MemoryMetric extends Metric {

  @SerializedName("bytesAllocated") private final long bytesAllocated;
  @SerializedName("consumption") private final int memoryUsage;

  public MemoryMetric(long timestamp, String appVersionName, String osVersion,
      boolean batterySaverOn, long bytesAllocated, int memoryUsage) {
    super(timestamp, appVersionName, osVersion, batterySaverOn);
    this.bytesAllocated = bytesAllocated;
    this.memoryUsage = memoryUsage;
  }

  public long getBytesAllocated() {
    return bytesAllocated;
  }

  public int getMemoryUsage() {
    return memoryUsage;
  }
}

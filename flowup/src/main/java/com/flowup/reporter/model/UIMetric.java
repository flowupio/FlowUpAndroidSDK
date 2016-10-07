/*
 * Copyright (C) 2016 Go Karumi S.L.
 */

package com.flowup.reporter.model;

import com.google.gson.annotations.SerializedName;

public class UIMetric extends Metric {

  @SerializedName("frameTime") private final long frameTime;
  @SerializedName("fps") private final long framesPerSecond;

  public UIMetric(long timestamp, String appVersionName, String osVersion,
      boolean batterySaverOn, long frameTime, long framesPerSecond) {
    super(timestamp, appVersionName, osVersion, batterySaverOn);
    this.frameTime = frameTime;
    this.framesPerSecond = framesPerSecond;
  }
}

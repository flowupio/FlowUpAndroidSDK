/*
 * Copyright (C) 2016 Go Karumi S.L.
 */

package io.flowup.reporter.model;

import com.google.gson.annotations.SerializedName;

public class UIMetric extends Metric {

  @SerializedName("screen") private final String screen;
  @SerializedName("frameTime") private final StatisticalValue frameTime;
  @SerializedName("framesPerSecond") private final StatisticalValue framesPerSecond;

  public UIMetric(long timestamp, String appVersionName, String osVersion, boolean batterySaverOn,
      String screen, StatisticalValue frameTime, StatisticalValue framesPerSecond) {
    super(timestamp, appVersionName, osVersion, batterySaverOn);
    this.screen = screen;
    this.frameTime = frameTime;
    this.framesPerSecond = framesPerSecond;
  }

  public String getScreen() {
    return screen;
  }

  public StatisticalValue getFrameTime() {
    return frameTime;
  }

  public StatisticalValue getFramesPerSecond() {
    return framesPerSecond;
  }

  @Override public String toString() {
    return "UIMetric{"
        + "screen='"
        + screen
        + '\''
        + ", frameTime="
        + frameTime
        + ", framesPerSecond="
        + framesPerSecond
        + '}';
  }
}

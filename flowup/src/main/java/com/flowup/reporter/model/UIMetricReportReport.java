/*
 * Copyright (C) 2016 Go Karumi S.L.
 */

package com.flowup.reporter.model;

public class UIMetricReportReport extends MetricReport {

  private final String screen;
  private final StatisticalValue frameTime;
  private final StatisticalValue framesPerSecond;

  public UIMetricReportReport(long timestamp, String appVersionName, String osVersion,
      boolean batterySaverOn, String screen, StatisticalValue frameTime,
      StatisticalValue framesPerSecond) {
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
}

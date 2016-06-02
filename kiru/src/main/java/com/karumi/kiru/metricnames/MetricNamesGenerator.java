/*
 * Copyright (C) 2016 Go Karumi S.L.
 */

package com.karumi.kiru.metricnames;

import android.content.Context;
import com.codahale.metrics.MetricRegistry;

public class MetricNamesGenerator {

  private final App app;
  private final Device device;

  public MetricNamesGenerator(Context context) {
    this.app = new App(context);
    this.device = new Device(context);
  }

  public String getFPSMetricName() {
    return MetricRegistry.name(appendCrossMetricInfo("ui.performance.fps"));
  }

  public String getFrameTimeMetricName() {
    return MetricRegistry.name(appendCrossMetricInfo("ui.performance.frameTime"));
  }

  private String appendCrossMetricInfo(String metricName) {
    return app.getApplicationName()
        + app.getApplicationVersion()
        + device.getOSVersion()
        + device.getModel()
        + device.getScreenDensity()
        + device.getScreenSize()
        + metricName;
  }
}

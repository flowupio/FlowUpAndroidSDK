/*
 * Copyright (C) 2016 Go Karumi S.L.
 */

package com.flowup.metricnames;

import android.content.Context;
import com.codahale.metrics.MetricRegistry;

public class MetricNamesGenerator {

  private final App app;
  private final Device device;

  public MetricNamesGenerator(Context context) {
    this.app = new App(context);
    this.device = new Device(context, new UUIDGenerator(context));
  }

  public String getFPSMetricName() {
    return MetricRegistry.name(appendCrossMetricInfo("ui.performance.fps"));
  }

  public String getFrameTimeMetricName() {
    return MetricRegistry.name(appendCrossMetricInfo("ui.performance.frameTime"));
  }

  public String getHttpBytesDownloadedMetricsName() {
    return MetricRegistry.name(appendCrossMetricInfo("http.bytesDownloaded"));
  }

  public String getHttpBytesUploadedMetricsName() {
    return MetricRegistry.name(appendCrossMetricInfo("http.bytesUploaded"));
  }

  private String appendCrossMetricInfo(String metricName) {
    return app.getApplicationName()
        + "."
        + app.getApplicationVersionName()
        + "."
        + device.getOSVersion()
        + "."
        + device.getUUID()
        + "."
        + device.getModel()
        + "."
        + device.getScreenDensity()
        + "."
        + device.getScreenSize()
        + "."
        + metricName;
  }
}

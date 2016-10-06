/*
 * Copyright (C) 2016 Go Karumi S.L.
 */

package com.flowup.metricnames;

import android.app.Activity;
import android.content.Context;
import com.codahale.metrics.MetricRegistry;

public class MetricNamesGenerator {

  private final App app;
  private final Device device;

  public MetricNamesGenerator(Context context) {
    this.app = new App(context);
    this.device = new Device(context);
  }

  public String getFPSMetricName(Activity activity) {
    String activityName = getActivityName(activity);
    return MetricRegistry.name(appendCrossMetricInfo("ui." + activityName + ".performance.fps"));
  }

  public String getFrameTimeMetricName(Activity activity) {
    String activityName = getActivityName(activity);
    return MetricRegistry.name(
        appendCrossMetricInfo("ui." + activityName + ".performance.frameTime"));
  }

  public String getHttpBytesDownloadedMetricsName() {
    return MetricRegistry.name(appendCrossMetricInfo("network.traffic.bytesDownloaded"));
  }

  public String getHttpBytesUploadedMetricsName() {
    return MetricRegistry.name(appendCrossMetricInfo("network.traffic.bytesUploaded"));
  }

  private String getActivityName(Activity activity) {
    return activity.getClass().getSimpleName();
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

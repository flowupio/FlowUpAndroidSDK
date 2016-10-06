/*
 * Copyright (C) 2016 Go Karumi S.L.
 */

package com.flowup.metricnames;

import android.app.Activity;
import android.content.Context;
import com.codahale.metrics.MetricRegistry;
import com.flowup.utils.Time;

public class MetricNamesGenerator {

  private final App app;
  private final Device device;
  private final Time time;

  public MetricNamesGenerator(Context context, Time time) {
    this.app = new App(context);
    this.device = new Device(context);
    this.time = time;
  }

  public String getFPSMetricName(Activity activity) {
    String activityName = getActivityName(activity);
    return MetricRegistry.name(appendCrossMetricInfo("ui.fps." + activityName + "." + time.now()));
  }

  public String getFrameTimeMetricName(Activity activity) {
    String activityName = getActivityName(activity);
    return MetricRegistry.name(
        appendCrossMetricInfo("ui.frameTime." + activityName + "." + time.now()));
  }

  public String getHttpBytesDownloadedMetricsName() {
    return MetricRegistry.name(appendCrossMetricInfo("network.bytesDownloaded"));
  }

  public String getHttpBytesUploadedMetricsName() {
    return MetricRegistry.name(appendCrossMetricInfo("network.bytesUploaded"));
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
        + device.getNumberOfCores()
        + "."
        + device.getScreenDensity()
        + "."
        + device.getScreenSize()
        + "."
        + metricName;
  }
}

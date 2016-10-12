/*
 * Copyright (C) 2016 Go Karumi S.L.
 */

package com.flowup.metricnames;

import android.app.Activity;
import com.codahale.metrics.MetricRegistry;
import com.flowup.android.App;
import com.flowup.android.Device;
import com.flowup.utils.Time;

public class MetricNamesGenerator {

  public static final String FPS = "fps";
  public static final String FRAME_TIME = "frameTime";
  public static final String BYTES_DOWNLOADED = "bytesDownloaded";
  public static final String BYTES_UPLOADED = "bytesUploaded";
  public static final String CPU_USAGE = "cpuUsage";

  private static final String UI = "ui";
  private static final String NETWORK = "network";
  private static final String SEPARATOR = ".";

  private final App app;
  private final Device device;
  private final Time time;

  public MetricNamesGenerator(App app, Device device, Time time) {
    this.app = app;
    this.device = device;
    this.time = time;
  }

  public String getFPSMetricName(Activity activity) {
    String activityName = getActivityName(activity);
    return MetricRegistry.name(appendCrossMetricInfo(
        UI + SEPARATOR + FPS + SEPARATOR + activityName + SEPARATOR + time.now()));
  }

  public String getFrameTimeMetricName(Activity activity) {
    String activityName = getActivityName(activity);
    return MetricRegistry.name(appendCrossMetricInfo(
        UI + SEPARATOR + FRAME_TIME + SEPARATOR + activityName + SEPARATOR + time.now()));
  }

  public String getBytesDownloadedMetricName() {
    return MetricRegistry.name(appendCrossMetricInfo(NETWORK + SEPARATOR + BYTES_DOWNLOADED));
  }

  public String getBytesUploadedMetricName() {
    return MetricRegistry.name(appendCrossMetricInfo(NETWORK + SEPARATOR + BYTES_UPLOADED));
  }

  public String getCPUUsageMetricName() {
    return MetricRegistry.name(appendCrossMetricInfo(CPU_USAGE));
  }

  private String getActivityName(Activity activity) {
    return activity.getClass().getSimpleName();
  }

  private String appendCrossMetricInfo(String metricName) {
    return app.getAppPackageName()
        + SEPARATOR
        + device.getInstallationUUID()
        + SEPARATOR
        + device.getModel()
        + SEPARATOR
        + device.getNumberOfCores()
        + SEPARATOR
        + device.getScreenDensity()
        + SEPARATOR
        + device.getScreenSize()
        + SEPARATOR
        + device.getOSVersion()
        + SEPARATOR
        + app.getVersionName()
        + SEPARATOR
        + device.isBatterySaverOn()
        + SEPARATOR
        + metricName;
  }
}

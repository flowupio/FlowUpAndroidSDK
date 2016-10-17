/*
 * Copyright (C) 2016 Go Karumi S.L.
 */

package io.flowup.metricnames;

import android.app.Activity;
import com.codahale.metrics.MetricRegistry;
import io.flowup.android.App;
import io.flowup.android.Device;
import io.flowup.utils.Time;

public class MetricNamesGenerator {

  static final String FPS = "fps";
  static final String FRAME_TIME = "frameTime";
  static final String BYTES_DOWNLOADED = "bytesDownloaded";
  static final String BYTES_UPLOADED = "bytesUploaded";
  static final String CPU_USAGE = "cpuUsage";
  static final String MEMORY_USAGE = "memoryUsage";
  static final String BYTES_ALLOCATED = "bytesAllocated";
  static final String INTERNAL_STORAGE_WRITTEN_BYTES = "internalStorageWrittenBytes";
  static final String SHARED_PREFERENCES_WRITTEN_BYTES = "sharedPreferencesStorageWrittenBytes";

  private static final String UI = "ui";
  private static final String NETWORK = "network";
  private static final String SEPARATOR = ".";
  private static final String MEMORY = "memory";
  private static final String DISK = "disk";

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

  public String getMemoryUsageMetricName() {
    return MetricRegistry.name(appendCrossMetricInfo(MEMORY + SEPARATOR + MEMORY_USAGE));
  }

  public String getBytesAllocatedMetricName() {
    return MetricRegistry.name(appendCrossMetricInfo(MEMORY + SEPARATOR + BYTES_ALLOCATED));
  }

  public String getInternalStorageWrittenBytes() {
    return MetricRegistry.name(
        appendCrossMetricInfo(DISK + SEPARATOR + INTERNAL_STORAGE_WRITTEN_BYTES));
  }

  public String getSharedPreferencesWrittenBytes() {
    return MetricRegistry.name(
        appendCrossMetricInfo(DISK + SEPARATOR + SHARED_PREFERENCES_WRITTEN_BYTES));
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

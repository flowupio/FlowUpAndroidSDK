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

  static final String UI = "ui";
  static final String FRAME_TIME = "frameTime";
  static final String ON_ACTIVITY_CREATED = "onActivityCreated";
  static final String ON_ACTIVITY_STARTED = "onActivityStarted";
  static final String ON_ACTIVITY_RESUMED = "onActivityResumed";
  static final String ON_ACTIVITY_PAUSED = "onActivityPaused";
  static final String ON_ACTIVITY_STOPPED = "onActivityStopped";
  static final String ON_ACTIVITY_DESTROYED = "onActivityDestroyed";
  static final String ACTIVITY_VISIBLE = "activityVisible";
  static final String BYTES_DOWNLOADED = "bytesDownloaded";
  static final String BYTES_UPLOADED = "bytesUploaded";
  static final String CPU_USAGE = "cpuUsage";
  static final String MEMORY_USAGE = "memoryUsage";
  static final String BYTES_ALLOCATED = "bytesAllocated";
  static final String INTERNAL_STORAGE_WRITTEN_BYTES = "internalStorageWrittenBytes";
  static final String SHARED_PREFERENCES_WRITTEN_BYTES = "sharedPreferencesStorageWrittenBytes";

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

  public String getFrameTimeMetricName(Activity activity) {
    String activityName = getActivityName(activity);
    return MetricRegistry.name(appendCrossMetricInfo(
        UI + SEPARATOR + FRAME_TIME + SEPARATOR + activityName + SEPARATOR + time.now()));
  }

  public String getOnActivityCreatedMetricName(Activity activity) {
    String activityName = getActivityName(activity);
    return MetricRegistry.name(
        appendCrossMetricInfo(UI + SEPARATOR + ON_ACTIVITY_CREATED + SEPARATOR + activityName));
  }

  public String getOnActivityStartedMetricName(Activity activity) {
    String activityName = getActivityName(activity);
    return MetricRegistry.name(
        appendCrossMetricInfo(UI + SEPARATOR + ON_ACTIVITY_STARTED + SEPARATOR + activityName));
  }

  public String getOnActivityResumedMetricName(Activity activity) {
    String activityName = getActivityName(activity);
    return MetricRegistry.name(
        appendCrossMetricInfo(UI + SEPARATOR + ON_ACTIVITY_RESUMED + SEPARATOR + activityName));
  }

  public String getActivityVisibleMetricName(Activity activity) {
    String activityName = getActivityName(activity);
    return MetricRegistry.name(
        appendCrossMetricInfo(UI + SEPARATOR + ACTIVITY_VISIBLE + SEPARATOR + activityName));
  }

  public String getOnActivityPausedMetricName(Activity activity) {
    String activityName = getActivityName(activity);
    return MetricRegistry.name(
        appendCrossMetricInfo(UI + SEPARATOR + ON_ACTIVITY_PAUSED + SEPARATOR + activityName));
  }

  public String getOnActivityStoppedMetricName(Activity activity) {
    String activityName = getActivityName(activity);
    return MetricRegistry.name(
        appendCrossMetricInfo(UI + SEPARATOR + ON_ACTIVITY_STOPPED + SEPARATOR + activityName));
  }

  public String getOnActivityDestroyedMetricName(Activity activity) {
    String activityName = getActivityName(activity);
    return MetricRegistry.name(
        appendCrossMetricInfo(UI + SEPARATOR + ON_ACTIVITY_DESTROYED + SEPARATOR + activityName));
  }

  public String getBytesDownloadedMetricName(boolean isInBackground) {
    return MetricRegistry.name(
        appendCrossMetricInfo(NETWORK + SEPARATOR + BYTES_DOWNLOADED, isInBackground));
  }

  public String getBytesUploadedMetricName(boolean isInBackground) {
    return MetricRegistry.name(
        appendCrossMetricInfo(NETWORK + SEPARATOR + BYTES_UPLOADED, isInBackground));
  }

  public String getCPUUsageMetricName(boolean isInBackground) {
    return MetricRegistry.name(appendCrossMetricInfo(CPU_USAGE, isInBackground));
  }

  public String getMemoryUsageMetricName(boolean forBackground) {
    return MetricRegistry.name(
        appendCrossMetricInfo(MEMORY + SEPARATOR + MEMORY_USAGE, forBackground));
  }

  public String getBytesAllocatedMetricName(boolean forBackground) {
    return MetricRegistry.name(
        appendCrossMetricInfo(MEMORY + SEPARATOR + BYTES_ALLOCATED, forBackground));
  }

  public String getInternalStorageWrittenBytes(boolean isInBackground) {
    return MetricRegistry.name(
        appendCrossMetricInfo(DISK + SEPARATOR + INTERNAL_STORAGE_WRITTEN_BYTES, isInBackground));
  }

  public String getSharedPreferencesWrittenBytes(boolean isInBackground) {
    return MetricRegistry.name(
        appendCrossMetricInfo(DISK + SEPARATOR + SHARED_PREFERENCES_WRITTEN_BYTES, isInBackground));
  }

  private String getActivityName(Activity activity) {
    return activity.getClass().getSimpleName();
  }

  private String appendCrossMetricInfo(String metricName) {
    return appendCrossMetricInfo(metricName, false);
  }

  private String appendCrossMetricInfo(String metricName, boolean isInBackground) {
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
        + isInBackground
        + SEPARATOR
        + metricName;
  }
}

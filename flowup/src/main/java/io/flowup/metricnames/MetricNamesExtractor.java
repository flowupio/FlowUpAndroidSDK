/*
 * Copyright (C) 2016 Go Karumi S.L.
 */

package io.flowup.metricnames;

import io.flowup.utils.MetricNameUtils;

import static io.flowup.utils.MetricNameUtils.replaceDashes;

public class MetricNamesExtractor {

  public String getAppPackage(String metricName) {
    return MetricNameUtils.replaceDashes(findCrossMetricInfoAtPosition(0, metricName));
  }

  public String getInstallationUUID(String metricName) {
    return findCrossMetricInfoAtPosition(1, metricName);
  }

  public String getDeviceModel(String metricName) {
    return findCrossMetricInfoAtPosition(2, metricName);
  }

  public int getNumberOfCores(String metricName) {
    try {
      return Integer.valueOf(findCrossMetricInfoAtPosition(3, metricName));
    } catch (NumberFormatException e) {
      return 1;
    }
  }

  public String getScreenDensity(String metricName) {
    return findCrossMetricInfoAtPosition(4, metricName);
  }

  public String getScreenSize(String metricName) {
    return findCrossMetricInfoAtPosition(5, metricName);
  }

  public String getOSVersion(String metricName) {
    return findCrossMetricInfoAtPosition(6, metricName);
  }

  public String getVersionName(String metricName) {
    return replaceDashes(findCrossMetricInfoAtPosition(7, metricName));
  }

  public boolean getIsBatterSaverOn(String metricName) {
    return Boolean.valueOf(findCrossMetricInfoAtPosition(8, metricName));
  }

  public boolean getIsApplicationInBackground(String metricName) {
    return Boolean.valueOf(findCrossMetricInfoAtPosition(9, metricName));
  }

  public String getScreenName(String metricName) {
    return findCrossMetricInfoAtPosition(12, metricName);
  }

  public long getTimestamp(String metricName) {
    try {
      return Long.valueOf(findCrossMetricInfoAtPosition(13, metricName));
    } catch (NumberFormatException e) {
      return 0;
    }
  }

  public boolean isBytesDownloadedMetric(String metricName) {
    return metricName.contains(MetricNamesGenerator.BYTES_DOWNLOADED);
  }

  public boolean isCPUUsageMetric(String metricName) {
    return metricName.contains(MetricNamesGenerator.CPU_USAGE);
  }

  public boolean isBytesUploadedMetric(String metricName) {
    return metricName.contains(MetricNamesGenerator.BYTES_UPLOADED);
  }

  public boolean isUIMetric(String metricName) {
    return findCrossMetricInfoAtPosition(10, metricName).equals(MetricNamesGenerator.UI);
  }

  public boolean isFrameTimeMetric(String metricName) {
    return metricName.contains(MetricNamesGenerator.FRAME_TIME);
  }

  public boolean isOnActivityCreatedMetric(String metricName) {
    return metricName.contains(MetricNamesGenerator.ON_ACTIVITY_CREATED);
  }

  public boolean isOnActivityStartedMetric(String metricName) {
    return metricName.contains(MetricNamesGenerator.ON_ACTIVITY_STARTED);
  }

  public boolean isOnActivityResumedMetric(String metricName) {
    return metricName.contains(MetricNamesGenerator.ON_ACTIVITY_RESUMED);
  }

  public boolean isActivityVisibleMetric(String metricName) {
    return metricName.contains(MetricNamesGenerator.ACTIVITY_VISIBLE);
  }

  public boolean isOnActivityPausedMetric(String metricName) {
    return metricName.contains(MetricNamesGenerator.ON_ACTIVITY_PAUSED);
  }

  public boolean isOnActivityStoppedMetric(String metricName) {
    return metricName.contains(MetricNamesGenerator.ON_ACTIVITY_STOPPED);
  }

  public boolean isOnActivityDestroyedMetric(String metricName) {
    return metricName.contains(MetricNamesGenerator.ON_ACTIVITY_DESTROYED);
  }

  public boolean isMemoryUsageMetric(String metricName) {
    return metricName.contains(MetricNamesGenerator.MEMORY_USAGE);
  }

  public boolean isBytesAllocatedMetric(String metricName) {
    return metricName.contains(MetricNamesGenerator.BYTES_ALLOCATED);
  }

  public boolean isInternalStorageAllocatedBytesMetric(String metricName) {
    return metricName.contains(MetricNamesGenerator.INTERNAL_STORAGE_WRITTEN_BYTES);
  }

  public boolean isSharedPreferencesAllocatedBytesMetric(String metricName) {
    return metricName.contains(MetricNamesGenerator.SHARED_PREFERENCES_WRITTEN_BYTES);
  }

  private String findCrossMetricInfoAtPosition(int index, String metricName) {
    String[] metricNames = MetricNameUtils.split(metricName);
    if (metricNames.length > index) {
      return metricNames[index];
    }
    return null;
  }

}

/*
 * Copyright (C) 2016 Go Karumi S.L.
 */

package io.flowup.reporter.storage;

import io.flowup.metricnames.MetricNamesExtractor;
import io.flowup.reporter.model.CPUMetric;
import io.flowup.reporter.model.DiskMetric;
import io.flowup.reporter.model.MemoryMetric;
import io.flowup.reporter.model.NetworkMetric;
import io.flowup.reporter.model.Reports;
import io.flowup.reporter.model.StatisticalValue;
import io.flowup.reporter.model.UIMetric;
import io.flowup.utils.Mapper;
import io.flowup.utils.StatisticalValueUtils;
import io.realm.RealmList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

class RealmReportsToReportsMapper extends Mapper<List<RealmReport>, Reports> {

  private final MetricNamesExtractor extractor = new MetricNamesExtractor();

  @Override public Reports map(List<RealmReport> realmReports) {
    if (realmReports.size() == 0) {
      return null;
    }
    RealmList<RealmMetric> metrics = realmReports.get(0).getMetrics();
    List<String> reportsIds = mapReportsIds(realmReports);
    if (metrics.isEmpty()) {
      return new Reports(reportsIds);
    }
    String firstMetricName = metrics.first().getMetricName();
    String appPackage = getAppPackage(firstMetricName);
    String uuid = getUUID(firstMetricName);
    String deviceModel = getDeviceModel(firstMetricName);
    String screenDensity = getScreenDensity(firstMetricName);
    String screenSize = getScreenSize(firstMetricName);
    int numberOfCores = getNumberOfCores(firstMetricName);
    List<NetworkMetric> networkMetrics = mapNetworkMetrics(realmReports);
    List<UIMetric> uiMetrics = mapUIMetrics(realmReports);
    List<CPUMetric> cpuMetrics = mapCPUMetrics(realmReports);
    List<MemoryMetric> memoryMetrics = mapMemoryMetrics(realmReports);
    List<DiskMetric> diskMetrics = mapDiskMetrics(realmReports);
    return new Reports(reportsIds, appPackage, uuid, deviceModel, screenDensity, screenSize,
        numberOfCores, networkMetrics, uiMetrics, cpuMetrics, memoryMetrics, diskMetrics);
  }

  private List<String> mapReportsIds(List<RealmReport> realmReports) {
    List<String> ids = new LinkedList<>();
    for (int i = 0; i < realmReports.size(); i++) {
      String id = realmReports.get(i).getReportTimestamp();
      ids.add(id);
    }
    return ids;
  }

  private String getAppPackage(String metricName) {
    return extractor.getAppPackage(metricName);
  }

  private String getUUID(String metricName) {
    return extractor.getInstallationUUID(metricName);
  }

  private String getDeviceModel(String metricName) {
    return extractor.getDeviceModel(metricName);
  }

  private int getNumberOfCores(String metricName) {
    return extractor.getNumberOfCores(metricName);
  }

  private String getScreenDensity(String metricName) {
    return extractor.getScreenDensity(metricName);
  }

  private String getScreenSize(String metricName) {
    return extractor.getScreenSize(metricName);
  }

  private List<NetworkMetric> mapNetworkMetrics(List<RealmReport> reports) {
    List<NetworkMetric> networkMetricsReports = new LinkedList<>();
    Long bytesDownloaded = null;
    Long bytesUploaded = null;
    for (int i = 0; i < reports.size(); i++) {
      RealmReport report = reports.get(i);
      RealmList<RealmMetric> metrics = report.getMetrics();
      for (int j = 0; j < metrics.size(); j++) {
        RealmMetric metric = metrics.get(j);
        String metricName = metric.getMetricName();
        if (extractor.isBytesDownloadedMetric(metricName)) {
          bytesDownloaded = metric.getStatisticalValue().getValue();
        } else if (extractor.isBytesUploadedMetric(metricName)) {
          bytesUploaded = metric.getStatisticalValue().getValue();
        } else {
          continue;
        }
        String osVersion = extractor.getOSVersion(metricName);
        String versionName = extractor.getVersionName(metricName);

        boolean batterySaverOn = extractor.getIsBatterSaverOn(metricName);
        if (bytesDownloaded != null && bytesUploaded != null) {
          long reportTimestamp = Long.valueOf(report.getReportTimestamp());
          networkMetricsReports.add(
              new NetworkMetric(reportTimestamp, versionName, osVersion, batterySaverOn,
                  bytesUploaded, bytesDownloaded));
          break;
        }
      }
    }
    return networkMetricsReports;
  }

  private List<CPUMetric> mapCPUMetrics(List<RealmReport> reports) {
    List<CPUMetric> cpuMetrics = new LinkedList<>();
    for (int i = 0; i < reports.size(); i++) {
      RealmReport report = reports.get(i);
      RealmList<RealmMetric> metrics = report.getMetrics();
      for (int j = 0; j < metrics.size(); j++) {
        RealmMetric metric = metrics.get(j);
        String metricName = metric.getMetricName();
        if (extractor.isCPUUsageMetric(metricName)) {
          long reportTimestamp = Long.valueOf(report.getReportTimestamp());
          String osVersion = extractor.getOSVersion(metricName);
          String versionName = extractor.getVersionName(metricName);
          boolean batterySaverOn = extractor.getIsBatterSaverOn(metricName);
          int cpuUsage = metric.getStatisticalValue().getValue().intValue();
          cpuMetrics.add(
              new CPUMetric(reportTimestamp, versionName, osVersion, batterySaverOn, cpuUsage));
        }
      }
    }
    return cpuMetrics;
  }

  private List<MemoryMetric> mapMemoryMetrics(List<RealmReport> reports) {
    List<MemoryMetric> memoryMetrics = new LinkedList<>();
    for (int i = 0; i < reports.size(); i++) {
      RealmReport report = reports.get(i);
      RealmList<RealmMetric> metrics = report.getMetrics();
      Integer memoryUsage = null;
      Long bytesAllocated = null;
      for (int j = 0; j < metrics.size(); j++) {
        RealmMetric metric = metrics.get(j);
        String metricName = metric.getMetricName();
        if (extractor.isMemoryUsageMetric(metricName)) {
          memoryUsage = metric.getStatisticalValue().getValue().intValue();
        } else if (extractor.isBytesAllocatedMetric(metricName)) {
          bytesAllocated = metric.getStatisticalValue().getValue();
        }
        if (memoryUsage != null && bytesAllocated != null) {
          long reportTimestamp = Long.valueOf(report.getReportTimestamp());
          String osVersion = extractor.getOSVersion(metricName);
          String versionName = extractor.getVersionName(metricName);
          boolean batterySaverOn = extractor.getIsBatterSaverOn(metricName);
          memoryMetrics.add(
              new MemoryMetric(reportTimestamp, versionName, osVersion, batterySaverOn,
                  bytesAllocated, memoryUsage));
          break;
        }
      }
    }
    return memoryMetrics;
  }

  private List<DiskMetric> mapDiskMetrics(List<RealmReport> reports) {
    List<DiskMetric> diskMetrics = new LinkedList<>();
    for (int i = 0; i < reports.size(); i++) {
      RealmReport report = reports.get(i);
      RealmList<RealmMetric> metrics = report.getMetrics();
      Long internalStorageBytes = null;
      Long sharedPrefsBytes = null;
      for (int j = 0; j < metrics.size(); j++) {
        RealmMetric metric = metrics.get(j);
        String metricName = metric.getMetricName();
        if (extractor.isInternalStorageAllocatedBytesMetric(metricName)) {
          internalStorageBytes = metric.getStatisticalValue().getValue();
        } else if (extractor.isSharedPreferencesAllocatedBytesMetric(metricName)) {
          sharedPrefsBytes = metric.getStatisticalValue().getValue();
        }
        if (internalStorageBytes != null && sharedPrefsBytes != null) {
          long reportTimestamp = Long.valueOf(report.getReportTimestamp());
          String osVersion = extractor.getOSVersion(metricName);
          String versionName = extractor.getVersionName(metricName);
          boolean batterySaverOn = extractor.getIsBatterSaverOn(metricName);
          diskMetrics.add(new DiskMetric(reportTimestamp, versionName, osVersion, batterySaverOn,
              internalStorageBytes, sharedPrefsBytes));
          break;
        }
      }
    }
    return diskMetrics;
  }

  private List<UIMetric> mapUIMetrics(List<RealmReport> reports) {
    List<UIMetric> uiMetricsReports = new LinkedList<>();
    for (int i = 0; i < reports.size(); i++) {
      RealmList<RealmMetric> metrics = reports.get(i).getMetrics();
      List<UIMetric> uiMetricsPerReport = extractUIMetricsForReport(metrics);
      uiMetricsReports.addAll(uiMetricsPerReport);
    }
    return uiMetricsReports;
  }

  private List<UIMetric> extractUIMetricsForReport(RealmList<RealmMetric> metrics) {
    List<UIMetric> uiMetrics = new LinkedList<>();
    Set<String> screenNames = extractScreenNames(metrics);
    for (String screenName : screenNames) {
      UIMetric uiMetric = extractUIMetric(screenName, metrics);
      if (uiMetric != null) {
        uiMetrics.add(uiMetric);
      }
    }
    return uiMetrics;
  }

  private Set<String> extractScreenNames(RealmList<RealmMetric> metrics) {
    Set<String> screenNames = new HashSet<>();
    for (RealmMetric metric : metrics) {
      String screenName = extractor.getScreenName(metric.getMetricName());
      if (screenName != null) {
        screenNames.add(screenName);
      }
    }
    return screenNames;
  }

  private UIMetric extractUIMetric(String screenName, RealmList<RealmMetric> metrics) {
    Long timestamp = null;
    StatisticalValue frameTime = null;
    StatisticalValue onActivityCreated = null;
    StatisticalValue onActivityStarted = null;
    StatisticalValue onActivityResumed = null;
    StatisticalValue activityVisible = null;
    StatisticalValue onActivityPaused = null;
    StatisticalValue onActivityStopped = null;
    StatisticalValue onActivityDestroyed = null;
    for (int i = 0; i < metrics.size(); i++) {
      RealmMetric metric = metrics.get(i);
      String metricName = metric.getMetricName();
      if (!extractor.isUIMetric(metricName)) {
        continue;
      }
      String metricScreenName = extractor.getScreenName(metricName);
      if (metricScreenName.equals(screenName)) {
        if (extractor.isFrameTimeMetric(metricName)) {
          timestamp = extractor.getTimestamp(metricName);
          frameTime = StatisticalValueUtils.fromRealm(metric.getStatisticalValue());
        } else if (extractor.isOnActivityCreatedMetric(metricName)) {
          onActivityCreated = StatisticalValueUtils.fromRealm(metric.getStatisticalValue());
        } else if (extractor.isOnActivityStartedMetric(metricName)) {
          onActivityStarted = StatisticalValueUtils.fromRealm(metric.getStatisticalValue());
        } else if (extractor.isOnActivityResumedMetric(metricName)) {
          onActivityResumed = StatisticalValueUtils.fromRealm(metric.getStatisticalValue());
        } else if (extractor.isActivityVisibleMetric(metricName)) {
          activityVisible = StatisticalValueUtils.fromRealm(metric.getStatisticalValue());
        } else if (extractor.isOnActivityPausedMetric(metricName)) {
          onActivityPaused = StatisticalValueUtils.fromRealm(metric.getStatisticalValue());
        } else if (extractor.isOnActivityStoppedMetric(metricName)) {
          onActivityStopped = StatisticalValueUtils.fromRealm(metric.getStatisticalValue());
        } else if (extractor.isOnActivityDestroyedMetric(metricName)) {
          onActivityDestroyed = StatisticalValueUtils.fromRealm(metric.getStatisticalValue());
        }
      }
      String versionName = extractor.getVersionName(metricName);
      String osVersion = extractor.getOSVersion(metricName);
      boolean batterySaverOne = extractor.getIsBatterSaverOn(metricName);
      if (i == metrics.size() - 1 && timestamp != null) {
        return new UIMetric(timestamp, versionName, osVersion, batterySaverOne, screenName,
            frameTime, onActivityCreated, onActivityStarted, onActivityResumed, activityVisible,
            onActivityPaused, onActivityStopped, onActivityDestroyed);
      }
    }
    return null;
  }
}

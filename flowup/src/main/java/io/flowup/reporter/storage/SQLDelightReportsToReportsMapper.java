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
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

class SQLDelightReportsToReportsMapper extends Mapper<SQLDelightReports, Reports> {

  private final MetricNamesExtractor extractor = new MetricNamesExtractor();

  @Override public Reports map(SQLDelightReports sqlDelightReports) {
    if (sqlDelightReports.getReports().size() == 0) {
      return null;
    }
    List<String> reportsIds = mapReportsIds(sqlDelightReports.getReports());
    List<SQLDelightMetric> metrics = sqlDelightReports.getMetrics();
    if (metrics.isEmpty()) {
      return new Reports(reportsIds);
    }
    String firstMetricName = metrics.get(0).metric_name();
    String appPackage = getAppPackage(firstMetricName);
    String uuid = getUUID(firstMetricName);
    String deviceModel = getDeviceModel(firstMetricName);
    String screenDensity = getScreenDensity(firstMetricName);
    String screenSize = getScreenSize(firstMetricName);
    int numberOfCores = getNumberOfCores(firstMetricName);
    List<NetworkMetric> networkMetrics = mapNetworkMetrics(sqlDelightReports);
    List<UIMetric> uiMetrics = mapUIMetrics(sqlDelightReports);
    List<CPUMetric> cpuMetrics = mapCPUMetrics(sqlDelightReports);
    List<MemoryMetric> memoryMetrics = mapMemoryMetrics(sqlDelightReports);
    List<DiskMetric> diskMetrics = mapDiskMetrics(sqlDelightReports);
    return new Reports(reportsIds, appPackage, uuid, deviceModel, screenDensity, screenSize,
        numberOfCores, networkMetrics, uiMetrics, cpuMetrics, memoryMetrics, diskMetrics);
  }

  private List<String> mapReportsIds(List<SQLDelightReport> reports) {
    List<String> ids = new LinkedList<>();
    for (int i = 0; i < reports.size(); i++) {
      String id = "" + reports.get(i)._id();
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

  private List<NetworkMetric> mapNetworkMetrics(SQLDelightReports sqlDelightReports) {
    List<SQLDelightReport> reports = sqlDelightReports.getReports();
    List<SQLDelightMetric> metrics = sqlDelightReports.getMetrics();
    List<NetworkMetric> networkMetricsReports = new LinkedList<>();
    Long bytesDownloaded = null;
    Long bytesUploaded = null;
    for (int i = 0; i < reports.size(); i++) {
      SQLDelightReport report = reports.get(i);
      for (int j = 0; j < metrics.size(); j++) {
        SQLDelightMetric metric = metrics.get(j);
        if (metric.report_id() != report._id()) {
          continue;
        }
        String metricName = metric.metric_name();
        if (extractor.isBytesDownloadedMetric(metricName)) {
          bytesDownloaded = metric.value().longValue();
        } else if (extractor.isBytesUploadedMetric(metricName)) {
          bytesUploaded = metric.value().longValue();
        } else {
          continue;
        }
        String osVersion = extractor.getOSVersion(metricName);
        String versionName = extractor.getVersionName(metricName);

        boolean batterySaverOn = extractor.getIsBatterSaverOn(metricName);
        if (bytesDownloaded != null && bytesUploaded != null) {
          long reportTimestamp = Long.valueOf(report.report_timestamp());
          networkMetricsReports.add(
              new NetworkMetric(reportTimestamp, versionName, osVersion, batterySaverOn,
                  bytesUploaded, bytesDownloaded));
          break;
        }
      }
    }
    return networkMetricsReports;
  }

  private List<CPUMetric> mapCPUMetrics(SQLDelightReports sqlDelightReports) {
    List<SQLDelightReport> reports = sqlDelightReports.getReports();
    List<SQLDelightMetric> metrics = sqlDelightReports.getMetrics();
    List<CPUMetric> cpuMetrics = new LinkedList<>();
    for (int i = 0; i < reports.size(); i++) {
      SQLDelightReport report = reports.get(i);
      for (int j = 0; j < metrics.size(); j++) {
        SQLDelightMetric metric = metrics.get(j);
        if (report._id() != metric.report_id()) {
          continue;
        }
        String metricName = metric.metric_name();
        if (extractor.isCPUUsageMetric(metricName)) {
          long reportTimestamp = Long.valueOf(report.report_timestamp());
          String osVersion = extractor.getOSVersion(metricName);
          String versionName = extractor.getVersionName(metricName);
          boolean batterySaverOn = extractor.getIsBatterSaverOn(metricName);
          int cpuUsage = metric.value().intValue();
          cpuMetrics.add(
              new CPUMetric(reportTimestamp, versionName, osVersion, batterySaverOn, cpuUsage));
        }
      }
    }
    return cpuMetrics;
  }

  private List<MemoryMetric> mapMemoryMetrics(SQLDelightReports sqlDelightReports) {
    List<SQLDelightReport> reports = sqlDelightReports.getReports();
    List<SQLDelightMetric> metrics = sqlDelightReports.getMetrics();
    List<MemoryMetric> memoryMetrics = new LinkedList<>();
    for (int i = 0; i < reports.size(); i++) {
      SQLDelightReport report = reports.get(i);
      Integer memoryUsage = null;
      Long bytesAllocated = null;
      for (int j = 0; j < metrics.size(); j++) {
        SQLDelightMetric metric = metrics.get(j);
        if (metric.report_id() != report._id()) {
          continue;
        }
        String metricName = metric.metric_name();
        if (extractor.isMemoryUsageMetric(metricName)) {
          memoryUsage = metric.value().intValue();
        } else if (extractor.isBytesAllocatedMetric(metricName)) {
          bytesAllocated = metric.value().longValue();
        }
        if (memoryUsage != null && bytesAllocated != null) {
          long reportTimestamp = Long.valueOf(report.report_timestamp());
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

  private List<DiskMetric> mapDiskMetrics(SQLDelightReports sqlDelightReports) {
    List<SQLDelightReport> reports = sqlDelightReports.getReports();
    List<SQLDelightMetric> metrics = sqlDelightReports.getMetrics();
    List<DiskMetric> diskMetrics = new LinkedList<>();
    for (int i = 0; i < reports.size(); i++) {
      SQLDelightReport report = reports.get(i);
      Long internalStorageBytes = null;
      Long sharedPrefsBytes = null;
      for (int j = 0; j < metrics.size(); j++) {
        SQLDelightMetric metric = metrics.get(j);
        if (metric.report_id() != report._id()) {
          continue;
        }
        String metricName = metric.metric_name();
        if (extractor.isInternalStorageAllocatedBytesMetric(metricName)) {
          internalStorageBytes = metric.value().longValue();
        } else if (extractor.isSharedPreferencesAllocatedBytesMetric(metricName)) {
          sharedPrefsBytes = metric.value().longValue();
        }
        if (internalStorageBytes != null && sharedPrefsBytes != null) {
          long reportTimestamp = Long.valueOf(report.report_timestamp());
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

  private List<UIMetric> mapUIMetrics(SQLDelightReports sqlDelightReports) {
    List<SQLDelightReport> reports = sqlDelightReports.getReports();
    List<SQLDelightMetric> metrics = sqlDelightReports.getMetrics();
    List<UIMetric> uiMetricsReports = new LinkedList<>();
    for (int i = 0; i < reports.size(); i++) {
      SQLDelightReport report = reports.get(i);
      List<UIMetric> uiMetricsPerReport = extractUIMetricsForReport(report, metrics);
      uiMetricsReports.addAll(uiMetricsPerReport);
    }
    return uiMetricsReports;
  }

  private List<UIMetric> extractUIMetricsForReport(SQLDelightReport report,
      List<SQLDelightMetric> metrics) {
    List<UIMetric> uiMetrics = new LinkedList<>();
    Set<String> screenNames = extractScreenNames(report, metrics);
    for (String screenName : screenNames) {
      List<SQLDelightMetric> filteredMetrics = filterMetricsByReportId(report, metrics);
      UIMetric uiMetric = extractUIMetric(screenName, report, filteredMetrics);
      if (uiMetric != null) {
        uiMetrics.add(uiMetric);
      }
    }
    return uiMetrics;
  }

  private List<SQLDelightMetric> filterMetricsByReportId(SQLDelightReport report,
      List<SQLDelightMetric> metrics) {
    List<SQLDelightMetric> filteredMetrics = new LinkedList<>();
    for (SQLDelightMetric metric : metrics) {
      if (metric.report_id() == report._id()) {
        filteredMetrics.add(metric);
      }
    }
    return filteredMetrics;
  }

  private Set<String> extractScreenNames(SQLDelightReport report, List<SQLDelightMetric> metrics) {
    Set<String> screenNames = new HashSet<>();
    for (SQLDelightMetric metric : metrics) {
      if (report._id() == metric.report_id()) {
        String screenName = extractor.getScreenName(metric.metric_name());
        if (screenName != null) {
          screenNames.add(screenName);
        }
      }
    }
    return screenNames;
  }

  private UIMetric extractUIMetric(String screenName, SQLDelightReport report,
      List<SQLDelightMetric> metrics) {
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
      SQLDelightMetric metric = metrics.get(i);
      if (report._id() != metric.report_id()) {
        continue;
      }
      String metricName = metric.metric_name();
      if (!extractor.isUIMetric(metricName)) {
        continue;
      }
      String metricScreenName = extractor.getScreenName(metricName);
      if (metricScreenName.equals(screenName)) {
        if (extractor.isFrameTimeMetric(metricName)) {
          timestamp = extractor.getTimestamp(metricName);
          frameTime = StatisticalValueUtils.fromSQLDelightMetric(metric);
        } else if (extractor.isOnActivityCreatedMetric(metricName)) {
          onActivityCreated = StatisticalValueUtils.fromSQLDelightMetric(metric);
        } else if (extractor.isOnActivityStartedMetric(metricName)) {
          onActivityStarted = StatisticalValueUtils.fromSQLDelightMetric(metric);
        } else if (extractor.isOnActivityResumedMetric(metricName)) {
          onActivityResumed = StatisticalValueUtils.fromSQLDelightMetric(metric);
        } else if (extractor.isActivityVisibleMetric(metricName)) {
          activityVisible = StatisticalValueUtils.fromSQLDelightMetric(metric);
        } else if (extractor.isOnActivityPausedMetric(metricName)) {
          onActivityPaused = StatisticalValueUtils.fromSQLDelightMetric(metric);
        } else if (extractor.isOnActivityStoppedMetric(metricName)) {
          onActivityStopped = StatisticalValueUtils.fromSQLDelightMetric(metric);
        } else if (extractor.isOnActivityDestroyedMetric(metricName)) {
          onActivityDestroyed = StatisticalValueUtils.fromSQLDelightMetric(metric);
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

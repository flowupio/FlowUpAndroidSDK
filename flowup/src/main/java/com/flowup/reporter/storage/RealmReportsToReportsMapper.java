/*
 * Copyright (C) 2016 Go Karumi S.L.
 */

package com.flowup.reporter.storage;

import com.flowup.metricnames.MetricNamesExtractor;
import com.flowup.reporter.model.NetworkMetric;
import com.flowup.reporter.model.Reports;
import com.flowup.reporter.model.StatisticalValue;
import com.flowup.reporter.model.UIMetric;
import com.flowup.utils.Mapper;
import com.flowup.utils.StatisticalValueUtils;
import io.realm.RealmList;
import java.util.LinkedList;
import java.util.List;

class RealmReportsToReportsMapper extends Mapper<List<RealmReport>, Reports> {

  private final MetricNamesExtractor extractor = new MetricNamesExtractor();

  @Override public Reports map(List<RealmReport> realmReports) {
    if (realmReports.size() == 0) {
      return null;
    }
    RealmList<RealmMetric> metrics = realmReports.get(0).getMetrics();
    List<String> reportsIds = mapReportsIds(realmReports);
    if (metrics.isEmpty()) {
      return new Reports(reportsIds, null, null, null, null, null, null, null, null);
    }
    String firstMetricName = metrics.first().getMetricName();
    String appPackage = getAppPackage(firstMetricName);
    String uuid = getUUID(firstMetricName);
    String deviceModel = getDeviceModel(firstMetricName);
    String screenDensity = getScreenDensity(firstMetricName);
    String screenSize = getScreenSize(firstMetricName);
    int numberOfCores = getNumberOfCores(firstMetricName);
    List<NetworkMetric> networkMetrics = mapNetworkMetricsReport(realmReports);
    List<UIMetric> uiMetrics = mapUIMetricsReport(realmReports);
    return new Reports(reportsIds, appPackage, uuid, deviceModel, screenDensity, screenSize,
        numberOfCores, networkMetrics, uiMetrics);
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

  private List<NetworkMetric> mapNetworkMetricsReport(List<RealmReport> reports) {
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

  private List<UIMetric> mapUIMetricsReport(List<RealmReport> reports) {
    List<UIMetric> uiMetricsReports = new LinkedList<>();
    StatisticalValue frameTime = null;
    StatisticalValue framesPerSecond = null;
    for (int i = 0; i < reports.size(); i++) {
      RealmList<RealmMetric> metrics = reports.get(i).getMetrics();
      for (int j = 0; j < metrics.size(); j++) {
        RealmMetric metric = metrics.get(j);
        String metricName = metric.getMetricName();
        if (extractor.isFrameTimeMetric(metricName)) {
          frameTime = StatisticalValueUtils.fromRealm(metric.getStatisticalValue());
        } else if (extractor.isFPSMetric(metricName)) {
          framesPerSecond = StatisticalValueUtils.fromRealm(metric.getStatisticalValue());
        } else {
          continue;
        }
        String versionName = extractor.getVersionName(metricName);
        String osVersion = extractor.getOSVersion(metricName);
        boolean batterySaverOne = extractor.getIsBatterSaverOn(metricName);
        String screenName = extractor.getScreenName(metricName);
        long timestamp = extractor.getTimestamp(metricName);
        if (frameTime != null && framesPerSecond != null) {
          uiMetricsReports.add(
              new UIMetric(timestamp, versionName, osVersion, batterySaverOne, screenName,
                  frameTime, framesPerSecond));
          break;
        }
      }
    }
    return uiMetricsReports;
  }
}

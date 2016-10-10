/*
 * Copyright (C) 2016 Go Karumi S.L.
 */

package com.flowup.reporter.storage;

import com.flowup.metricnames.MetricNamesExtractor;
import com.flowup.metricnames.MetricNamesGenerator;
import com.flowup.reporter.model.NetworkMetricReport;
import com.flowup.reporter.model.Reports;
import com.flowup.reporter.model.StatisticalValue;
import com.flowup.reporter.model.UIMetricReport;
import com.flowup.utils.Mapper;
import com.flowup.utils.StatisticalValueUtils;
import io.realm.RealmList;
import io.realm.RealmResults;
import java.util.LinkedList;
import java.util.List;

class RealmReportsToReportsMapper extends Mapper<RealmResults<RealmReport>, Reports> {

  private final MetricNamesExtractor extractor = new MetricNamesExtractor();

  @Override public Reports map(RealmResults<RealmReport> realmReports) {
    if (realmReports.size() == 0) {
      return null;
    }
    List<String> reportsIds = mapReportsIds(realmReports);
    String firstMetricName = realmReports.first().getMetrics().first().getMetricName();
    String appPackage = getAppPackage(firstMetricName);
    String uuid = getUUID(firstMetricName);
    String deviceModel = getDeviceModel(firstMetricName);
    String screenDensity = getScreenDensity(firstMetricName);
    String screenSize = getScreenSize(firstMetricName);
    int numberOfCores = getNumberOfCores(firstMetricName);
    List<NetworkMetricReport> networkMetrics = mapNetworkMetricsReport(realmReports);
    List<UIMetricReport> uiMetrics = mapUIMetricsReport(realmReports);
    return new Reports(reportsIds, appPackage, uuid, deviceModel, screenDensity, screenSize,
        numberOfCores, networkMetrics, uiMetrics);
  }

  private List<String> mapReportsIds(RealmResults<RealmReport> realmReports) {
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
    return extractor.getUUID(metricName);
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

  private List<NetworkMetricReport> mapNetworkMetricsReport(RealmResults<RealmReport> reports) {
    List<NetworkMetricReport> networkMetricsReports = new LinkedList<>();
    long bytesDownloaded = 0;
    long bytesUploaded = 0;
    for (int i = 0; i < reports.size(); i++) {
      RealmReport report = reports.get(i);
      RealmList<RealmMetric> metrics = report.getMetrics();
      for (int j = 0; j < metrics.size(); j++) {
        RealmMetric metric = metrics.get(j);
        String metricName = metric.getMetricName();
        if (metricName.contains(MetricNamesGenerator.BYTES_DOWNLOADED)) {
          bytesDownloaded = metric.getStatisticalValue().getValue();
        } else if (metricName.contains(MetricNamesGenerator.BYTES_UPLOADED)) {
          bytesUploaded = metric.getStatisticalValue().getValue();
        } else {
          continue;
        }
        String osVersion = extractor.getOSVersion(metricName);
        String versionName = extractor.getVersionName(metricName);

        boolean batterySaverOn = extractor.getIsBatterSaverOn(metricName);

        long reportTimestamp = Long.valueOf(report.getReportTimestamp());
        networkMetricsReports.add(
            new NetworkMetricReport(reportTimestamp, versionName, osVersion, batterySaverOn,
                bytesUploaded, bytesDownloaded));
      }
    }
    return networkMetricsReports;
  }

  private List<UIMetricReport> mapUIMetricsReport(RealmResults<RealmReport> reports) {
    List<UIMetricReport> uiMetricsReports = new LinkedList<>();
    StatisticalValue frameTime = null;
    StatisticalValue framesPerSecond = null;
    for (int i = 0; i < reports.size(); i++) {
      RealmList<RealmMetric> metrics = reports.get(i).getMetrics();
      for (int j = 0; j < metrics.size(); j++) {
        RealmMetric metric = metrics.get(j);
        String metricName = metric.getMetricName();
        if (metricName.contains(MetricNamesGenerator.FRAME_TIME)) {
          frameTime = StatisticalValueUtils.fromRealm(metric.getStatisticalValue());
        } else if (metricName.contains(MetricNamesGenerator.FPS)) {
          framesPerSecond = StatisticalValueUtils.fromRealm(metric.getStatisticalValue());
        } else {
          continue;
        }
        String versionName = extractor.getVersionName(metricName);
        String osVersion = extractor.getOSVersion(metricName);
        boolean batterySaverOne = extractor.getIsBatterSaverOn(metricName);
        String screenName = extractor.getScreenName(metricName);
        long timestamp = extractor.getTimestamp(metricName);

        uiMetricsReports.add(
            new UIMetricReport(timestamp, versionName, osVersion, batterySaverOne, screenName,
                frameTime, framesPerSecond));
      }
    }
    return uiMetricsReports;
  }
}

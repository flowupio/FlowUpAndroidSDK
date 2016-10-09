/*
 * Copyright (C) 2016 Go Karumi S.L.
 */

package com.flowup.reporter.storage;

import com.flowup.reporter.model.NetworkMetricReport;
import com.flowup.reporter.model.Reports;
import com.flowup.reporter.model.StatisticalValue;
import com.flowup.reporter.model.UIMetricReport;
import com.flowup.utils.Mapper;
import com.flowup.utils.MetricNameUtils;
import com.flowup.utils.StatisticalValueUtils;
import io.realm.RealmList;
import io.realm.RealmResults;
import java.util.LinkedList;
import java.util.List;

import static com.flowup.utils.MetricNameUtils.replaceDashes;

class RealmReportsToReportsMapper extends Mapper<RealmResults<RealmReport>, Reports> {

  @Override public Reports map(RealmResults<RealmReport> realmReports) {
    if (realmReports.size() == 0) {
      return null;
    }
    RealmReport firstRealmReport = realmReports.first();
    String firstMetricName = realmReports.first().getMetrics().first().getMetricName();
    String appPackage = getAppPackage(firstMetricName);
    String uuid = getUUID(firstMetricName);
    String deviceModel = getDeviceModel(firstMetricName);
    String screenDensity = getScreenDensity(firstMetricName);
    String screenSize = getScreenSize(firstMetricName);
    int numberOfCores = getNumberOfCores(firstMetricName);

    List<NetworkMetricReport> networkMetrics = mapNetworkMetricsReport(realmReports);
    List<UIMetricReport> uiMetrics = mapUIMetricsReport(realmReports);
    return new Reports(firstRealmReport.getReportTimestamp(), appPackage, uuid, deviceModel,
        screenDensity, screenSize, numberOfCores, networkMetrics, uiMetrics);
  }

  private String getAppPackage(String metricName) {
    return MetricNameUtils.findCrossMetricInfoAtPosition(0, metricName);
  }

  private String getUUID(String metricName) {
    return MetricNameUtils.findCrossMetricInfoAtPosition(1, metricName);
  }

  private String getDeviceModel(String metricName) {
    return MetricNameUtils.findCrossMetricInfoAtPosition(2, metricName);
  }

  private int getNumberOfCores(String metricName) {
    try {
      return Integer.valueOf(MetricNameUtils.findCrossMetricInfoAtPosition(3, metricName));
    } catch (NumberFormatException e) {
      return 1;
    }
  }

  private String getScreenDensity(String metricName) {
    return MetricNameUtils.findCrossMetricInfoAtPosition(4, metricName);
  }

  private String getScreenSize(String metricName) {
    return MetricNameUtils.findCrossMetricInfoAtPosition(5, metricName);
  }

  private List<NetworkMetricReport> mapNetworkMetricsReport(RealmResults<RealmReport> reports) {
    List<NetworkMetricReport> networkMetricsReports = new LinkedList<>();
    long bytesDownloaded = 0;
    long bytesUploaded = 0;
    for (int i = 0; i < reports.size(); i++) {
      RealmReport report = reports.get(i);
      RealmList<RealmMetricReport> metrics = report.getMetrics();
      for (int j = 0; j < metrics.size(); j++) {
        RealmMetricReport metric = metrics.get(i);
        if (metric.getMetricName().contains("bytesDownloaded")) {
          bytesDownloaded = metric.getStatisticalValue().getValue();
        }
        if (metric.getMetricName().contains("bytesDownloaded")) {
          bytesUploaded = metric.getStatisticalValue().getValue();
        }
      }
      String metricName = metrics.first().getMetricName();
      String versionName =
          replaceDashes(MetricNameUtils.findCrossMetricInfoAtPosition(1, metricName));
      String osVersion = MetricNameUtils.findCrossMetricInfoAtPosition(2, metricName);
      boolean batterySaverOne =
          Boolean.valueOf(MetricNameUtils.findCrossMetricInfoAtPosition(6, metricName));
      long reportTimestamp = Long.valueOf(report.getReportTimestamp());
      networkMetricsReports.add(
          new NetworkMetricReport(reportTimestamp, versionName, osVersion, batterySaverOne,
              bytesUploaded, bytesDownloaded));
    }
    return networkMetricsReports;
  }

  private List<UIMetricReport> mapUIMetricsReport(RealmResults<RealmReport> reports) {
    List<UIMetricReport> uiMetricsReports = new LinkedList<>();
    StatisticalValue frameTime = null;
    StatisticalValue framesPerSecond = null;
    for (int i = 0; i < reports.size(); i++) {
      RealmList<RealmMetricReport> metrics = reports.get(i).getMetrics();
      for (int j = 0; j < metrics.size(); j++) {
        RealmMetricReport metric = metrics.get(i);
        if (metric.getMetricName().contains("frameTime")) {
          frameTime = StatisticalValueUtils.fromRealm(metric.getStatisticalValue());
        }
        if (metric.getMetricName().contains("fps")) {
          framesPerSecond = StatisticalValueUtils.fromRealm(metric.getStatisticalValue());
        }
      }
      String metricName = metrics.first().getMetricName();
      String versionName =
          replaceDashes(MetricNameUtils.findCrossMetricInfoAtPosition(1, metricName));
      String osVersion = MetricNameUtils.findCrossMetricInfoAtPosition(2, metricName);
      boolean batterySaverOne =
          Boolean.valueOf(MetricNameUtils.findCrossMetricInfoAtPosition(6, metricName));
      long timestamp = Long.valueOf(MetricNameUtils.findCrossMetricInfoAtPosition(12, metricName));
      String screenName = MetricNameUtils.findCrossMetricInfoAtPosition(11, metricName);
      uiMetricsReports.add(
          new UIMetricReport(timestamp, versionName, osVersion, batterySaverOne, screenName,
              frameTime, framesPerSecond));
    }
    return uiMetricsReports;
  }
}

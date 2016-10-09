/*
 * Copyright (C) 2016 Go Karumi S.L.
 */

package com.flowup.reporter;

import com.codahale.metrics.Gauge;
import com.codahale.metrics.Histogram;
import com.codahale.metrics.Timer;
import com.flowup.reporter.model.NetworkMetricReportReport;
import com.flowup.reporter.model.Report;
import com.flowup.reporter.model.StatisticalValue;
import com.flowup.reporter.model.UIMetricReport;
import com.flowup.utils.Mapper;
import com.flowup.utils.MetricNameUtils;
import com.flowup.utils.StatisticalValueUtils;
import java.util.SortedMap;

import static com.flowup.utils.MetricNameUtils.replaceDashes;

class MetricsReportToReportMapper extends Mapper<MetricsReport, Report> {

  @Override public Report map(MetricsReport metricsReport) {
    return new Report(metricsReport.getAppPackageName(), metricsReport.getInstallationUUID(),
        metricsReport.getDeviceModel(), metricsReport.getScreenDensity(),
        metricsReport.getScreenSize(), metricsReport.getNumberOfCores(),
        getNetworkMetric(metricsReport), getUIMetric(metricsReport));
  }

  private NetworkMetricReportReport getNetworkMetric(MetricsReport metricsReport) {
    if (metricsReport.getGauges().isEmpty()) {
      return null;
    }

    long reportingTimestamp = metricsReport.getReportingTimestamp();
    return mapNetworkMetric(reportingTimestamp, metricsReport.getGauges());
  }

  private UIMetricReport getUIMetric(MetricsReport metricsReport) {
    if (metricsReport.getTimers().isEmpty() || metricsReport.getHistograms().isEmpty()) {
      return null;
    }
    return mapUIMetric(metricsReport.getHistograms(), metricsReport.getTimers());
  }

  private NetworkMetricReportReport mapNetworkMetric(long reportingTimestamp,
      SortedMap<String, Gauge> gauges) {
    long bytesUploaded = 0;
    long bytesDownloaded = 0;
    for (String metricName : gauges.keySet()) {
      if (metricName.contains("bytesUploaded")) { //TODO: Extract this into a constant
        bytesUploaded = (long) gauges.get(metricName).getValue();
      } else if (metricName.contains("bytesDownloaded")) { //TODO: Extract this into a constant
        bytesDownloaded = (long) gauges.get(metricName).getValue();
      }
    }
    String metricName = gauges.firstKey();
    String versionName =
        replaceDashes(MetricNameUtils.findCrossMetricInfoAtPosition(1, metricName));
    String osVersion = MetricNameUtils.findCrossMetricInfoAtPosition(2, metricName);
    boolean batterySaverOne =
        Boolean.valueOf(MetricNameUtils.findCrossMetricInfoAtPosition(6, metricName));
    return new NetworkMetricReportReport(reportingTimestamp, versionName, osVersion,
        batterySaverOne, bytesUploaded, bytesDownloaded);
  }

  private UIMetricReport mapUIMetric(SortedMap<String, Histogram> histograms,
      SortedMap<String, Timer> timers) {
    StatisticalValue frameTime = null;
    StatisticalValue framesPerSecond = null;
    for (String metricName : timers.keySet()) {
      if (metricName.contains("frameTime")) { //TODO: Extract this into a constant
        frameTime = StatisticalValueUtils.fromSampling(timers.get(metricName));
      }
    }
    for (String metricName : histograms.keySet()) {
      if (metricName.contains("fps")) { //TODO: Extract this into a constant
        framesPerSecond = StatisticalValueUtils.fromSampling(histograms.get(metricName));
      }
    }
    String metricName = timers.firstKey();
    String versionName =
        replaceDashes(MetricNameUtils.findCrossMetricInfoAtPosition(1, metricName));
    String osVersion = MetricNameUtils.findCrossMetricInfoAtPosition(2, metricName);
    boolean batterySaverOne =
        Boolean.valueOf(MetricNameUtils.findCrossMetricInfoAtPosition(6, metricName));
    long timestamp = Long.valueOf(MetricNameUtils.findCrossMetricInfoAtPosition(12, metricName));
    String screenName = MetricNameUtils.findCrossMetricInfoAtPosition(11, metricName);
    return new UIMetricReport(timestamp, versionName, osVersion, batterySaverOne, screenName,
        frameTime, framesPerSecond);
  }
}

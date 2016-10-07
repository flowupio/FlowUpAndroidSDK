package com.flowup.reporter;

import com.codahale.metrics.Gauge;
import com.flowup.reporter.model.NetworkMetric;
import com.flowup.reporter.model.Report;
import com.flowup.reporter.model.UIMetric;
import com.flowup.utils.Mapper;
import com.flowup.utils.MetricNameUtils;
import java.util.SortedMap;

class MetricsReportToMetricsMapper extends Mapper<MetricsReport, Report> {

  @Override public Report map(MetricsReport metricsReport) {
    return new Report(metricsReport.getAppPackageName(), metricsReport.getInstallationUUID(),
        metricsReport.getDeviceModel(), metricsReport.getScreenDensity(),
        metricsReport.getScreenSize(), metricsReport.getNumberOfCores(),
        getNetworkMetric(metricsReport), getUIMetric(metricsReport));
  }

  private NetworkMetric getNetworkMetric(MetricsReport metricsReport) {
    if (metricsReport.getGauges().isEmpty()) {
      return null;
    }

    long reportingTimestamp = metricsReport.getReportingTimestamp();
    return mapNetworkMetric(reportingTimestamp, metricsReport.getGauges());
  }

  private NetworkMetric mapNetworkMetric(long reportingTimestamp, SortedMap<String, Gauge> gauges) {
    long bytesUploaded = 0;
    long bytesDownloaded = 0;
    for (String metricName : gauges.keySet()) {
      if (metricName.contains("bytesUploaded")) {//TODO: Extract this into a constant
        bytesUploaded = (long) gauges.get(metricName).getValue();
      } else if (metricName.contains("bytesDownloaded")) {//TODO: Extract this into a constant
        bytesDownloaded = (long) gauges.get(metricName).getValue();
      }
    }
    String metricName = gauges.firstKey();
    String versionName = MetricNameUtils.findCrossMetricInfoAtPosition(1, metricName);
    String osVersion = MetricNameUtils.findCrossMetricInfoAtPosition(2, metricName);
    boolean batterySaverOne =
        Boolean.valueOf(MetricNameUtils.findCrossMetricInfoAtPosition(6, metricName));
    return new NetworkMetric(reportingTimestamp, versionName, osVersion, batterySaverOne,
        bytesUploaded, bytesDownloaded);
  }

  private UIMetric getUIMetric(MetricsReport metrics) {
    if (metrics.getTimers().isEmpty()) {
      return null;
    }
    return null;
  }
}

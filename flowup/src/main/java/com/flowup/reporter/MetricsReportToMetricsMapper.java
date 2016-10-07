package com.flowup.reporter;

import com.codahale.metrics.Gauge;
import com.flowup.reporter.model.Metrics;
import com.flowup.reporter.model.NetworkMetric;
import com.flowup.reporter.model.UIMetric;
import com.flowup.utils.Mapper;
import com.flowup.utils.MetricNameUtils;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.SortedMap;

class MetricsReportToMetricsMapper extends Mapper<MetricsReport, Metrics> {

  @Override public Metrics map(MetricsReport metricsReport) {

    return new Metrics(metricsReport.getAppPackageName(), metricsReport.getInstallationUUID(),
        metricsReport.getDeviceModel(), metricsReport.getScreenDensity(),
        metricsReport.getScreenSize(), metricsReport.getNumberOfCores(),
        getNetworkMetrics(metricsReport), getUIMetrics(metricsReport));
  }

  private List<NetworkMetric> getNetworkMetrics(MetricsReport metricsReport) {
    long reportingTimestamp = metricsReport.getReportingTimestamp();
    List<NetworkMetric> networkMetrics = new LinkedList<>();
    mapNetworkMetric(reportingTimestamp, metricsReport.getGauges());
    return networkMetrics;
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
    String versionName = MetricNameUtils.findCrossMetricInfoAtPosition(0, metricName);
    String osVersion = MetricNameUtils.findCrossMetricInfoAtPosition(0, metricName);
    boolean batterySaverOne =
        Boolean.valueOf(MetricNameUtils.findCrossMetricInfoAtPosition(0, metricName));
    return new NetworkMetric(reportingTimestamp, versionName, osVersion, batterySaverOne,
        bytesUploaded, bytesDownloaded);
  }

  private List<UIMetric> getUIMetrics(MetricsReport metrics) {
    //TODO: Implement this shit.
    return Collections.EMPTY_LIST;
  }
}

package com.flowup.reporter;

import com.codahale.metrics.Gauge;
import com.flowup.reporter.MetricsReport;
import com.flowup.reporter.model.Metrics;
import com.flowup.reporter.model.NetworkMetric;
import com.flowup.reporter.model.UIMetric;
import com.flowup.utils.Mapper;
import java.util.LinkedList;
import java.util.List;
import java.util.SortedMap;

class MetricsReportToMetricsMapper extends Mapper<MetricsReport, Metrics> {

  @Override public Metrics map(MetricsReport metrics) {

    return new Metrics(metrics.getAppPackageName(), metrics.getInstallationUUID(),
        metrics.getDeviceModel(), metrics.getScreenDensity(), metrics.getScreenSize(),
        metrics.getNumberOfCores(), getNetworkMetrics(metrics), getUIMetrics(metrics));
  }

  private List<NetworkMetric> getNetworkMetrics(MetricsReport metrics) {
    long reportingTimestamp = metrics.getReportingTimestamp();
    List<NetworkMetric> networkMetrics = new LinkedList<>();
    SortedMap<String, Gauge> gauges = metrics.getGauges();
    for (String metricName : gauges.keySet()) {
      Gauge gauge = gauges.get(metricName);
      if (gauge.getValue() instanceof Long) {
        mapNetworkMetric(reportingTimestamp, metricName, gauge);
      }
    }
    return networkMetrics;
  }

  private void mapNetworkMetric(long reportingTimestamp, String metricName, Gauge<Long> gauge) {
    String versionName;
    String osVersion;
    boolean batterySaverOne;
    long bytesDownloaded;
    long bytesUploaded;
    //return new NetworkMetric(reportingTimestamp,versionName,osVersion,batterySaverOne,bytesUploaded,bytesDownloaded);
  }

  private List<UIMetric> getUIMetrics(MetricsReport metrics) {
    return null;
  }
}

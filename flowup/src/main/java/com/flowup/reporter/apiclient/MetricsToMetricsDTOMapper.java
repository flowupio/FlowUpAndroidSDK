package com.flowup.reporter.apiclient;

import com.flowup.reporter.Metrics;
import com.flowup.utils.Mapper;
import java.util.List;

class MetricsToMetricsDTOMapper extends Mapper<Metrics, MetricsDTO> {

  @Override public MetricsDTO map(Metrics metrics) {

    return new MetricsDTO(metrics.getAppPackageName(), metrics.getAppVersionName(),
        metrics.getOSVersion(), metrics.getInstallationUUID(),
        metrics.getDeviceModel(), metrics.isPowerSaverEnabled(),
        metrics.getScreenDensity(), metrics.getScreenSize(),
        metrics.getNumberOfCores(), getNetworkMetrics(metrics), getUIMetrics(metrics));
  }

  private List<NetworkMetricDTO> getNetworkMetrics(Metrics metrics) {
    return null;
  }

  private List<UIMetricDTO> getUIMetrics(Metrics metrics) {
    return null;
  }
}

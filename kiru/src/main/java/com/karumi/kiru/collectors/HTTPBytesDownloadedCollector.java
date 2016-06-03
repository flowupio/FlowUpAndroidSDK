/*
 * Copyright (C) 2016 Go Karumi S.L.
 */

package com.karumi.kiru.collectors;

import android.net.TrafficStats;
import com.codahale.metrics.Gauge;
import com.codahale.metrics.MetricRegistry;
import com.karumi.kiru.metricnames.MetricNamesGenerator;

class HttpBytesDownloadedCollector implements Collector {

  private final MetricNamesGenerator metricNamesGenerator;

  private long lastBytesSample;

  HttpBytesDownloadedCollector(MetricNamesGenerator metricNamesGenerator) {
    this.metricNamesGenerator = metricNamesGenerator;
  }

  @Override public void initialize(MetricRegistry registry) {
    registry.register(metricNamesGenerator.getHttpBytesDownloadedMetricsName(), new Gauge<Long>() {
      @Override public Long getValue() {
        long totalRxBytes = TrafficStats.getTotalRxBytes();
        long rxBytes = totalRxBytes - lastBytesSample;
        lastBytesSample = totalRxBytes;
        return rxBytes;
      }
    });
  }
}

/*
 * Copyright (C) 2016 Go Karumi S.L.
 */

package com.flowup.collectors;

import android.net.TrafficStats;
import android.os.Process;
import android.util.Log;
import com.codahale.metrics.Gauge;
import com.codahale.metrics.MetricRegistry;
import com.flowup.metricnames.MetricNamesGenerator;

class HttpBytesDownloadedCollector implements Collector {

  private final MetricNamesGenerator metricNamesGenerator;

  private long lastBytesSample;

  HttpBytesDownloadedCollector(MetricNamesGenerator metricNamesGenerator) {
    this.metricNamesGenerator = metricNamesGenerator;
  }

  @Override public void initialize(MetricRegistry registry) {
    registry.register(metricNamesGenerator.getHttpBytesDownloadedMetricsName(), new Gauge<Long>() {
      @Override public Long getValue() {
        int applicationUid = Process.myUid();
        long totalRxBytes = TrafficStats.getUidRxBytes(applicationUid);
        long rxBytes = totalRxBytes - lastBytesSample;
        lastBytesSample = totalRxBytes;
        Log.d("FlowUp", "Collecting http bytes downloaded metric-> " + rxBytes);
        return rxBytes;
      }
    });
  }
}

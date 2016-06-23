/*
 * Copyright (C) 2016 Go Karumi S.L.
 */

package com.karumi.kiru.collectors;

import android.net.TrafficStats;
import android.os.Process;
import android.util.Log;
import com.codahale.metrics.Gauge;
import com.codahale.metrics.MetricRegistry;
import com.karumi.kiru.metricnames.MetricNamesGenerator;

class HttpBytesUploadedCollector implements Collector {

  private final MetricNamesGenerator metricNamesGenerator;

  private long lastBytesSample;

  HttpBytesUploadedCollector(MetricNamesGenerator metricNamesGenerator) {
    this.metricNamesGenerator = metricNamesGenerator;
  }

  @Override public void initialize(MetricRegistry registry) {
    registry.register(metricNamesGenerator.getHttpBytesUploadedMetricsName(), new Gauge<Long>() {
      @Override public Long getValue() {
        int applicationUid = Process.myUid();
        long totalTxBytes = TrafficStats.getUidTxBytes(applicationUid);
        long txBytes = totalTxBytes - lastBytesSample;
        lastBytesSample = totalTxBytes;
        Log.d("KIRU", "Collecting http bytes uploaded metric-> " + txBytes);
        return txBytes;
      }
    });
  }
}

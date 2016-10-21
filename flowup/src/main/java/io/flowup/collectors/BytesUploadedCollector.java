/*
 * Copyright (C) 2016 Go Karumi S.L.
 */

package io.flowup.collectors;

import android.net.TrafficStats;
import android.os.Process;
import com.codahale.metrics.CachedGauge;
import com.codahale.metrics.MetricRegistry;
import io.flowup.metricnames.MetricNamesGenerator;
import java.util.concurrent.TimeUnit;

class BytesUploadedCollector implements Collector {

  private final MetricNamesGenerator metricNamesGenerator;
  private final long samplingInterval;
  private final TimeUnit timeUnit;

  private long lastBytesSample;

  BytesUploadedCollector(MetricNamesGenerator metricNamesGenerator, long samplingInterval,
      TimeUnit timeUnit) {
    this.metricNamesGenerator = metricNamesGenerator;
    this.samplingInterval = samplingInterval;
    this.timeUnit = timeUnit;
  }

  @Override public void initialize(MetricRegistry registry) {
    registry.register(metricNamesGenerator.getBytesUploadedMetricName(),
        new CachedGauge<Long>(samplingInterval, timeUnit) {
          @Override public Long loadValue() {
            int applicationUid = Process.myUid();
            long totalTxBytes = TrafficStats.getUidTxBytes(applicationUid);
            long txBytes = totalTxBytes - lastBytesSample;
            lastBytesSample = totalTxBytes;
            return txBytes;
          }
        });
  }
}

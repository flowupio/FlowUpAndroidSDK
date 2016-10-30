/*
 * Copyright (C) 2016 Go Karumi S.L.
 */

package io.flowup.collectors;

import android.net.TrafficStats;
import android.os.Process;
import com.codahale.metrics.CachedGauge;
import com.codahale.metrics.MetricRegistry;
import io.flowup.android.AppTrafficStats;
import io.flowup.metricnames.MetricNamesGenerator;
import io.flowup.utils.TrafficStatsUtils;
import java.util.concurrent.TimeUnit;

class NetworkUsageCollector implements Collector {

  private final AppTrafficStats appTrafficStats;
  private final MetricNamesGenerator metricNamesGenerator;
  private final long samplingInterval;
  private final TimeUnit timeUnit;

  private Long lastTxSampleInBytes;
  private Long lastRxSampleInBytes;

  NetworkUsageCollector(AppTrafficStats appTrafficStats, MetricNamesGenerator metricNamesGenerator, long samplingInterval,
      TimeUnit timeUnit) {
    this.appTrafficStats = appTrafficStats;
    this.metricNamesGenerator = metricNamesGenerator;
    this.samplingInterval = samplingInterval;
    this.timeUnit = timeUnit;
  }

  @Override public void initialize(MetricRegistry registry) {
    if (!isTrafficStatsAPISupported()) {
      return;
    }
    registry.register(metricNamesGenerator.getBytesUploadedMetricName(),
        new CachedGauge<Long>(samplingInterval, timeUnit) {
          @Override public Long loadValue() {
            long totalTxBytes = appTrafficStats.getTxBytes();
            if(lastTxSampleInBytes == null) {
              lastTxSampleInBytes = totalTxBytes;
              return null;
            }
            long txBytes = totalTxBytes - lastTxSampleInBytes;
            lastTxSampleInBytes = totalTxBytes;
            return txBytes;
          }
        });
    registry.register(metricNamesGenerator.getBytesDownloadedMetricName(),
        new CachedGauge<Long>(samplingInterval, timeUnit) {
          @Override public Long loadValue() {
            long totalRxBytes = appTrafficStats.getRxBytes();
            if(lastRxSampleInBytes == null){
              lastRxSampleInBytes = totalRxBytes;
              return null;
            }
            long rxBytes = totalRxBytes - lastRxSampleInBytes;
            lastRxSampleInBytes = totalRxBytes;
            return rxBytes;
          }
        });
  }

  private boolean isTrafficStatsAPISupported() {
    int applicationUid = Process.myUid();
    long totalRxBytes = TrafficStats.getUidRxBytes(applicationUid);
    long totalTxBytes = TrafficStats.getUidTxBytes(applicationUid);
    boolean downloadedBytesSupported = TrafficStatsUtils.isAPISupported(totalRxBytes);
    boolean uploadedBytesSupported = TrafficStatsUtils.isAPISupported(totalTxBytes);
    return downloadedBytesSupported && uploadedBytesSupported;
  }
}

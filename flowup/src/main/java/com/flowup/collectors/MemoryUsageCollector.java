package com.flowup.collectors;

import com.codahale.metrics.CachedGauge;
import com.codahale.metrics.MetricRegistry;
import com.flowup.android.Device;
import com.flowup.metricnames.MetricNamesGenerator;
import java.util.concurrent.TimeUnit;

class MemoryUsageCollector implements Collector {

  private final MetricNamesGenerator metricNamesGenerator;
  private final long samplingInterval;
  private final TimeUnit timeUnit;
  private final Device device;

  MemoryUsageCollector(MetricNamesGenerator metricNamesGenerator, long samplingInterval,
      TimeUnit timeUnit, Device device) {
    this.metricNamesGenerator = metricNamesGenerator;
    this.samplingInterval = samplingInterval;
    this.timeUnit = timeUnit;
    this.device = device;
  }

  @Override public void initialize(MetricRegistry registry) {
    registry.register(metricNamesGenerator.getMemoryUsageMetricName(),
        new CachedGauge<Long>(samplingInterval, timeUnit) {
          @Override protected Long loadValue() {
            return Long.valueOf(device.getMemoryUsage());
          }
        });
  }
}

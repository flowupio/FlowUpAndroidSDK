package com.flowup.collectors;

import com.codahale.metrics.CachedGauge;
import com.codahale.metrics.MetricRegistry;
import com.flowup.android.App;
import com.flowup.android.Device;
import com.flowup.metricnames.MetricNamesGenerator;
import java.util.concurrent.TimeUnit;

class MemoryUsageCollector implements Collector {

  private final MetricNamesGenerator metricNamesGenerator;
  private final long samplingInterval;
  private final TimeUnit timeUnit;
  private final App app;

  MemoryUsageCollector(MetricNamesGenerator metricNamesGenerator, long samplingInterval,
      TimeUnit timeUnit, App app) {
    this.metricNamesGenerator = metricNamesGenerator;
    this.samplingInterval = samplingInterval;
    this.timeUnit = timeUnit;
    this.app = app;
  }

  @Override public void initialize(MetricRegistry registry) {
    registry.register(metricNamesGenerator.getMemoryUsageMetricName(),
        new CachedGauge<Long>(samplingInterval, timeUnit) {
          @Override protected Long loadValue() {
            return Long.valueOf(app.getMemoryUsage());
          }
        });
  }
}

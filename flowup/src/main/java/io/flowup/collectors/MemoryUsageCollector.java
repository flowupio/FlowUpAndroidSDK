package io.flowup.collectors;

import com.codahale.metrics.CachedGauge;
import com.codahale.metrics.MetricRegistry;
import io.flowup.android.App;
import io.flowup.metricnames.MetricNamesGenerator;
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
    registerMemoryUsageCachedGauge(registry, false);
    registerBytesAllocatedCachedGauge(registry, false);
    registerMemoryUsageCachedGauge(registry, true);
    registerBytesAllocatedCachedGauge(registry, true);
  }

  private void registerMemoryUsageCachedGauge(MetricRegistry registry,
      final boolean isInBackground) {
    registry.register(metricNamesGenerator.getMemoryUsageMetricName(isInBackground),
        new CachedGauge<Long>(samplingInterval, timeUnit) {
          @Override protected Long loadValue() {
            if ((isInBackground && app.isApplicaitonInForeground()) || (!isInBackground
                && app.isApplicationInBackground())) {
              return null;
            }
            return app.getMemoryUsage();
          }
        });
  }

  private void registerBytesAllocatedCachedGauge(MetricRegistry registry,
      final boolean isInBackground) {
    registry.register(metricNamesGenerator.getBytesAllocatedMetricName(isInBackground),
        new CachedGauge<Long>(samplingInterval, timeUnit) {
          @Override protected Long loadValue() {
            if ((isInBackground && app.isApplicaitonInForeground()) || (!isInBackground
                && app.isApplicationInBackground())) {
              return null;
            }
            return app.getBytesAllocated();
          }
        });
  }
}

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
    registry.register(metricNamesGenerator.getMemoryUsageMetricName(false),
        new CachedGauge<Long>(samplingInterval, timeUnit) {
          @Override protected Long loadValue() {
            if (app.isApplicationInBackground()) {
              return null;
            }
            return app.getMemoryUsage();
          }
        });
    registry.register(metricNamesGenerator.getBytesAllocatedMetricName(false),
        new CachedGauge<Long>(samplingInterval, timeUnit) {
          @Override protected Long loadValue() {
            if (app.isApplicationInBackground()) {
              return null;
            }
            return app.getBytesAllocated();
          }
        });
    registry.register(metricNamesGenerator.getMemoryUsageMetricName(true),
        new CachedGauge<Long>(samplingInterval, timeUnit) {
          @Override protected Long loadValue() {
            if (app.isApplicaitonInForeground()) {
              return null;
            }
            return app.getMemoryUsage();
          }
        });
    registry.register(metricNamesGenerator.getBytesAllocatedMetricName(true),
        new CachedGauge<Long>(samplingInterval, timeUnit) {
          @Override protected Long loadValue() {
            if (app.isApplicaitonInForeground()) {
              return null;
            }
            return app.getBytesAllocated();
          }
        });
  }
}

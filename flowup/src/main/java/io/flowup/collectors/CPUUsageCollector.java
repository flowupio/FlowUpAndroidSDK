/*
 * Copyright (C) 2016 Go Karumi S.L.
 */

package io.flowup.collectors;

import com.codahale.metrics.CachedGauge;
import com.codahale.metrics.MetricRegistry;
import io.flowup.android.App;
import io.flowup.android.CPU;
import io.flowup.metricnames.MetricNamesGenerator;
import java.util.concurrent.TimeUnit;

class CPUUsageCollector implements Collector {

  private final MetricNamesGenerator metricNamesGenerator;
  private final long samplingInterval;
  private final TimeUnit timeUnit;
  private final CPU cpu;
  private final App app;

  CPUUsageCollector(MetricNamesGenerator metricNamesGenerator, long samplingInterval,
      TimeUnit timeUnit, CPU cpu, App app) {
    this.metricNamesGenerator = metricNamesGenerator;
    this.samplingInterval = samplingInterval;
    this.timeUnit = timeUnit;
    this.cpu = cpu;
    this.app = app;
  }

  @Override public void initialize(MetricRegistry registry) {
    registry.register(metricNamesGenerator.getCPUUsageMetricName(false),
        new CachedGauge<Long>(samplingInterval, timeUnit) {
          @Override protected Long loadValue() {
            if(app.isApplicationInBackground()){
              return null;
            }
            return Long.valueOf(cpu.getUsage());
          }
        });
    registry.register(metricNamesGenerator.getCPUUsageMetricName(true),
        new CachedGauge<Long>(samplingInterval, timeUnit) {
          @Override protected Long loadValue() {
            if(app.isApplicaitonInForeground()) {
              return null;
            }
            return Long.valueOf(cpu.getUsage());
          }
        });
  }
}

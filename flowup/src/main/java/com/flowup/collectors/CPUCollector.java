/*
 * Copyright (C) 2016 Go Karumi S.L.
 */

package com.flowup.collectors;

import com.codahale.metrics.CachedGauge;
import com.codahale.metrics.MetricRegistry;
import com.flowup.android.CPU;
import com.flowup.metricnames.MetricNamesGenerator;
import java.util.concurrent.TimeUnit;

class CPUCollector implements Collector {

  private final MetricNamesGenerator metricNamesGenerator;
  private final long samplingInterval;
  private final TimeUnit timeUnit;
  private final CPU cpu;

  CPUCollector(MetricNamesGenerator metricNamesGenerator, long samplingInterval,
      TimeUnit timeUnit, CPU cpu) {
    this.metricNamesGenerator = metricNamesGenerator;
    this.samplingInterval = samplingInterval;
    this.timeUnit = timeUnit;
    this.cpu = cpu;
  }

  @Override public void initialize(MetricRegistry registry) {
    registry.register(metricNamesGenerator.getCPUUsageMetricName(),
        new CachedGauge<Float>(samplingInterval, timeUnit) {
          @Override protected Float loadValue() {
            return cpu.getLoad();
          }
        });
  }
}

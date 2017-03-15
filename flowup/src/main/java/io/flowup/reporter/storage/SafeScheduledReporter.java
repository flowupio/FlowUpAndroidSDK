/*
 * Copyright (C) 2016 Go Karumi S.L.
 */

package io.flowup.reporter.storage;

import com.codahale.metrics.Counter;
import com.codahale.metrics.Gauge;
import com.codahale.metrics.Histogram;
import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricFilter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.ScheduledReporter;
import com.codahale.metrics.Timer;
import io.flowup.crashreporter.SafetyNet;
import java.util.SortedMap;
import java.util.concurrent.TimeUnit;

public abstract class SafeScheduledReporter extends ScheduledReporter {

  private final SafetyNet safetyNet;

  protected SafeScheduledReporter(MetricRegistry registry, String name, MetricFilter filter,
      TimeUnit rateUnit, TimeUnit durationUnit, SafetyNet safetyNet) {
    super(registry, name, filter, rateUnit, durationUnit);
    this.safetyNet = safetyNet;
  }

  @Override public void report(final SortedMap<String, Gauge> gauges,
      final SortedMap<String, Counter> counters, final SortedMap<String, Histogram> histograms,
      final SortedMap<String, Meter> meters, final SortedMap<String, Timer> timers) {
    safetyNet.executeSafety(new Runnable() {
      @Override public void run() {
        safeReport(gauges, counters, histograms, meters, timers);
      }
    });
  }

  protected abstract void safeReport(SortedMap<String, Gauge> gauges,
      SortedMap<String, Counter> counters, SortedMap<String, Histogram> histograms,
      SortedMap<String, Meter> meters, SortedMap<String, Timer> timers);
}

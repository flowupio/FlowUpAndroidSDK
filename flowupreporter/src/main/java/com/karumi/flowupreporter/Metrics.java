/*
 * Copyright (C) 2016 Go Karumi S.L.
 */

package com.karumi.flowupreporter;

import com.codahale.metrics.Counter;
import com.codahale.metrics.Gauge;
import com.codahale.metrics.Histogram;
import com.codahale.metrics.Meter;
import com.codahale.metrics.Timer;
import java.util.SortedMap;

public class Metrics {

  private final SortedMap<String, Gauge> gauges;
  private final SortedMap<String, Counter> counters;
  private final SortedMap<String, Histogram> histograms;
  private final SortedMap<String, Meter> meters;
  private final SortedMap<String, Timer> timers;

  public Metrics(SortedMap<String, Gauge> gauges, SortedMap<String, Counter> counters,
      SortedMap<String, Histogram> histograms, SortedMap<String, Meter> meters,
      SortedMap<String, Timer> timers) {
    this.gauges = gauges;
    this.counters = counters;
    this.histograms = histograms;
    this.meters = meters;
    this.timers = timers;
  }

  public SortedMap<String, Gauge> getGauges() {
    return gauges;
  }

  public SortedMap<String, Counter> getCounters() {
    return counters;
  }

  public SortedMap<String, Histogram> getHistograms() {
    return histograms;
  }

  public SortedMap<String, Meter> getMeters() {
    return meters;
  }

  public SortedMap<String, Timer> getTimers() {
    return timers;
  }
}

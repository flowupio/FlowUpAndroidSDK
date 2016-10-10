/*
 * Copyright (C) 2016 Go Karumi S.L.
 */

package com.flowup.reporter;

import com.codahale.metrics.Counter;
import com.codahale.metrics.Gauge;
import com.codahale.metrics.Histogram;
import com.codahale.metrics.Meter;
import com.codahale.metrics.Timer;
import java.util.SortedMap;

public class DropwizardReport {

  private final long reportingTimestamp;
  private final SortedMap<String, Gauge> gauges;
  private final SortedMap<String, Counter> counters;
  private final SortedMap<String, Histogram> histograms;
  private final SortedMap<String, Meter> meters;
  private final SortedMap<String, Timer> timers;

  public DropwizardReport(long reportingTimestamp, SortedMap<String, Gauge> gauges,
      SortedMap<String, Counter> counters, SortedMap<String, Histogram> histograms,
      SortedMap<String, Meter> meters, SortedMap<String, Timer> timers) {
    this.reportingTimestamp = reportingTimestamp;
    this.gauges = gauges;
    this.counters = counters;
    this.histograms = histograms;
    this.meters = meters;
    this.timers = timers;
  }

  public long getReportingTimestamp() {
    return reportingTimestamp;
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

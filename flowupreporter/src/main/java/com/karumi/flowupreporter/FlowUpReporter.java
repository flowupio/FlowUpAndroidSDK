package com.karumi.flowupreporter;

import android.util.Log;
import com.codahale.metrics.Counter;
import com.codahale.metrics.Gauge;
import com.codahale.metrics.Histogram;
import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricFilter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.ScheduledReporter;
import com.codahale.metrics.Timer;
import java.util.SortedMap;
import java.util.concurrent.TimeUnit;

public class FlowUpReporter extends ScheduledReporter {

  private static final String LOGTAG = "FlowUpReporter";

  private final String host;
  private final int port;

  public static FlowUpReporter.Builder forRegistry(MetricRegistry registry) {
    return new FlowUpReporter.Builder(registry);
  }

  private FlowUpReporter(MetricRegistry registry, String name, MetricFilter filter,
      TimeUnit rateUnit, TimeUnit durationUnit, String host, int port) {
    super(registry, name, filter, rateUnit, durationUnit);
    this.host = host;
    this.port = port;
  }

  @Override public void report(SortedMap<String, Gauge> gauges, SortedMap<String, Counter> counters,
      SortedMap<String, Histogram> histograms, SortedMap<String, Meter> meters,
      SortedMap<String, Timer> timers) {
    logReportStarting(gauges, counters, histograms, meters, timers);
  }

  private void logReportStarting(SortedMap<String, Gauge> gauges,
      SortedMap<String, Counter> counters, SortedMap<String, Histogram> histograms,
      SortedMap<String, Meter> meters, SortedMap<String, Timer> timers) {
    Log.d(LOGTAG, "--------------------------------");
    Log.d(LOGTAG, "Time to start reporting data!!!!");
    Log.d(LOGTAG, "--------------------------------");
    Log.d(LOGTAG, "Number of gauges: " + gauges.size());
    for (String key : gauges.keySet()) {
      Long value = (Long) gauges.get(key).getValue();
      Log.d(LOGTAG, "Gauge: " + key + " received with value: " + value);
    }
    Log.d(LOGTAG, "Number of counters: " + counters.size());
    for (String key : counters.keySet()) {
      Log.d(LOGTAG, "Counter: " + key + " received with value: " + counters.get(key).getCount());
    }
    Log.d(LOGTAG, "Number of histograms: " + histograms.size());
    for (String key : histograms.keySet()) {
      Log.d(LOGTAG, "Histogram: " + key + " received with value: " + histograms.get(key).getSnapshot().getMean());
    }
    Log.d(LOGTAG, "Number of meters: " + meters.size());
    for (String key : meters.keySet()) {
      Log.d(LOGTAG, "Meter: " + key + " received with value: " + meters.get(key).getMeanRate());
    }
    Log.d(LOGTAG, "Number of timers: " + timers.size());
    for (String key : timers.keySet()) {
      Log.d(LOGTAG, "Timer: " + key + " received with value: " + timers.get(key).getSnapshot().getMean());
    }
  }

  public static final class Builder {
    private MetricRegistry registry;
    private String name;
    private MetricFilter filter;
    private TimeUnit rateUnit;
    private TimeUnit durationUnit;

    public Builder(MetricRegistry registry) {
      this.registry = registry;
      this.name = "FlowUp Reporter";
      this.filter = MetricFilter.ALL;
      this.rateUnit = TimeUnit.SECONDS;
      this.durationUnit = TimeUnit.MILLISECONDS;
    }

    public Builder name(String name) {
      this.name = name;
      return this;
    }

    public Builder filter(MetricFilter filter) {
      this.filter = filter;
      return this;
    }

    public Builder rateUnit(TimeUnit rateUnit) {
      this.rateUnit = rateUnit;
      return this;
    }

    public Builder durationUnit(TimeUnit durationUnit) {
      this.durationUnit = durationUnit;
      return this;
    }

    public FlowUpReporter build(String host, int port) {
      validateHost(host);
      validatePort(port);
      return new FlowUpReporter(registry, name, filter, rateUnit, durationUnit, host, port);
    }

    private void validateHost(String host) {
      if (host == null || host.isEmpty()) {
        throw new IllegalArgumentException("The host configured can not be used: " + host);
      }
    }

    private void validatePort(int port) {
      if (port <= 0 || port >= 49151) {
        throw new IllegalArgumentException("The port configured can not be used: " + port);
      }
    }
  }
}
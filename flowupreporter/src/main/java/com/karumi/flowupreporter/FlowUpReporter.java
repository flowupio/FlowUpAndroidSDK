/*
 * Copyright (C) 2016 Go Karumi S.L.
 */

package com.karumi.flowupreporter;

import com.codahale.metrics.Counter;
import com.codahale.metrics.Gauge;
import com.codahale.metrics.Histogram;
import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricFilter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.ScheduledReporter;
import com.codahale.metrics.Timer;
import com.karumi.flowupreporter.apiclient.ApiClient;
import com.karumi.flowupreporter.storage.MetricsStorage;
import java.util.SortedMap;
import java.util.concurrent.TimeUnit;

public class FlowUpReporter extends ScheduledReporter {

  private static final String LOGTAG = "FlowUpReporter";

  public static FlowUpReporter.Builder forRegistry(MetricRegistry registry) {
    return new FlowUpReporter.Builder(registry);
  }

  private final MetricsStorage metricsStorage;
  private final ApiClient apiClient;

  private FlowUpReporter(MetricRegistry registry, String name, MetricFilter filter,
      TimeUnit rateUnit, TimeUnit durationUnit, String host, int port, boolean persistent) {
    super(registry, name, filter, rateUnit, durationUnit);
    this.apiClient = new ApiClient(host, port);
    this.metricsStorage = new MetricsStorage(persistent);
  }

  @Override public void report(SortedMap<String, Gauge> gauges, SortedMap<String, Counter> counters,
      SortedMap<String, Histogram> histograms, SortedMap<String, Meter> meters,
      SortedMap<String, Timer> timers) {
    metricsStorage.storeMetrics(new Metrics(gauges, counters, histograms, meters, timers));
  }

  public static final class Builder {
    private MetricRegistry registry;
    private String name;
    private MetricFilter filter;
    private TimeUnit rateUnit;
    private TimeUnit durationUnit;
    private boolean persistent;

    public Builder(MetricRegistry registry) {
      this.registry = registry;
      this.name = "FlowUp Reporter";
      this.filter = MetricFilter.ALL;
      this.rateUnit = TimeUnit.SECONDS;
      this.durationUnit = TimeUnit.MILLISECONDS;
      this.persistent = true;
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
      return new FlowUpReporter(registry, name, filter, rateUnit, durationUnit, host, port,
          persistent);
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

    public Builder persistent(boolean persistent) {
      this.persistent = persistent;
      return this;
    }
  }
}
/*
 * Copyright (C) 2016 Go Karumi S.L.
 */

package com.flowup.reporter;

import android.content.Context;
import com.codahale.metrics.Counter;
import com.codahale.metrics.Gauge;
import com.codahale.metrics.Histogram;
import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricFilter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.ScheduledReporter;
import com.codahale.metrics.Timer;
import com.flowup.reporter.android.WiFiSyncServiceScheduler;
import com.flowup.reporter.apiclient.ApiClient;
import com.flowup.reporter.model.Report;
import com.flowup.reporter.storage.ReportsStorage;
import com.flowup.utils.Mapper;
import com.flowup.utils.Time;
import java.util.SortedMap;
import java.util.concurrent.TimeUnit;

public class FlowUpReporter extends ScheduledReporter {

  public static FlowUpReporter.Builder forRegistry(MetricRegistry registry, Context context) {
    return new FlowUpReporter.Builder(registry, context);
  }

  private final ReportsStorage reportsStorage;
  private final ApiClient apiClient;
  private final WiFiSyncServiceScheduler syncScheduler;
  private final Time time;
  private final Mapper<MetricsReport, Report> mapper = new MetricsReportToReportMapper();

  private FlowUpReporter(MetricRegistry registry, String name, MetricFilter filter,
      TimeUnit rateUnit, TimeUnit durationUnit, String scheme, String host, int port,
      boolean persistent, Context context, Time time) {
    super(registry, name, filter, rateUnit, durationUnit);
    this.apiClient = new ApiClient(scheme, host, port);
    this.reportsStorage = new ReportsStorage(context, persistent);
    this.syncScheduler = new WiFiSyncServiceScheduler(context);
    this.time = time;
  }

  @Override public void report(SortedMap<String, Gauge> gauges, SortedMap<String, Counter> counters,
      SortedMap<String, Histogram> histograms, SortedMap<String, Meter> meters,
      SortedMap<String, Timer> timers) {
    MetricsReport metricsReport =
        new MetricsReport(time.now(), gauges, counters, histograms, meters, timers);
    sendReport(metricsReport);
  }

  private void sendReport(MetricsReport metricsReport) {
    Report report = mapper.map(metricsReport);
    apiClient.sendMetrics(report);
  }

  public static final class Builder {

    private MetricRegistry registry;
    private String name;
    private MetricFilter filter;
    private TimeUnit rateUnit;
    private TimeUnit durationUnit;
    private boolean persistent;
    private Context context;

    public Builder(MetricRegistry registry, Context context) {
      this.registry = registry;
      this.name = "FlowUp Reporter";
      this.filter = MetricFilter.ALL;
      this.rateUnit = TimeUnit.SECONDS;
      this.durationUnit = TimeUnit.MILLISECONDS;
      this.persistent = true;
      this.context = context;
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

    public FlowUpReporter build(String scheme, String host, int port) {
      return new FlowUpReporter(registry, name, filter, rateUnit, durationUnit, scheme, host, port,
          persistent, context, new Time());
    }

    public Builder persistent(boolean persistent) {
      this.persistent = persistent;
      return this;
    }
  }
}
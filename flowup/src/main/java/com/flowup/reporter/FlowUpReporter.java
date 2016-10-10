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
import com.flowup.reporter.model.Reports;
import com.flowup.reporter.storage.ReportsStorage;
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
  private final boolean debuggable;

  private FlowUpReporter(MetricRegistry registry, String name, MetricFilter filter,
      TimeUnit rateUnit, TimeUnit durationUnit, String scheme, String host, int port,
      boolean debuggable, Context context, Time time) {
    super(registry, name, filter, rateUnit, durationUnit);
    this.apiClient = new ApiClient(scheme, host, port);
    this.reportsStorage = new ReportsStorage(context);
    this.syncScheduler = new WiFiSyncServiceScheduler(context);
    this.time = time;
    this.debuggable = debuggable;
  }

  @Override public void start(long period, TimeUnit unit) {
    super.start(period, unit);
    syncScheduler.scheduleSyncTask();
  }

  @Override public void report(SortedMap<String, Gauge> gauges, SortedMap<String, Counter> counters,
      SortedMap<String, Histogram> histograms, SortedMap<String, Meter> meters,
      SortedMap<String, Timer> timers) {
    DropwizardReport dropwizardReport =
        new DropwizardReport(time.now(), gauges, counters, histograms, meters, timers);
    storeReport(dropwizardReport);
    if (debuggable) {
      sendStoredReports();
    }
  }

  private void storeReport(DropwizardReport dropwizardReport) {
    reportsStorage.storeMetrics(dropwizardReport);
  }

  private void sendStoredReports() {
    Reports reports = reportsStorage.getReports();
    if (reports != null) {
      ReportResult result = apiClient.sendReports(reports);
      if (result.isSuccess()) {
        reportsStorage.deleteReports(reports);
      }
    }
  }

  public static final class Builder {

    private MetricRegistry registry;
    private String name;
    private MetricFilter filter;
    private TimeUnit rateUnit;
    private TimeUnit durationUnit;
    private boolean debuggable;
    private Context context;

    public Builder(MetricRegistry registry, Context context) {
      this.registry = registry;
      this.name = "FlowUp Reporter";
      this.filter = MetricFilter.ALL;
      this.rateUnit = TimeUnit.SECONDS;
      this.durationUnit = TimeUnit.MILLISECONDS;
      this.debuggable = true;
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
          debuggable, context, new Time());
    }

    public Builder debuggable(boolean debuggable) {
      this.debuggable = debuggable;
      return this;
    }
  }
}
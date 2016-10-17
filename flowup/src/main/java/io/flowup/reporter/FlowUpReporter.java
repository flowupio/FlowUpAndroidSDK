/*
 * Copyright (C) 2016 Go Karumi S.L.
 */

package io.flowup.reporter;

import android.content.Context;
import com.codahale.metrics.Counter;
import com.codahale.metrics.Gauge;
import com.codahale.metrics.Histogram;
import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricFilter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.ScheduledReporter;
import com.codahale.metrics.Timer;
import io.flowup.logger.Logger;
import io.flowup.reporter.android.WiFiSyncServiceScheduler;
import io.flowup.reporter.apiclient.ApiClient;
import io.flowup.reporter.model.Reports;
import io.flowup.reporter.storage.ReportsStorage;
import io.flowup.utils.Time;
import java.util.SortedMap;
import java.util.concurrent.TimeUnit;

public class FlowUpReporter extends ScheduledReporter {

  public static final int NUMBER_OF_REPORTS_PER_REQUEST = 251;

  public static FlowUpReporter.Builder forRegistry(MetricRegistry registry, Context context) {
    return new FlowUpReporter.Builder(registry, context);
  }

  private final ReportsStorage reportsStorage;
  private final ApiClient apiClient;
  private final WiFiSyncServiceScheduler syncScheduler;
  private final Time time;
  private final boolean forceReports;

  FlowUpReporter(MetricRegistry registry, String name, MetricFilter filter, TimeUnit rateUnit,
      TimeUnit durationUnit, ApiClient apiClient, ReportsStorage reportsStorage,
      WiFiSyncServiceScheduler syncScheduler, Time time, boolean forceReports) {
    super(registry, name, filter, rateUnit, durationUnit);
    this.apiClient = apiClient;
    this.reportsStorage = reportsStorage;
    this.syncScheduler = syncScheduler;
    this.time = time;
    this.forceReports = forceReports;
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
    if (forceReports) {
      sendStoredReports();
    }
  }

  private void storeReport(DropwizardReport dropwizardReport) {
    reportsStorage.storeMetrics(dropwizardReport);
  }

  private void sendStoredReports() {
    Logger.d("Let's start with the sync process");
    Reports reports = reportsStorage.getReports(NUMBER_OF_REPORTS_PER_REQUEST);
    if (reports == null) {
      Logger.d("There are no reports to sync.");
      return;
    }
    Logger.d(reports.getReportsIds().size() + " reports to sync");
    Logger.d(reports.toString());
    ReportResult result;
    do {
      result = apiClient.sendReports(reports);
      if (result.isSuccess()) {
        Logger.d("Api response successful");
        reportsStorage.deleteReports(reports);
      } else if (ReportResult.Error.UNAUTHORIZED == result.getError()) {
        Logger.e("Api response error: " + result.getError());
        reportsStorage.deleteReports(reports);
      } else {
        Logger.e("Api response error: " + result.getError());
      }
      reports = reportsStorage.getReports(NUMBER_OF_REPORTS_PER_REQUEST);
      if (reports != null && result.isSuccess()) {
        Logger.d("Let's continue reporting, we have "
            + reports.getReportsIds().size()
            + " reports pending");
      }
    } while (reports != null && result.isSuccess());
    ReportResult.Error error = result.getError();
    if (error == ReportResult.Error.NETWORK_ERROR) {
      Logger.e("The last sync failed due to a network error, so let's reschedule a new task");
    } else if (!result.isSuccess()) {
      Logger.e("The last sync failed due to an unknown error");
    } else {
      Logger.e("Sync process finished with a successful result");
    }
  }

  public static final class Builder {

    private MetricRegistry registry;
    private Context context;
    private String name;
    private MetricFilter filter;
    private boolean forceReports;
    private boolean logEnabled;

    public Builder(MetricRegistry registry, Context context) {
      this.registry = registry;
      this.name = "FlowUp Reporter";
      this.filter = MetricFilter.ALL;
      this.forceReports = true;
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

    public FlowUpReporter build(String apiKey, String scheme, String host, int port) {
      return new FlowUpReporter(registry, name, filter, TimeUnit.NANOSECONDS, TimeUnit.NANOSECONDS,
          new ApiClient(apiKey, scheme, host, port, logEnabled), new ReportsStorage(context),
          new WiFiSyncServiceScheduler(context, apiKey), new Time(), forceReports);
    }

    public Builder forceReports(boolean forceReports) {
      this.forceReports = forceReports;
      return this;
    }

    public Builder logEnabled(boolean logEnabled) {
      this.logEnabled = logEnabled;
      return this;
    }
  }
}
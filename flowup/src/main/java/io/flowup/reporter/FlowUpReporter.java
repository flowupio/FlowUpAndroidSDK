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
import io.flowup.android.Device;
import io.flowup.apiclient.ApiClientResult;
import io.flowup.logger.Logger;
import io.flowup.reporter.android.DeleteOldReportsServiceScheduler;
import io.flowup.reporter.android.WiFiSyncServiceScheduler;
import io.flowup.reporter.apiclient.ReportApiClient;
import io.flowup.reporter.model.Reports;
import io.flowup.reporter.storage.ReportsStorage;
import io.flowup.storage.SQLDelightfulOpenHelper;
import io.flowup.utils.Time;
import java.util.SortedMap;
import java.util.concurrent.TimeUnit;

public class FlowUpReporter extends ScheduledReporter {

  public static final int NUMBER_OF_REPORTS_PER_REQUEST = 924;

  public static FlowUpReporter.Builder forRegistry(MetricRegistry registry, Context context) {
    return new FlowUpReporter.Builder(registry, context);
  }

  private final ReportApiClient reportApiClient;
  private final ReportsStorage reportsStorage;
  private final WiFiSyncServiceScheduler syncScheduler;
  private final DeleteOldReportsServiceScheduler cleanScheduler;
  private final Time time;
  private final boolean forceReports;
  private final FlowUpReporterListener listener;

  FlowUpReporter(MetricRegistry registry, String name, MetricFilter filter, TimeUnit rateUnit,
      TimeUnit durationUnit, ReportApiClient reportApiClient, ReportsStorage reportsStorage,
      WiFiSyncServiceScheduler syncScheduler, DeleteOldReportsServiceScheduler cleanScheduler,
      Time time, boolean forceReports, FlowUpReporterListener listener) {
    super(registry, name, filter, rateUnit, durationUnit);
    this.reportApiClient = reportApiClient;
    this.reportsStorage = reportsStorage;
    this.syncScheduler = syncScheduler;
    this.cleanScheduler = cleanScheduler;
    this.time = time;
    this.forceReports = forceReports;
    this.listener = listener;
  }

  @Override public void start(long period, TimeUnit unit) {
    super.start(period, unit);
    syncScheduler.scheduleSyncTask();
    cleanScheduler.scheduleCleanTask();
  }

  @Override public void report(SortedMap<String, Gauge> gauges, SortedMap<String, Counter> counters,
      SortedMap<String, Histogram> histograms, SortedMap<String, Meter> meters,
      SortedMap<String, Timer> timers) {
    DropwizardReport dropwizardReport =
        new DropwizardReport(time.now(), gauges, counters, histograms, meters, timers);
    storeReport(dropwizardReport);
    notifyReport(dropwizardReport);
    if (forceReports) {
      sendStoredReports();
    }
  }

  private void notifyReport(DropwizardReport report) {
    if (listener != null) {
      listener.onReport(report);
    }
  }

  private void storeReport(DropwizardReport dropwizardReport) {
    reportsStorage.storeMetrics(dropwizardReport);
  }

  private void sendStoredReports() {
    Logger.d("Let's start with the sync process");
    Reports reports = reportsStorage.getReports(NUMBER_OF_REPORTS_PER_REQUEST);
    if (reports == null || reports.size() == 0) {
      Logger.d("There are no reports to sync.");
      return;
    }
    Logger.d(reports.size() + " reports to sync");
    Logger.d(reports.toString());
    ApiClientResult result;
    do {
      result = reportApiClient.sendReports(reports);
      if (result.isSuccess()) {
        Logger.d("Api response successful");
        reportsStorage.deleteReports(reports);
      } else if (shouldDeleteReportsOnError(result)) {
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
    ApiClientResult.Error error = result.getError();
    if (error == ApiClientResult.Error.NETWORK_ERROR) {
      Logger.e("The last sync failed due to a network error, so let's reschedule a new task");
    } else if (error == ApiClientResult.Error.CLIENT_DISABLED) {
      Logger.e("The client trying to report data has been disabled");
      notifyClientDisabled();
      reportsStorage.clear();
    } else if (!result.isSuccess()) {
      Logger.e("The last sync failed due to an unknown error");
    } else {
      Logger.d("Sync process finished with a successful result");
    }
  }

  private void notifyClientDisabled() {
    if (listener != null) {
      listener.onFlowUpDisabled();
    }
  }

  private boolean shouldDeleteReportsOnError(ApiClientResult result) {
    return ApiClientResult.Error.UNAUTHORIZED == result.getError()
        || ApiClientResult.Error.SERVER_ERROR == result.getError();
  }

  public static final class Builder {

    private MetricRegistry registry;
    private Context context;
    private String name;
    private MetricFilter filter;
    private boolean forceReports;
    private FlowUpReporterListener listener;

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
      Device device = new Device(context);
      Time time = new Time();
      return new FlowUpReporter(registry, name, filter, TimeUnit.NANOSECONDS, TimeUnit.NANOSECONDS,
          new ReportApiClient(apiKey, device, scheme, host, port),
          new ReportsStorage(new SQLDelightfulOpenHelper(context), time), new WiFiSyncServiceScheduler(context, apiKey),
          new DeleteOldReportsServiceScheduler(context), time, forceReports, listener);
    }

    public Builder forceReports(boolean forceReports) {
      this.forceReports = forceReports;
      return this;
    }

    public Builder listener(FlowUpReporterListener listener) {
      this.listener = listener;
      return this;
    }
  }
}
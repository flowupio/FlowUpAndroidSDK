/*
 * Copyright (C) 2016 Go Karumi S.L.
 */

package io.flowup.reporter;

import com.codahale.metrics.Counter;
import com.codahale.metrics.Gauge;
import com.codahale.metrics.Histogram;
import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricFilter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import io.flowup.reporter.android.WiFiSyncServiceScheduler;
import io.flowup.reporter.apiclient.ReporterApiClient;
import io.flowup.reporter.model.Reports;
import io.flowup.reporter.storage.ReportsStorage;
import io.flowup.utils.Time;
import java.util.Collections;
import java.util.List;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class) public class FlowUpReporterTest {

  private static final long ANY_TIMESTAMP = 0;

  @Mock private ReporterApiClient reporterApiClient;
  @Mock private ReportsStorage storage;
  @Mock private WiFiSyncServiceScheduler syncScheduler;
  @Mock private Time time;

  private FlowUpReporter givenAFlowUpReporter(boolean forceReports) {
    return new FlowUpReporter(new MetricRegistry(), "ReporterName", MetricFilter.ALL,
        TimeUnit.SECONDS, TimeUnit.MILLISECONDS, reporterApiClient, storage, syncScheduler, time,
        forceReports, null);
  }

  @Test public void storesDropwizardReportAndDoesNotInitializeSyncIfDebugIsNotEnabled() {
    FlowUpReporter reporter = givenAFlowUpReporter(false);

    DropwizardReport report = reportSomeMetrics(reporter);

    verify(storage).storeMetrics(report);
    verify(reporterApiClient, never()).sendReports(any(Reports.class));
  }

  @Test public void storesDropwizardReportAndInitializesSynProcessIfDebugIsEnabled() {
    FlowUpReporter reporter = givenAFlowUpReporter(false);

    reportSomeMetrics(reporter);

    List<String> ids = Collections.singletonList(String.valueOf(ANY_TIMESTAMP));
    verify(reporterApiClient, never()).sendReports(givenAReportsInstanceWithId(ids));
  }

  @Test public void ifTheSyncProcessIsSuccessfulTheReportsShouldBeDeleted() {
    List<String> ids = Collections.singletonList(String.valueOf(ANY_TIMESTAMP));
    Reports reportsSent = givenAReportsInstanceWithId(ids);
    givenSomeStoredReports(reportsSent);
    givenTheSyncProcessIsSuccess(reportsSent);
    FlowUpReporter reporter = givenAFlowUpReporter(true);

    reportSomeMetrics(reporter);

    verify(storage).deleteReports(reportsSent);
  }

  @Test public void sendsReportsInBatches() {
    FlowUpReporter reporter = givenAFlowUpReporter(true);
    List<String> firstBatchIds = Collections.singletonList(String.valueOf("1"));
    List<String> secondBatchIds = Collections.singletonList(String.valueOf("2"));
    Reports firstBatch = givenAReportsInstanceWithId(firstBatchIds);
    Reports secondBatch = givenAReportsInstanceWithId(secondBatchIds);
    when(storage.getReports(anyInt())).thenReturn(firstBatch, secondBatch, null);
    givenTheSyncProcessIsSuccess(firstBatch);
    givenTheSyncProcessIsSuccess(secondBatch);

    reportSomeMetrics(reporter);

    verify(reporterApiClient).sendReports(firstBatch);
    verify(reporterApiClient).sendReports(secondBatch);
  }

  @Test public void deletesEveryReportProperlySent() {
    FlowUpReporter reporter = givenAFlowUpReporter(true);
    List<String> firstBatchIds = Collections.singletonList(String.valueOf("1"));
    List<String> secondBatchIds = Collections.singletonList(String.valueOf("2"));
    Reports firstBatch = givenAReportsInstanceWithId(firstBatchIds);
    Reports secondBatch = givenAReportsInstanceWithId(secondBatchIds);
    when(storage.getReports(anyInt())).thenReturn(firstBatch, secondBatch, null);
    givenTheSyncProcessIsSuccess(firstBatch);
    givenTheSyncProcessIsSuccess(secondBatch);

    reportSomeMetrics(reporter);

    verify(storage).deleteReports(firstBatch);
    verify(storage).deleteReports(secondBatch);
  }

  @Test public void deletesJustReportsSentProperlyBecauseThereIsNoConnection() {
    FlowUpReporter reporter = givenAFlowUpReporter(true);
    List<String> firstBatchIds = Collections.singletonList(String.valueOf("1"));
    List<String> secondBatchIds = Collections.singletonList(String.valueOf("2"));
    Reports firstBatch = givenAReportsInstanceWithId(firstBatchIds);
    Reports secondBatch = givenAReportsInstanceWithId(secondBatchIds);
    when(storage.getReports(anyInt())).thenReturn(firstBatch, secondBatch, null);
    givenTheSyncProcessIsSuccess(firstBatch);
    givenTheSyncProcessFailsBecauseThereIsNoConnection(secondBatch);

    reportSomeMetrics(reporter);

    verify(storage).deleteReports(firstBatch);
    verify(storage, never()).deleteReports(secondBatch);
  }

  @Test public void deletesJustReportsSentProperlyBecauseServerFails() {
    FlowUpReporter reporter = givenAFlowUpReporter(true);
    List<String> firstBatchIds = Collections.singletonList(String.valueOf("1"));
    List<String> secondBatchIds = Collections.singletonList(String.valueOf("2"));
    Reports firstBatch = givenAReportsInstanceWithId(firstBatchIds);
    Reports secondBatch = givenAReportsInstanceWithId(secondBatchIds);
    when(storage.getReports(anyInt())).thenReturn(firstBatch, secondBatch, null);
    givenTheSyncProcessIsSuccess(firstBatch);
    givenTheSyncProcessFails(secondBatch);

    reportSomeMetrics(reporter);

    verify(storage).deleteReports(firstBatch);
    verify(storage, never()).deleteReports(secondBatch);
  }

  @Test public void evenIfThereAreReportsPendingToBeSentIfASyncRequestFailsTheReportProcessStops() {
    FlowUpReporter reporter = givenAFlowUpReporter(true);
    List<String> firstBatchIds = Collections.singletonList(String.valueOf("1"));
    List<String> secondBatchIds = Collections.singletonList(String.valueOf("2"));
    Reports firstBatch = givenAReportsInstanceWithId(firstBatchIds);
    Reports secondBatch = givenAReportsInstanceWithId(secondBatchIds);
    when(storage.getReports(anyInt())).thenReturn(firstBatch, secondBatch, null);
    givenTheSyncProcessFails(firstBatch);

    reportSomeMetrics(reporter);

    verify(storage, never()).deleteReports(firstBatch);
    verify(storage, never()).deleteReports(secondBatch);
  }

  @Test public void removesTheStoredReportsIfTheResultOfTheSyncProcessIsUnauthorized() {
    List<String> ids = Collections.singletonList(String.valueOf(ANY_TIMESTAMP));
    Reports reportsSent = givenAReportsInstanceWithId(ids);
    givenSomeStoredReports(reportsSent);
    givenTheSyncProcessReturnsUnauthorized(reportsSent);
    FlowUpReporter reporter = givenAFlowUpReporter(true);

    reportSomeMetrics(reporter);

    verify(storage).deleteReports(reportsSent);
  }

  @Test public void removesTheStoredReportsIfTheResultOfTheSyncProcessIsServerError() {
    List<String> ids = Collections.singletonList(String.valueOf(ANY_TIMESTAMP));
    Reports reportsSent = givenAReportsInstanceWithId(ids);
    givenSomeStoredReports(reportsSent);
    givenTheSyncProcessReturnsServerError(reportsSent);
    FlowUpReporter reporter = givenAFlowUpReporter(true);

    reportSomeMetrics(reporter);

    verify(storage).deleteReports(reportsSent);
  }

  private void givenSomeStoredReports(Reports reportsSent) {
    when(storage.getReports(anyInt())).thenReturn(reportsSent, null);
  }

  private void givenTheSyncProcessReturnsUnauthorized(Reports reports) {
    when(reporterApiClient.sendReports(reports)).thenReturn(
        new ReportResult(ReportResult.Error.UNAUTHORIZED));
  }

  private void givenTheSyncProcessReturnsServerError(Reports reports) {
    when(reporterApiClient.sendReports(reports)).thenReturn(
        new ReportResult(ReportResult.Error.SERVER_ERROR));
  }

  private void givenTheSyncProcessIsSuccess(Reports reports) {
    when(reporterApiClient.sendReports(reports)).thenReturn(new ReportResult(reports));
  }

  private void givenTheSyncProcessFailsBecauseThereIsNoConnection(Reports reports) {
    when(reporterApiClient.sendReports(reports)).thenReturn(
        new ReportResult(ReportResult.Error.NETWORK_ERROR));
  }

  private void givenTheSyncProcessFails(Reports reports) {
    when(reporterApiClient.sendReports(reports)).thenReturn(new ReportResult(ReportResult.Error.UNKNOWN));
  }

  private DropwizardReport report(FlowUpReporter reporter) {
    DropwizardReport report = new DropwizardReport(ANY_TIMESTAMP, new TreeMap<String, Gauge>(),
        new TreeMap<String, Counter>(), new TreeMap<String, Histogram>(),
        new TreeMap<String, Meter>(), new TreeMap<String, Timer>());
    reporter.report(report.getGauges(), report.getCounters(), report.getHistograms(),
        report.getMeters(), report.getTimers());
    return report;
  }

  private DropwizardReport reportSomeMetrics(FlowUpReporter reporter) {
    return report(reporter);
  }

  private Reports givenAReportsInstanceWithId(List<String> ids) {
    return new Reports(ids);
  }
}
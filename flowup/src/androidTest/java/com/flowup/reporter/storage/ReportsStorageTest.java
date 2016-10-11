/*
 * Copyright (C) 2016 Go Karumi S.L.
 */

package com.flowup.reporter.storage;

import android.app.Activity;
import android.content.Context;
import com.codahale.metrics.Counter;
import com.codahale.metrics.ExponentiallyDecayingReservoir;
import com.codahale.metrics.Gauge;
import com.codahale.metrics.Histogram;
import com.codahale.metrics.Meter;
import com.codahale.metrics.Timer;
import com.flowup.metricnames.App;
import com.flowup.metricnames.Device;
import com.flowup.metricnames.MetricNamesGenerator;
import com.flowup.reporter.DropwizardReport;
import com.flowup.reporter.model.NetworkMetric;
import com.flowup.reporter.model.Reports;
import com.flowup.reporter.model.UIMetric;
import com.flowup.utils.Time;
import io.realm.Realm;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;
import static org.mockito.Mockito.mock;

public class ReportsStorageTest {

  private static final double ANY_FRAME_TIME = 17;
  private static final double ANY_FRAMES_PER_SECOND = 62;
  private static final long ANY_BYTES_UPLOADED = 1024L;
  private static final long ANY_BYTES_DOWNLOADED = 1024L;
  private static final double DELTA = 0.1d;

  private ReportsStorage storage;
  private MetricNamesGenerator generator;

  @Before public void setUp() {
    Context context = getInstrumentation().getContext();
    storage = new ReportsStorage(context);
    generator = new MetricNamesGenerator(new App(context), new Device(context), new Time());
    clearRealmDB();
  }

  @After public void tearDown() {
    clearRealmDB();
  }

  @Test public void storesOneReport() {
    DropwizardReport dropwizardReport = givenAnEmptyDropwizardReport();

    Reports reports = storeAndGet(dropwizardReport);

    assertEquals(1, reports.getReportsIds().size());
  }

  @Test public void storesMoreThanOneReport() {
    int numberOfReports = 12;
    List<DropwizardReport> dropwizardReports = givenSomeEmptyDropwizardReports(numberOfReports);

    Reports reports = storeAndGet(dropwizardReports);

    assertEquals(numberOfReports, reports.getReportsIds().size());
  }

  @Test public void doesNotHaveInfoAssociatedIfTheDropwizardReportsAreEmpty() {
    DropwizardReport dropwizardReport = givenAnEmptyDropwizardReport();

    Reports reports = storeAndGet(dropwizardReport);

    assertThereAreNoReports(reports);
  }

  @Test public void returnsReportInfoBasedOnDropwizardMetricsWithOnlyNetworkMetricsReported() {
    SortedMap<String, Gauge> networkMetrics = givenANetworkMetric();
    DropwizardReport dropwizardReport = givenADropWizardReport(networkMetrics);

    Reports reports = storeAndGet(dropwizardReport);

    assertEquals(1, reports.getNetworkMetricsReports().size());
    assertEquals(0, reports.getUIMetricsReports().size());
  }

  @Test public void returnsReportInfoBasedOnDropwizardMetricsWithOnlyUIMetricsReported() {
    SortedMap<String, Histogram> fpsMetric = givenAFPSMetric();
    SortedMap<String, Timer> frameTimeMetric = givenAFrameTimeMetric();
    DropwizardReport dropwizardReport =
        givenADropWizardReport(new TreeMap<String, Gauge>(), fpsMetric, frameTimeMetric);

    Reports reports = storeAndGet(dropwizardReport);

    assertEquals(1, reports.getUIMetricsReports().size());
    assertEquals(0, reports.getNetworkMetricsReports().size());
  }

  @Test public void joinsSomeReportsIntoOneReportsInstanceWithAllTheMetricsInside() {
    int numberOfReports = 10;
    List<DropwizardReport> dropwizardReports = givenSomeDropwizardReports(numberOfReports);

    Reports reports = storeAndGet(dropwizardReports);

    assertNetworkMetricsContainsExpectedValues(numberOfReports, reports);
    assertUIMetricsContainsExpectedValues(numberOfReports, reports);
  }

  @Test public void deletesTheReportsPreviouslyObtained() {
    int numberOfReports = 10;
    List<DropwizardReport> dropwizardReports = givenSomeDropwizardReports(numberOfReports);
    Reports reports = storeAndGet(dropwizardReports);

    storage.deleteReports(reports);
    reports = storage.getReports(numberOfReports);

    assertNull(reports);
  }

  @Test public void deletesTheReportsMatchingWithTheReportId() {
    int numberOfReports = 10;
    List<DropwizardReport> dropwizardReports = givenSomeDropwizardReports(numberOfReports);
    storeAndGet(dropwizardReports);

    int numberOfReportsToRemove = numberOfReports / 2;
    Reports reportsToRemove = givenReportsWithId(numberOfReportsToRemove);
    storage.deleteReports(reportsToRemove);
    Reports restOfReports = storage.getReports(numberOfReports);

    assertEquals(numberOfReportsToRemove, restOfReports.getReportsIds().size());
  }

  @Test
  public void savesDataAssociatedToDifferentScreensAsDifferentReports() {
    SortedMap<String, Histogram> fpsMetric = givenAFPSMetric();
    SortedMap<String, Timer> frameTimeMetric = givenAFrameTimeMetric();
    DropwizardReport dropwizardReport =
        givenADropWizardReport(new TreeMap<String, Gauge>(), fpsMetric, frameTimeMetric);

    Reports reports = storeAndGet(dropwizardReport);

    assertEquals(1, reports.getUIMetricsReports().size());
    assertEquals(0, reports.getNetworkMetricsReports().size());
  }

  private Reports givenReportsWithId(int iterativeReportsId) {
    List<String> reportsIds = new LinkedList<>();
    for (int i = 0; i < iterativeReportsId; i++) {
      reportsIds.add(String.valueOf(i));
    }
    return new Reports(reportsIds, null, null, null, null, null, null, Collections.EMPTY_LIST,
        Collections.EMPTY_LIST);
  }

  private void assertNetworkMetricsContainsExpectedValues(int numberOfReports, Reports reports) {
    List<NetworkMetric> networkReports = reports.getNetworkMetricsReports();
    assertEquals(numberOfReports, networkReports.size());
    for (int i = 0; i < numberOfReports; i++) {
      assertEquals(ANY_BYTES_UPLOADED, networkReports.get(i).getBytesUploaded());
      assertEquals(ANY_BYTES_DOWNLOADED, networkReports.get(i).getBytesDownloaded());
    }
  }

  private void assertUIMetricsContainsExpectedValues(int numberOfReports, Reports reports) {
    List<UIMetric> uiReports = reports.getUIMetricsReports();
    assertEquals(numberOfReports, uiReports.size());
    for (int i = 0; i < numberOfReports; i++) {
      assertEquals(1.7E7, uiReports.get(i).getFrameTime().getMean(), DELTA);
      assertEquals(ANY_FRAMES_PER_SECOND, uiReports.get(i).getFramesPerSecond().getMean(), DELTA);
    }
  }

  private List<DropwizardReport> givenSomeDropwizardReports(int numberOfReports) {
    List<DropwizardReport> dropwizardReports = new LinkedList<>();
    for (int i = 0; i < numberOfReports; i++) {
      SortedMap<String, Gauge> networkMetrics = givenANetworkMetric();
      SortedMap<String, Histogram> fpsMetric = givenAFPSMetric();
      SortedMap<String, Timer> frameTimeMetric = givenAFrameTimeMetric();
      DropwizardReport dropwizardReport =
          givenADropWizardReport(i, networkMetrics, fpsMetric, frameTimeMetric);
      dropwizardReports.add(dropwizardReport);
    }
    return dropwizardReports;
  }

  private SortedMap<String, Timer> givenAFrameTimeMetric() {
    SortedMap<String, Timer> timers = new TreeMap<>();
    Activity activity = mock(Activity.class);
    Timer timer = new Timer();
    timer.update((long) ANY_FRAME_TIME, TimeUnit.MILLISECONDS);
    timers.put(generator.getFrameTimeMetricName(activity), timer);
    return timers;
  }

  private SortedMap<String, Histogram> givenAFPSMetric() {
    SortedMap<String, Histogram> histograms = new TreeMap<>();
    Activity activity = mock(Activity.class);
    String name = generator.getFPSMetricName(activity);
    Histogram histogram = new Histogram(new ExponentiallyDecayingReservoir());
    histogram.update((long) ANY_FRAMES_PER_SECOND);
    histograms.put(name, histogram);
    return histograms;
  }

  private DropwizardReport givenADropWizardReport(SortedMap<String, Gauge> networkMetrics) {
    return givenADropWizardReport(0, networkMetrics, new TreeMap<String, Histogram>(),
        new TreeMap<String, Timer>());
  }

  private DropwizardReport givenADropWizardReport(SortedMap<String, Gauge> networkMetrics,
      SortedMap<String, Histogram> fpsMetrics, SortedMap<String, Timer> frameTimeMetrics) {
    return givenADropWizardReport(0, networkMetrics, fpsMetrics, frameTimeMetrics);
  }

  private DropwizardReport givenADropWizardReport(long timestamp,
      SortedMap<String, Gauge> networkMetrics, SortedMap<String, Histogram> fpsMetrics,
      SortedMap<String, Timer> frameTimeMetrics) {
    SortedMap<String, Gauge> gauges = networkMetrics;
    SortedMap<String, Counter> counters = new TreeMap<>();
    SortedMap<String, Histogram> histograms = fpsMetrics;
    SortedMap<String, Meter> meters = new TreeMap<>();
    SortedMap<String, Timer> timers = frameTimeMetrics;
    return new DropwizardReport(timestamp, gauges, counters, histograms, meters, timers);
  }

  private SortedMap<String, Gauge> givenANetworkMetric() {
    Gauge<Long> bytesUploaded = new Gauge<Long>() {
      @Override public Long getValue() {
        return ANY_BYTES_UPLOADED;
      }
    };
    Gauge<Long> bytesDownloaded = new Gauge<Long>() {
      @Override public Long getValue() {
        return ANY_BYTES_DOWNLOADED;
      }
    };
    SortedMap<String, Gauge> gauges = new TreeMap<>();
    gauges.put(generator.getBytesDownloadedMetricsName(), bytesUploaded);
    gauges.put(generator.getBytesUploadedMetricsName(), bytesDownloaded);
    return gauges;
  }

  private void assertThereAreNoReports(Reports reports) {
    assertNull(reports.getAppPackage());
    assertNull(reports.getDeviceModel());
    assertNull(reports.getNumberOfCores());
    assertNull(reports.getScreenSize());
    assertNull(reports.getScreenDensity());
    assertNull(reports.getUUID());
    assertNull(reports.getNetworkMetricsReports());
    assertNull(reports.getUIMetricsReports());
  }

  private Reports storeAndGet(DropwizardReport report) {
    return storeAndGet(Collections.singletonList(report));
  }

  private Reports storeAndGet(List<DropwizardReport> dropwizardReports) {
    int numberOfReports = dropwizardReports.size();
    for (int i = 0; i < numberOfReports; i++) {
      storage.storeMetrics(dropwizardReports.get(i));
    }
    return storage.getReports(numberOfReports);
  }

  private List<DropwizardReport> givenSomeEmptyDropwizardReports(int numberOfReports) {
    List<DropwizardReport> reports = new LinkedList<>();
    for (int i = 0; i < numberOfReports; i++) {
      DropwizardReport report = givenAnEmptyDropwizardReport(i);
      reports.add(report);
    }
    return reports;
  }

  private DropwizardReport givenAnEmptyDropwizardReport() {
    return givenAnEmptyDropwizardReport(0);
  }

  private DropwizardReport givenAnEmptyDropwizardReport(long timestamp) {
    SortedMap<String, Gauge> gauges = generateGauges();
    SortedMap<String, Counter> counters = new TreeMap<>();
    SortedMap<String, Histogram> histograms = generateHistograms();
    SortedMap<String, Meter> meters = new TreeMap<>();
    SortedMap<String, Timer> timers = generateTimers();
    return new DropwizardReport(timestamp, gauges, counters, histograms, meters, timers);
  }

  private SortedMap<String, Gauge> generateGauges() {
    return new TreeMap<>();
  }

  private SortedMap<String, Histogram> generateHistograms() {
    return new TreeMap<>();
  }

  private SortedMap<String, Timer> generateTimers() {
    return new TreeMap<>();
  }

  private void clearRealmDB() {
    Context context = getInstrumentation().getContext();
    Realm.init(context);
    Realm realm = Realm.getInstance(RealmConfig.getRealmConfig(context));
    realm.executeTransaction(new Realm.Transaction() {
      @Override public void execute(Realm realm) {
        realm.deleteAll();
      }
    });
    realm.close();
  }
}
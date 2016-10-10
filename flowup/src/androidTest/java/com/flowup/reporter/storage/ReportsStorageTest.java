/*
 * Copyright (C) 2016 Go Karumi S.L.
 */

package com.flowup.reporter.storage;

import android.content.Context;
import com.codahale.metrics.Counter;
import com.codahale.metrics.Gauge;
import com.codahale.metrics.Histogram;
import com.codahale.metrics.Meter;
import com.codahale.metrics.Timer;
import com.flowup.metricnames.App;
import com.flowup.metricnames.Device;
import com.flowup.metricnames.MetricNamesGenerator;
import com.flowup.reporter.DropwizardReport;
import com.flowup.reporter.model.Reports;
import com.flowup.utils.Time;
import io.realm.Realm;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;

public class ReportsStorageTest {

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

  @Test public void returnsReportInfoBasedOnDropwizardMetrics() {
    SortedMap<String, Gauge> networkMetrics = givenSomeNetworkMetrics();
    DropwizardReport dropwizardReport = givenADropWizardReport(networkMetrics);

    Reports reports = storeAndGet(dropwizardReport);

    assertEquals(1, reports.getNetworkMetricsReports().size());
  }

  private DropwizardReport givenADropWizardReport(SortedMap<String, Gauge> networkMetrics) {
    return givenADropWizardReport(0, networkMetrics);
  }

  private DropwizardReport givenADropWizardReport(long timestamp,
      SortedMap<String, Gauge> networkMetrics) {
    SortedMap<String, Gauge> gauges = networkMetrics;
    SortedMap<String, Counter> counters = new TreeMap<>();
    SortedMap<String, Histogram> histograms = generateHistograms();
    SortedMap<String, Meter> meters = new TreeMap<>();
    SortedMap<String, Timer> timers = generateTimers();
    return new DropwizardReport(timestamp, gauges, counters, histograms, meters, timers);
  }

  private SortedMap<String, Gauge> givenSomeNetworkMetrics() {
    Gauge<Long> bytesUploaded = new Gauge<Long>() {
      @Override public Long getValue() {
        return 512L;
      }
    };
    Gauge<Long> bytesDownloaded = new Gauge<Long>() {
      @Override public Long getValue() {
        return 1024L;
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
    assertNull(reports.getUiMetricsReports());
  }

  private Reports storeAndGet(DropwizardReport report) {
    return storeAndGet(Collections.singletonList(report));
  }

  private Reports storeAndGet(List<DropwizardReport> dropwizardReports) {
    for (int i = 0; i < dropwizardReports.size(); i++) {
      storage.storeMetrics(dropwizardReports.get(i));
    }
    return storage.getReports();
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
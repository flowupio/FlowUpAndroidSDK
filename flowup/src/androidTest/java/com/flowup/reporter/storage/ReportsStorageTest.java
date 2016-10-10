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
import com.flowup.reporter.DropwizardReport;
import com.flowup.reporter.model.Reports;
import io.realm.Realm;
import java.util.LinkedList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static junit.framework.Assert.assertEquals;

public class ReportsStorageTest {

  private ReportsStorage storage;

  @Before
  public void setUp() {
    Context context = getInstrumentation().getContext();
    storage = new ReportsStorage(context);
    clearRealmDB();
  }

  @After
  public void tearDown() {
    clearRealmDB();
  }

  @Test public void shouldStoreOneReport() {
    DropwizardReport dropwizardReport = givenADropwizardReport();

    storage.storeMetrics(dropwizardReport);
    Reports reports = storage.getReports();

    assertEquals(1, reports.getReportsIds().size());
  }

  @Test public void shouldStoreMoreThanOneReport() {
    int numberOfReports = 12;
    List<DropwizardReport> dropwizardReports = givenSomeDropwizardReports(numberOfReports);

    for (int i = 0; i < numberOfReports; i++) {
      storage.storeMetrics(dropwizardReports.get(i));
    }
    Reports reports = storage.getReports();

    assertEquals(numberOfReports, reports.getReportsIds().size());
  }

  private List<DropwizardReport> givenSomeDropwizardReports(int numberOfReports) {
    List<DropwizardReport> reports = new LinkedList<>();
    for (int i = 0; i < numberOfReports; i++) {
      DropwizardReport report = givenADropwizardReport(i);
      reports.add(report);
    }
    return reports;
  }

  private DropwizardReport givenADropwizardReport() {
    return givenADropwizardReport(0);
  }

  private DropwizardReport givenADropwizardReport(long timestamp) {
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
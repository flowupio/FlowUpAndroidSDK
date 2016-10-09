/*
 * Copyright (C) 2016 Go Karumi S.L.
 */

package com.flowup.reporter.storage;

import android.content.Context;
import android.util.Log;
import com.codahale.metrics.Counter;
import com.codahale.metrics.Gauge;
import com.codahale.metrics.Histogram;
import com.codahale.metrics.Meter;
import com.codahale.metrics.Sampling;
import com.codahale.metrics.Snapshot;
import com.codahale.metrics.Timer;
import com.flowup.reporter.MetricsReport;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmList;
import java.util.SortedMap;

public class ReportsStorage {

  private static final String REALM_DB_NAME = "FlowUp.realm";
  private static final long REALM_SCHEMA_VERSION = 1;

  private final Context context;
  private final boolean persistent;

  public ReportsStorage(Context context) {
    this(context, true);
  }

  public ReportsStorage(Context context, boolean persistent) {
    this.context = context;
    this.persistent = persistent;
  }

  public void storeMetrics(final MetricsReport metricsReport) {
    Realm realm = getRealm();
    Log.e("DEPURAR", "----------------->" + realm.getPath());
    realm.executeTransaction(new Realm.Transaction() {
      @Override public void execute(Realm realm) {
        storeAsRealmObject(realm, metricsReport);
      }
    });
    realm.close();
  }

  private Realm getRealm() {
    RealmConfiguration.Builder builder = getRealmConfig(context, persistent);
    return Realm.getInstance(builder.build());
  }

  private RealmConfiguration.Builder getRealmConfig(Context context, boolean persistent) {
    Realm.init(context);
    RealmConfiguration.Builder builder =
        new RealmConfiguration.Builder().name(REALM_DB_NAME).schemaVersion(REALM_SCHEMA_VERSION);
    if (persistent) {
      builder.inMemory();
    }
    return builder;
  }

  private void storeAsRealmObject(Realm realm, MetricsReport metricsReport) {
    RealmReport report = realm.createObject(RealmReport.class);
    String reportingTimestamp = String.valueOf(metricsReport.getReportingTimestamp());
    RealmList<RealmMetricReport> realmMetricsReports = mapRealmMetricsReport(realm, metricsReport);
    report.setReportTimestamp(reportingTimestamp);
    report.setMetrics(realmMetricsReports);
    realm.insertOrUpdate(report);
  }

  private RealmList<RealmMetricReport> mapRealmMetricsReport(Realm realm,
      MetricsReport metricsReport) {
    RealmList<RealmMetricReport> realmMetricReports = new RealmList<>();
    realmMetricReports.addAll(mapGauges(realm, metricsReport.getGauges()));
    realmMetricReports.addAll(mapCounters(realm, metricsReport.getCounters()));
    realmMetricReports.addAll(mapHistograms(realm, metricsReport.getHistograms()));
    realmMetricReports.addAll(mapMeters(realm, metricsReport.getMeters()));
    realmMetricReports.addAll(mapTimers(realm, metricsReport.getTimers()));

    return realmMetricReports;
  }

  private RealmList<RealmMetricReport> mapGauges(Realm realm, SortedMap<String, Gauge> gauges) {
    RealmList<RealmMetricReport> realmMetricReports = new RealmList<>();
    for (String metricName : gauges.keySet()) {
      Gauge gauge = gauges.get(metricName);
      mapGauge(realm, metricName, gauge);
    }
    return realmMetricReports;
  }

  private RealmList<RealmMetricReport> mapCounters(Realm realm,
      SortedMap<String, Counter> counters) {
    RealmList<RealmMetricReport> realmMetricReports = new RealmList<>();
    for (String metricName : counters.keySet()) {
      Counter counter = counters.get(metricName);
      mapCounter(realm, metricName, counter);
    }
    return realmMetricReports;
  }

  private RealmList<RealmMetricReport> mapHistograms(Realm realm,
      SortedMap<String, Histogram> histograms) {
    RealmList<RealmMetricReport> realmMetricReports = new RealmList<>();
    for (String metricName : histograms.keySet()) {
      Histogram histogram = histograms.get(metricName);
      mapSampling(realm, metricName, histogram);
    }
    return realmMetricReports;
  }

  private RealmList<RealmMetricReport> mapMeters(Realm realm, SortedMap<String, Meter> meters) {
    //TODO: This will be implemented when needed
    return new RealmList<RealmMetricReport>();
  }

  private RealmList<RealmMetricReport> mapTimers(Realm realm, SortedMap<String, Timer> timers) {
    RealmList<RealmMetricReport> realmMetricReports = new RealmList<>();
    for (String metricName : timers.keySet()) {
      Timer timer = timers.get(metricName);
      mapSampling(realm, metricName, timer);
    }
    return realmMetricReports;
  }

  private void mapSampling(Realm realm, String metricName, Sampling sampling) {
    RealmMetricReport realmMetricReport = realm.createObject(RealmMetricReport.class);
    realmMetricReport.setMetricName(metricName);
    RealmStatisticalValue realmValue = realm.createObject(RealmStatisticalValue.class);

    Snapshot snapshot = sampling.getSnapshot();
    realmValue.setCount(Long.valueOf(snapshot.getValues().length));
    realmValue.setMin(snapshot.getMin());
    realmValue.setMax(snapshot.getMax());
    realmValue.setMean(snapshot.getMean());
    realmValue.setStandardDev(snapshot.getStdDev());
    realmValue.setMedian(snapshot.getMedian());
    realmValue.setP5(snapshot.getValue(0.5));
    realmValue.setP10(snapshot.getValue(0.10));
    realmValue.setP15(snapshot.getValue(0.15));
    realmValue.setP20(snapshot.getValue(0.20));
    realmValue.setP25(snapshot.getValue(0.25));
    realmValue.setP30(snapshot.getValue(0.30));
    realmValue.setP40(snapshot.getValue(0.40));
    realmValue.setP5(snapshot.getValue(0.50));
    realmValue.setP60(snapshot.getValue(0.60));
    realmValue.setP70(snapshot.getValue(0.70));
    realmValue.setP75(snapshot.getValue(0.75));
    realmValue.setP80(snapshot.getValue(0.80));
    realmValue.setP85(snapshot.getValue(0.85));
    realmValue.setP90(snapshot.getValue(0.90));
    realmValue.setP95(snapshot.getValue(0.95));
    realmValue.setP98(snapshot.getValue(0.98));
    realmValue.setP99(snapshot.getValue(0.99));

    realmMetricReport.setValue(realmValue);
  }

  private void mapGauge(Realm realm, String metricName, Gauge gauge) {
    Long gaugeValue = (Long) gauge.getValue();
    RealmMetricReport realmMetricReport = realm.createObject(RealmMetricReport.class);
    realmMetricReport.setMetricName(metricName);
    RealmStatisticalValue realmValue = realm.createObject(RealmStatisticalValue.class);
    realmValue.setValue(gaugeValue);
    realmMetricReport.setValue(realmValue);
  }

  private void mapCounter(Realm realm, String metricName, Counter counter) {
    Long gaugeValue = counter.getCount();
    RealmMetricReport realmMetricReport = realm.createObject(RealmMetricReport.class);
    realmMetricReport.setMetricName(metricName);
    RealmStatisticalValue realmValue = realm.createObject(RealmStatisticalValue.class);
    realmValue.setValue(gaugeValue);
    realmMetricReport.setValue(realmValue);
  }
}

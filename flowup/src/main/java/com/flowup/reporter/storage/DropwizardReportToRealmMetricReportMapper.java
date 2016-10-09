package com.flowup.reporter.storage;

import com.codahale.metrics.Counter;
import com.codahale.metrics.Gauge;
import com.codahale.metrics.Histogram;
import com.codahale.metrics.Sampling;
import com.codahale.metrics.Snapshot;
import com.codahale.metrics.Timer;
import com.flowup.reporter.DropwizardReport;
import com.flowup.utils.Mapper;
import io.realm.Realm;
import io.realm.RealmList;
import java.util.SortedMap;

class DropwizardReportToRealmMetricReportMapper
    extends Mapper<DropwizardReport, RealmList<RealmMetricReport>> {

  private final Realm realm;

  DropwizardReportToRealmMetricReportMapper(Realm realm) {
    this.realm = realm;
  }

  @Override public RealmList<RealmMetricReport> map(DropwizardReport dropwizardReport) {
    RealmList<RealmMetricReport> realmMetricReports = new RealmList<>();
    realmMetricReports.addAll(mapGauges(realm, dropwizardReport.getGauges()));
    realmMetricReports.addAll(mapCounters(realm, dropwizardReport.getCounters()));
    realmMetricReports.addAll(mapHistograms(realm, dropwizardReport.getHistograms()));
    realmMetricReports.addAll(mapTimers(realm, dropwizardReport.getTimers()));
    return realmMetricReports;
  }

  private RealmList<RealmMetricReport> mapGauges(Realm realm, SortedMap<String, Gauge> gauges) {
    RealmList<RealmMetricReport> realmMetricReports = new RealmList<>();
    for (String metricName : gauges.keySet()) {
      Gauge gauge = gauges.get(metricName);
      RealmMetricReport realmMetricReport = mapGauge(realm, metricName, gauge);
      realmMetricReports.add(realmMetricReport);
    }
    return realmMetricReports;
  }

  private RealmList<RealmMetricReport> mapCounters(Realm realm,
      SortedMap<String, Counter> counters) {
    RealmList<RealmMetricReport> realmMetricReports = new RealmList<>();
    for (String metricName : counters.keySet()) {
      Counter counter = counters.get(metricName);
      RealmMetricReport realmMetricReport = mapCounter(realm, metricName, counter);
      realmMetricReports.add(realmMetricReport);
    }
    return realmMetricReports;
  }

  private RealmList<RealmMetricReport> mapHistograms(Realm realm,
      SortedMap<String, Histogram> histograms) {
    RealmList<RealmMetricReport> realmMetricReports = new RealmList<>();
    for (String metricName : histograms.keySet()) {
      Histogram histogram = histograms.get(metricName);
      RealmMetricReport realmMetricReport = mapSampling(realm, metricName, histogram);
      realmMetricReports.add(realmMetricReport);
    }
    return realmMetricReports;
  }

  private RealmList<RealmMetricReport> mapTimers(Realm realm, SortedMap<String, Timer> timers) {
    RealmList<RealmMetricReport> realmMetricReports = new RealmList<>();
    for (String metricName : timers.keySet()) {
      Timer timer = timers.get(metricName);
      RealmMetricReport realmMetricReport = mapSampling(realm, metricName, timer);
      realmMetricReports.add(realmMetricReport);
    }
    return realmMetricReports;
  }

  private RealmMetricReport mapSampling(Realm realm, String metricName, Sampling sampling) {
    RealmMetricReport realmMetricReport = realm.createObject(RealmMetricReport.class, String.valueOf(System.nanoTime()));
    realmMetricReport.setMetricName(metricName);
    RealmStatisticalValue realmValue =
        realm.createObject(RealmStatisticalValue.class, String.valueOf(System.nanoTime()));

    Snapshot snapshot = sampling.getSnapshot();
    realmValue.setCount((long) snapshot.getValues().length);
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
    realmValue.setP50(snapshot.getValue(0.50));
    realmValue.setP60(snapshot.getValue(0.60));
    realmValue.setP70(snapshot.getValue(0.70));
    realmValue.setP75(snapshot.getValue(0.75));
    realmValue.setP80(snapshot.getValue(0.80));
    realmValue.setP85(snapshot.getValue(0.85));
    realmValue.setP90(snapshot.getValue(0.90));
    realmValue.setP95(snapshot.getValue(0.95));
    realmValue.setP98(snapshot.getValue(0.98));
    realmValue.setP99(snapshot.getValue(0.99));

    realmMetricReport.setStatisticalValue(realmValue);
    return realmMetricReport;
  }

  private RealmMetricReport mapGauge(Realm realm, String metricName, Gauge gauge) {
    Long gaugeValue = (Long) gauge.getValue();
    RealmMetricReport realmMetricReport =
        realm.createObject(RealmMetricReport.class, String.valueOf(System.nanoTime()));
    realmMetricReport.setMetricName(metricName);
    RealmStatisticalValue realmValue =
        realm.createObject(RealmStatisticalValue.class, String.valueOf(System.nanoTime()));
    realmValue.setValue(gaugeValue);
    realmMetricReport.setStatisticalValue(realmValue);
    return realmMetricReport;
  }

  private RealmMetricReport mapCounter(Realm realm, String metricName, Counter counter) {
    Long gaugeValue = counter.getCount();
    RealmMetricReport realmMetricReport =
        realm.createObject(RealmMetricReport.class, String.valueOf(System.nanoTime()));
    realmMetricReport.setMetricName(metricName);
    RealmStatisticalValue realmValue =
        realm.createObject(RealmStatisticalValue.class, String.valueOf(System.nanoTime()));
    realmValue.setValue(gaugeValue);
    realmMetricReport.setStatisticalValue(realmValue);
    return realmMetricReport;
  }
}

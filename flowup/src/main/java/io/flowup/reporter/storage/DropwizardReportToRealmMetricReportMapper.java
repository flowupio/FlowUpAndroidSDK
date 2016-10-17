package io.flowup.reporter.storage;

import com.codahale.metrics.Counter;
import com.codahale.metrics.Gauge;
import com.codahale.metrics.Histogram;
import com.codahale.metrics.Sampling;
import com.codahale.metrics.Snapshot;
import com.codahale.metrics.Timer;
import io.flowup.reporter.DropwizardReport;
import io.flowup.utils.Mapper;
import io.realm.Realm;
import io.realm.RealmList;
import java.util.SortedMap;

class DropwizardReportToRealmMetricReportMapper
    extends Mapper<DropwizardReport, RealmList<RealmMetric>> {

  private final Realm realm;

  DropwizardReportToRealmMetricReportMapper(Realm realm) {
    this.realm = realm;
  }

  @Override public RealmList<RealmMetric> map(DropwizardReport dropwizardReport) {
    RealmList<RealmMetric> realmMetrics = new RealmList<>();
    realmMetrics.addAll(mapGauges(realm, dropwizardReport.getGauges()));
    realmMetrics.addAll(mapCounters(realm, dropwizardReport.getCounters()));
    realmMetrics.addAll(mapHistograms(realm, dropwizardReport.getHistograms()));
    realmMetrics.addAll(mapTimers(realm, dropwizardReport.getTimers()));
    return realmMetrics;
  }

  private RealmList<RealmMetric> mapGauges(Realm realm, SortedMap<String, Gauge> gauges) {
    RealmList<RealmMetric> realmMetrics = new RealmList<>();
    for (String metricName : gauges.keySet()) {
      Gauge gauge = gauges.get(metricName);
      if (gauge.getValue() == null) {
        continue;
      }
      RealmMetric realmMetric = mapGauge(realm, metricName, gauge);
      realmMetrics.add(realmMetric);
    }
    return realmMetrics;
  }

  private RealmList<RealmMetric> mapCounters(Realm realm, SortedMap<String, Counter> counters) {
    RealmList<RealmMetric> realmMetrics = new RealmList<>();
    for (String metricName : counters.keySet()) {
      Counter counter = counters.get(metricName);
      RealmMetric realmMetric = mapCounter(realm, metricName, counter);
      realmMetrics.add(realmMetric);
    }
    return realmMetrics;
  }

  private RealmList<RealmMetric> mapHistograms(Realm realm,
      SortedMap<String, Histogram> histograms) {
    RealmList<RealmMetric> realmMetrics = new RealmList<>();
    for (String metricName : histograms.keySet()) {
      Histogram histogram = histograms.get(metricName);
      RealmMetric realmMetric = mapSampling(realm, metricName, histogram);
      realmMetrics.add(realmMetric);
    }
    return realmMetrics;
  }

  private RealmList<RealmMetric> mapTimers(Realm realm, SortedMap<String, Timer> timers) {
    RealmList<RealmMetric> realmMetrics = new RealmList<>();
    for (String metricName : timers.keySet()) {
      Timer timer = timers.get(metricName);
      RealmMetric realmMetric = mapSampling(realm, metricName, timer);
      realmMetrics.add(realmMetric);
    }
    return realmMetrics;
  }

  private RealmMetric mapSampling(Realm realm, String metricName, Sampling sampling) {
    RealmMetric realmMetric =
        realm.createObject(RealmMetric.class, String.valueOf(System.nanoTime()));
    realmMetric.setMetricName(metricName);
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

    realmMetric.setStatisticalValue(realmValue);
    return realmMetric;
  }

  private RealmMetric mapGauge(Realm realm, String metricName, Gauge gauge) {
    Long gaugeValue = (Long) gauge.getValue();
    RealmMetric realmMetric =
        realm.createObject(RealmMetric.class, String.valueOf(System.nanoTime()));
    realmMetric.setMetricName(metricName);
    RealmStatisticalValue realmValue =
        realm.createObject(RealmStatisticalValue.class, String.valueOf(System.nanoTime()));
    realmValue.setValue(gaugeValue);
    realmMetric.setStatisticalValue(realmValue);
    return realmMetric;
  }

  private RealmMetric mapCounter(Realm realm, String metricName, Counter counter) {
    Long gaugeValue = counter.getCount();
    RealmMetric realmMetric =
        realm.createObject(RealmMetric.class, String.valueOf(System.nanoTime()));
    realmMetric.setMetricName(metricName);
    RealmStatisticalValue realmValue =
        realm.createObject(RealmStatisticalValue.class, String.valueOf(System.nanoTime()));
    realmValue.setValue(gaugeValue);
    realmMetric.setStatisticalValue(realmValue);
    return realmMetric;
  }
}

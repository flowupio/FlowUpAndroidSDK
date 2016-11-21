package io.flowup.reporter.storage;

import com.codahale.metrics.Counter;
import com.codahale.metrics.Gauge;
import com.codahale.metrics.Histogram;
import com.codahale.metrics.Sampling;
import com.codahale.metrics.Snapshot;
import com.codahale.metrics.Timer;
import io.flowup.reporter.DropwizardReport;
import io.flowup.utils.Mapper;
import java.util.LinkedList;
import java.util.List;
import java.util.SortedMap;

class DropwizardReportToSQLDelightMetricMapper
    extends Mapper<DropwizardReport, List<SQLDelightMetric>> {

  private final long reportId;

  public DropwizardReportToSQLDelightMetricMapper(long reportId) {
    this.reportId = reportId;
  }

  @Override public List<SQLDelightMetric> map(DropwizardReport dropwizardReport) {
    List<SQLDelightMetric> metrics = new LinkedList<>();
    metrics.addAll(mapGauges(dropwizardReport.getGauges()));
    metrics.addAll(mapCounters(dropwizardReport.getCounters()));
    metrics.addAll(mapHistograms(dropwizardReport.getHistograms()));
    metrics.addAll(mapTimers(dropwizardReport.getTimers()));
    return metrics;
  }

  private List<SQLDelightMetric> mapGauges(SortedMap<String, Gauge> gauges) {
    List<SQLDelightMetric> metrics = new LinkedList<>();
    for (String metricName : gauges.keySet()) {
      Gauge gauge = gauges.get(metricName);
      if (gauge.getValue() == null) {
        continue;
      }
      SQLDelightMetric metric = mapGauge(metricName, gauge);
      metrics.add(metric);
    }
    return metrics;
  }

  private List<SQLDelightMetric> mapCounters(SortedMap<String, Counter> counters) {
    List<SQLDelightMetric> metrics = new LinkedList<>();
    for (String metricName : counters.keySet()) {
      Counter counter = counters.get(metricName);
      SQLDelightMetric metric = mapCounter(metricName, counter);
      metrics.add(metric);
    }
    return metrics;
  }

  private List<SQLDelightMetric> mapHistograms(SortedMap<String, Histogram> histograms) {
    List<SQLDelightMetric> metrics = new LinkedList<>();
    for (String metricName : histograms.keySet()) {
      Histogram histogram = histograms.get(metricName);
      SQLDelightMetric metric = mapSampling(metricName, histogram);
      metrics.add(metric);
    }
    return metrics;
  }

  private List<SQLDelightMetric> mapTimers(SortedMap<String, Timer> timers) {
    List<SQLDelightMetric> metrics = new LinkedList<>();
    for (String metricName : timers.keySet()) {
      Timer timer = timers.get(metricName);
      SQLDelightMetric metric = mapSampling(metricName, timer);
      metrics.add(metric);
    }
    return metrics;
  }

  private SQLDelightMetric mapSampling(String metricName, Sampling sampling) {
    Snapshot snapshot = sampling.getSnapshot();
    double mean = snapshot.getMean();
    double p10 = snapshot.getValue(0.10);
    double p90 = snapshot.getValue(0.90);
    long count = snapshot.getValues().length;
    return new AutoValue_SQLDelightMetric(0, reportId, metricName, count, null, mean, p10, p90);
  }

  private SQLDelightMetric mapGauge(String metricName, Gauge gauge) {
    Long gaugeValue = (Long) gauge.getValue();
    return new AutoValue_SQLDelightMetric(0, reportId, metricName, 1L, gaugeValue, null, null, null);
  }

  private SQLDelightMetric mapCounter(String metricName, Counter counter) {
    Long counterValue = counter.getCount();
    return new AutoValue_SQLDelightMetric(0, reportId, metricName, 1L, counterValue, null, null, null);
  }
}

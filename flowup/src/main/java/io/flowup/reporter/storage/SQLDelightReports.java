package io.flowup.reporter.storage;

import java.util.List;

public class SQLDelightReports {

  private final List<SQLDelightReport> reports;
  private final List<SQLDelightMetric> metrics;

  public SQLDelightReports(List<SQLDelightReport> reports, List<SQLDelightMetric> metrics) {
    this.reports = reports;
    this.metrics = metrics;
  }

  public List<SQLDelightReport> getReports() {
    return reports;
  }

  public List<SQLDelightMetric> getMetrics() {
    return metrics;
  }
}

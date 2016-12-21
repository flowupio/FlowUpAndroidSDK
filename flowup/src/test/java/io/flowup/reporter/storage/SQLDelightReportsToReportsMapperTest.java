package io.flowup.reporter.storage;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import io.flowup.reporter.model.Reports;
import java.util.ArrayList;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static junit.framework.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class) public class SQLDelightReportsToReportsMapperTest {

  private static final long REPORT_ID = 42;

  private SQLDelightReportsToReportsMapper mapper;

  @Before public void setUp() {
    mapper = new SQLDelightReportsToReportsMapper();
  }

  @Test public void doesNotSendReportIfCPUMetricHasNoValue() {
    SQLDelightReports sqlDelightReports = getSqlDelightReports("cpuUsage");

    Reports mappedReports = mapper.map(sqlDelightReports);

    assertEquals(0, mappedReports.getCpuMetrics().size());
  }

  @Test public void doesNotSendReportIfMemoryMetricHasNoValue() {
    SQLDelightReports sqlDelightReports = getSqlDelightReports("memoryUsage");

    Reports mappedReports = mapper.map(sqlDelightReports);

    assertEquals(0, mappedReports.getMemoryMetrics().size());
  }

  @Test public void doesNotSendReportIfNetworkMetricHasNoValue() {
    SQLDelightReports sqlDelightReports = getSqlDelightReports("bytesUploaded");

    Reports mappedReports = mapper.map(sqlDelightReports);

    assertEquals(0, mappedReports.getNetworkMetrics().size());
  }

  @Test public void doesNotSendReportIfDiskMetricHasNoValue() {
    SQLDelightReports sqlDelightReports = getSqlDelightReports("bytesUploaded");

    Reports mappedReports = mapper.map(sqlDelightReports);

    assertEquals(0, mappedReports.getDiskMetrics().size());
  }

  @NonNull private SQLDelightReports getSqlDelightReports(String metricName) {
    List<SQLDelightReport> reports = new ArrayList<>();
    reports.add(getReport(REPORT_ID));
    List<SQLDelightMetric> metrics = new ArrayList<>();
    metrics.add(getMetricWithNullValues(REPORT_ID, metricName));
    return new SQLDelightReports(reports, metrics);
  }

  @NonNull private SQLDelightReport getReport(final long reportId) {
    return new SQLDelightReport() {
      @Override public long _id() {
        return reportId;
      }

      @Override public long report_timestamp() {
        return 1000;
      }
    };
  }

  @NonNull private SQLDelightMetric getMetricWithNullValues(long reportId, String metricName) {
    return new SQLDelightMetricWithNullValues(reportId, metricName);
  }
}

class SQLDelightMetricWithNullValues extends SQLDelightMetric {

  private final long reportId;
  private final String metricName;

  SQLDelightMetricWithNullValues(long reportId, String metricName) {
    this.reportId = reportId;
    this.metricName = metricName;
  }

  @Override public long _id() {
    return 0;
  }

  @Override public long report_id() {
    return reportId;
  }

  @NonNull @Override public String metric_name() {
    return "com/something/something.ASD.QWE.3.mdpi.320x320.21.1.false.true." + metricName;
  }

  @Nullable @Override public Long count() {
    return null;
  }

  @Nullable @Override public Long value() {
    return null;
  }

  @Nullable @Override public Double mean() {
    return null;
  }

  @Nullable @Override public Double p10() {
    return null;
  }

  @Nullable @Override public Double p90() {
    return null;
  }
}

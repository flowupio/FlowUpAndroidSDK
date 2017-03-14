/*
 * Copyright (C) 2016 Go Karumi S.L.
 */

package io.flowup.reporter.storage;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import io.flowup.reporter.DropwizardReport;
import io.flowup.reporter.model.Reports;
import io.flowup.storage.SQLDelightStorage;
import io.flowup.utils.Time;
import java.util.List;

public class ReportsStorage extends SQLDelightStorage {

  private final Time time;

  public ReportsStorage(SQLiteOpenHelper openHelper, Time time) {
    super(openHelper);
    this.time = time;
  }

  public void storeMetrics(final DropwizardReport dropwizardReport) {
    executeTransaction(new Transaction() {
      @Override public void execute(SQLiteDatabase database) {
        storeReport(database, dropwizardReport);
      }
    });
  }

  public Reports getReports(final int numberOfReports) {
    return read(new Read<Reports>() {
      @Override public Reports read(SQLiteDatabase database) {
        List<SQLDelightReport> reports = SQLDelightReport.getReports(database, numberOfReports);
        if (reports == null || reports.isEmpty()) {
          return null;
        }
        long[] reportIds = new long[reports.size()];
        for (int i = 0; i < reportIds.length; i++) {
          reportIds[i] = reports.get(i)._id();
        }
        List<SQLDelightMetric> metrics =
            SQLDelightMetric.getMetricsByReportIds(database, reportIds);
        SQLDelightReports reportsToMap = new SQLDelightReports(reports, metrics);
        return new SQLDelightReportsToReportsMapper().map(reportsToMap);
      }
    });
  }

  public void deleteReports(final Reports reports) {
    executeTransaction(new Transaction() {
      @Override public void execute(SQLiteDatabase database) {
        List<String> reportsIds = reports.getReportsIds();
        int numberOfReports = reportsIds.size();
        long[] ids = new long[numberOfReports];
        for (int i = 0; i < numberOfReports; i++) {
          ids[i] = Long.valueOf(reportsIds.get(i));
        }
        SQLDelightReport.remove(database, ids);
        SQLDelightMetric.removeByReportIds(database, ids);
      }
    });
  }

  public int deleteOldReports() {
    final int[] numberOfReportsDeleted = new int[1];
    executeTransaction(new Transaction() {
      @Override public void execute(SQLiteDatabase database) {
        long twoDaysAgoTimestamp = time.twoDaysAgo();
        int removedReports = SQLDelightReport.removeOld(database, twoDaysAgoTimestamp);
        numberOfReportsDeleted[0] = removedReports;
      }
    });
    return numberOfReportsDeleted[0];
  }

  public void clear() {
    executeTransaction(new Transaction() {
      @Override public void execute(SQLiteDatabase database) {
        SQLDelightReport.removeAll(database);
      }
    });
  }

  private void storeReport(SQLiteDatabase db, DropwizardReport report) {
    Long id = SQLDelightReport.createReport(db, report.getReportingTimestamp());
    List<SQLDelightMetric> metrics = new DropwizardReportToSQLDelightMetricMapper(id).map(report);
    for (SQLDelightMetric metric : metrics) {
      if (isValidMetric(metric)) {
        SQLDelightMetric.createMetric(db, metric.report_id(), metric.metric_name(), metric.count(),
            metric.value(), metric.mean(), metric.p10(), metric.p90());
      }
    }
  }

  private boolean isValidMetric(SQLDelightMetric metric) {
    return metric.containsData();
  }
}

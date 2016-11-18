/*
 * Copyright (C) 2016 Go Karumi S.L.
 */

package io.flowup.reporter.storage;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.google.auto.value.AutoValue;
import com.squareup.sqldelight.RowMapper;
import com.squareup.sqldelight.SqlDelightStatement;
import java.util.ArrayList;
import java.util.List;

@AutoValue abstract class SQLDelightReport implements ReportModel {

  private static final ReportModel.Factory<SQLDelightReport> FACTORY =
      new ReportModel.Factory<>(new ReportModel.Creator<SQLDelightReport>() {

        @Override public SQLDelightReport create(long id, long reportTimestamp) {
          return new AutoValue_SQLDelightReport(id, reportTimestamp);
        }
      });

  private static final RowMapper<SQLDelightReport> REPORT_MAPPER = FACTORY.get_reportMapper();

  static long createReport(SQLiteDatabase db, long reportTimestamp) {
    Create_report createReport = new Create_report(db);
    createReport.bind(reportTimestamp);
    return createReport.program.executeInsert();
  }

  static List<SQLDelightReport> getReports(SQLiteDatabase db, int numberOfReports) {
    SqlDelightStatement getReport = SQLDelightReport.FACTORY.get_report(numberOfReports);
    Cursor cursor = db.rawQuery(getReport.statement, new String[0]);
    List<SQLDelightReport> reports = new ArrayList<>(numberOfReports);
    while (cursor.moveToNext()) {
      SQLDelightReport report = SQLDelightReport.REPORT_MAPPER.map(cursor);
      reports.add(report);
    }
    cursor.close();
    return reports;
  }

  static void removeAll(SQLiteDatabase db) {
    db.execSQL(ReportModel.REMOVE_ALL);
  }

  static void remove(SQLiteDatabase db, String[] ids) {
    db.execSQL(ReportModel.REMOVE, ids);
  }

  static int removeOld(SQLiteDatabase db, long twoDaysAgoTimestamp) {
    ReportModel.Remove_old removeOld = new Remove_old(db);
    removeOld.bind(twoDaysAgoTimestamp);
    return removeOld.program.executeUpdateDelete();
  }
}

package io.flowup.storage;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import io.flowup.config.storage.ConfigModel;
import io.flowup.config.storage.MetricModel;
import io.flowup.config.storage.ReportModel;

public class SQLDelightfulOpenHelper extends SQLiteOpenHelper {

  private static final String DB_NAME = "flowup.db";
  private static final int DB_VERSION = 1;
  private static final String REPORT_TIMESTAMP_INDEX =
      "CREATE INDEX search_report_timestamp ON report (report_timestamp DESC)";

  public SQLDelightfulOpenHelper(Context context) {
    super(context, DB_NAME, null, DB_VERSION);
  }

  @Override public void onCreate(SQLiteDatabase db) {
    db.execSQL(ConfigModel.CREATE_TABLE);
    db.execSQL(ReportModel.CREATE_TABLE);
    db.execSQL(MetricModel.CREATE_TABLE);
    db.execSQL(REPORT_TIMESTAMP_INDEX);
  }

  @Override public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    //TODO: Instead of migrations try to remove all here, like a DROP AND CREATE.
  }
}
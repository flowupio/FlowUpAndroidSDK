package io.flowup.storage;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import io.flowup.config.storage.ConfigModel;

public class SQLDelightfulOpenHelper extends SQLiteOpenHelper {

  private static final String DB_NAME = "flowup.db";
  private static final int DB_VERSION = 1;

  public SQLDelightfulOpenHelper(Context context) {
    super(context, DB_NAME, null, DB_VERSION);
  }

  @Override public void onCreate(SQLiteDatabase db) {
    db.execSQL(ConfigModel.CREATE_TABLE);
  }

  @Override public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    //TODO: Instead of migrations try to remove all here, like a DROP AND CREATE.
  }
}
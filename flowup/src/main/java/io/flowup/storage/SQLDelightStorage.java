/*
 * Copyright (C) 2016 Go Karumi S.L.
 */

package io.flowup.storage;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SQLDelightStorage {

  private static final Object WRITE_LOCK = new Object();

  private final SQLiteOpenHelper openHelper;

  public SQLDelightStorage(SQLiteOpenHelper openHelper) {
    this.openHelper = openHelper;
  }

  protected void executeTransaction(Transaction transaction) {
    synchronized (WRITE_LOCK) {
      SQLiteDatabase database = openHelper.getWritableDatabase();
      try {
        database.beginTransaction();
        transaction.execute(database);
        database.setTransactionSuccessful();
      } finally {
        database.endTransaction();
      }
    }
  }

  protected <T> T read(Read<T> read) {
    SQLiteDatabase database = openHelper.getReadableDatabase();
    T result = read.read(database);
    return result;
  }

  public interface Read<T> {
    T read(SQLiteDatabase database);
  }

  public interface Transaction {
    void execute(SQLiteDatabase database);
  }
}

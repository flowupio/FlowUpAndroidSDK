/*
 * Copyright (C) 2016 Go Karumi S.L.
 */

package io.flowup.storage;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class SQLDelightStorage {

  private static final ReentrantReadWriteLock DB_LOCK = new ReentrantReadWriteLock();
  private static final Lock READ_LOCK = DB_LOCK.readLock();
  private static final Lock WRITE_LOCK = DB_LOCK.writeLock();

  private final SQLiteOpenHelper openHelper;

  public SQLDelightStorage(SQLiteOpenHelper openHelper) {
    this.openHelper = openHelper;
  }

  protected void executeTransaction(Transaction transaction) {
    WRITE_LOCK.lock();
    SQLiteDatabase database = openHelper.getWritableDatabase();
    try {
      database.beginTransaction();
      transaction.execute(database);
      database.setTransactionSuccessful();
    } finally {
      database.endTransaction();
      WRITE_LOCK.unlock();
    }
  }

  protected <T> T read(Read<T> read) {
    READ_LOCK.lock();
    T result = null;
    try {
      SQLiteDatabase database = openHelper.getReadableDatabase();
      result = read.read(database);
    } finally {
      READ_LOCK.unlock();
    }
    return result;
  }

  public interface Read<T> {
    T read(SQLiteDatabase database);
  }

  public interface Transaction {
    void execute(SQLiteDatabase database);
  }
}

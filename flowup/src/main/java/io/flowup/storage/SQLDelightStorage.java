/*
 * Copyright (C) 2016 Go Karumi S.L.
 */

package io.flowup.storage;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabaseLockedException;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.NonNull;
import io.flowup.logger.Logger;

public class SQLDelightStorage {

  private static final Object DB_LOCK = new Object();

  private final SQLiteOpenHelper openHelper;

  public SQLDelightStorage(SQLiteOpenHelper openHelper) {
    this.openHelper = openHelper;
  }

  protected void executeTransaction(Transaction transaction) {
    executeTransaction(transaction, new EmptyErrorListener());
  }

  protected void executeTransaction(Transaction transaction, @NonNull ErrorListener errorListener) {
    synchronized (DB_LOCK) {
      Logger.d("Start writing a DB transaction");
      SQLiteDatabase database = null;
      try {
        database = openHelper.getWritableDatabase();
        database.beginTransaction();
        transaction.execute(database);
        database.setTransactionSuccessful();
      } catch (SQLiteDatabaseLockedException e) {
        Logger.e("Exception catch trying to execute a SQLite transaction", e);
        errorListener.onUnrecoverableError();
      } finally {
        if (database != null) {
          database.endTransaction();
          database.close();
        }
        Logger.d("Write DB transaction finished");
      }
    }
  }

  protected <T> T read(Read<T> read) {
    return read(read, new EmptyErrorListener());
  }

  protected <T> T read(Read<T> read, @NonNull ErrorListener errorListener) {
    synchronized (DB_LOCK) {
      Logger.d("Start reading from DB");
      T result = null;
      SQLiteDatabase database = null;
      try {
        database = openHelper.getWritableDatabase();
        result = read.read(database);
      } catch (SQLiteDatabaseLockedException e) {
        Logger.e("Exception catch trying to read our SQLite database", e);
        errorListener.onUnrecoverableError();
      } finally {
        Logger.d("End reading from DB");
        if (database != null) {
          database.close();
        }
      }
      return result;
    }
  }

  public interface Read<T> {
    T read(SQLiteDatabase database);
  }

  public interface Transaction {
    void execute(SQLiteDatabase database);
  }

  public interface ErrorListener {
    void onUnrecoverableError();
  }

  private static class EmptyErrorListener implements ErrorListener {
    @Override public void onUnrecoverableError() {
    }
  }
}

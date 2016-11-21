/*
 * Copyright (C) 2016 Go Karumi S.L.
 */

package io.flowup.config.storage;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import io.flowup.config.Config;
import io.flowup.storage.SQLDelightStorage;

public class ConfigStorage extends SQLDelightStorage {

  public ConfigStorage(SQLiteOpenHelper openHelper) {
    super(openHelper);
  }

  public Config getConfig() {
    SQLDelightConfig sqlDelightConfig = read(new SQLDelightStorage.Read<SQLDelightConfig>() {
      @Override public SQLDelightConfig read(SQLiteDatabase database) {
        return SQLDelightConfig.getConfig(database);
      }
    });
    return sqlDelightConfig == null ? new Config() : new Config(sqlDelightConfig.enabled());
  }

  public void updateConfig(final Config config) {
    executeTransaction(new Transaction() {
      @Override public void execute(SQLiteDatabase database) {
        SQLDelightConfig.updateConfig(database, config);
      }
    });
  }

  public void clearConfig() {
    executeTransaction(new Transaction() {
      @Override public void execute(SQLiteDatabase database) {
        SQLDelightConfig.deleteConfig(database);
      }
    });
  }
}

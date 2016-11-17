/*
 * Copyright (C) 2016 Go Karumi S.L.
 */

package io.flowup.config.storage;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.google.auto.value.AutoValue;
import com.squareup.sqldelight.RowMapper;
import io.flowup.config.Config;

@AutoValue abstract class SQLDelightConfig implements ConfigModel {

  static SQLDelightConfig getConfig(SQLiteDatabase db) {
    Cursor cursor = db.rawQuery(ConfigModel.SELECT_CONFIG, new String[0]);
    SQLDelightConfig config = null;
    if (cursor.moveToFirst()) {
      config = SQLDelightConfig.SELECT_CONFIG_MAPPER.map(cursor);
    }
    cursor.close();
    return config;
  }

  static void updateConfig(SQLiteDatabase db, Config config) {
    Update_config updateConfig = new Update_config(db);
    updateConfig.bind(config.isEnabled());
    updateConfig.program.executeInsert();
  }

  static void deleteConfig(SQLiteDatabase db) {
    db.execSQL(ConfigModel.DELETE_CONFIG);
  }

  private static final Factory<SQLDelightConfig> FACTORY =
      new ConfigModel.Factory<>(new Creator<SQLDelightConfig>() {
        @Override public SQLDelightConfig create(@NonNull String id, @Nullable Boolean enabled) {
          return new AutoValue_SQLDelightConfig(id, enabled);
        }
      });

  private static final RowMapper<SQLDelightConfig> SELECT_CONFIG_MAPPER =
      FACTORY.select_configMapper();
}

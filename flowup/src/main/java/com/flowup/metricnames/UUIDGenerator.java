/*
 * Copyright (C) 2016 Go Karumi S.L.
 */

package com.flowup.metricnames;

import android.content.Context;
import android.content.SharedPreferences;
import java.util.UUID;

class UUIDGenerator {

  private static final String UUID_KEY = "uuid";
  private static final String UUID_SHARED_PREFS_NAME = "uuid_shared_prefs_name";
  private static final Object GENERATOR_LOCK = new Object();

  private final SharedPreferences sharedPreferences;

  UUIDGenerator(Context context) {
    this.sharedPreferences =
        context.getSharedPreferences(UUID_SHARED_PREFS_NAME, Context.MODE_PRIVATE);
  }

  String getUUID() {
    String uuid = getUUIDFromSharedPreferences();
    if (uuid.isEmpty()) {
      uuid = generateAndSaveUUID();
    }
    return uuid;
  }

  private String generateAndSaveUUID() {
    String uuid = "";
    synchronized (GENERATOR_LOCK) {
      uuid = UUID.randomUUID().toString();
      saveUUID(uuid);
    }
    return uuid;
  }

  private void saveUUID(String uuid) {
    sharedPreferences.edit().putString(UUID_KEY, uuid).commit();
  }

  private String getUUIDFromSharedPreferences() {
    return sharedPreferences.getString(UUID_KEY, "");
  }
}

/*
 * Copyright (C) 2016 Go Karumi S.L.
 */

package io.flowup.android;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import java.util.UUID;

class UUIDGenerator {

  private static final String UUID_KEY = "uuid";
  private static final String UUID_SHARED_PREFS_NAME = "uuid_shared_prefs_name";
  private static final Object GENERATOR_LOCK = new Object();

  private static String uuid;
  private final Context context;

  UUIDGenerator(Context context) {
    this.context = context;
  }

  String getUUID() {
    String uuid = "";
    synchronized (GENERATOR_LOCK) {
      uuid = getUUIDFromSharedPreferences();
      if (uuid.isEmpty()) {
        uuid = generateAndSaveUUID();
      }
    }
    return uuid;
  }

  @SuppressLint("CommitPrefEdits") void clean() {
    uuid = null;
    getSharedPreferences().edit().clear().commit();
  }

  @SuppressLint("CommitPrefEdits") private void saveUUID(String uuid) {
    UUIDGenerator.uuid = uuid;
    getSharedPreferences().edit().putString(UUID_KEY, uuid).commit();
  }

  private String generateAndSaveUUID() {
    String uuid = UUID.randomUUID().toString();
    saveUUID(uuid);
    return uuid;
  }

  private String getUUIDFromSharedPreferences() {
    if (uuid == null) {
      uuid = getSharedPreferences().getString(UUID_KEY, "");
    }
    return uuid;
  }

  private SharedPreferences getSharedPreferences() {
    return context.getSharedPreferences(UUID_SHARED_PREFS_NAME, Context.MODE_PRIVATE);
  }
}

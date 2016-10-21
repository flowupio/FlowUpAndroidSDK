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
  private final SharedPreferences sharedPreferences;


  UUIDGenerator(Context context) {
    this.sharedPreferences =
        context.getSharedPreferences(UUID_SHARED_PREFS_NAME, Context.MODE_PRIVATE);
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

  void clean() {
    uuid = null;
    sharedPreferences.edit().clear().commit();
  }

  private String generateAndSaveUUID() {
    String uuid = UUID.randomUUID().toString();
    saveUUID(uuid);
    return uuid;
  }

  @SuppressLint("CommitPrefEdits") private void saveUUID(String uuid) {
    UUIDGenerator.uuid = uuid;
    sharedPreferences.edit().putString(UUID_KEY, uuid).commit();
  }

  private String getUUIDFromSharedPreferences() {
    if (uuid == null) {
      uuid = sharedPreferences.getString(UUID_KEY, "");
    }
    return uuid;
  }
}

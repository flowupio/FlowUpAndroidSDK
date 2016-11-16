/*
 * Copyright (C) 2016 Go Karumi S.L.
 */

package io.flowup.storage;

import android.content.Context;
import io.flowup.reporter.storage.FlowUpRealmModule;
import io.realm.RealmConfiguration;

class RealmConfig {

  private static final String REALM_DB_NAME = "FlowUpDB.realm";
  private static final long REALM_SCHEMA_VERSION = 1;

  static RealmConfiguration getRealmConfig(Context context) {
    return new RealmConfiguration.Builder(context).name(REALM_DB_NAME)
        .schemaVersion(REALM_SCHEMA_VERSION)
        .modules(new FlowUpRealmModule())
        .deleteRealmIfMigrationNeeded()
        .build();
  }
}

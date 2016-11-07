/*
 * Copyright (C) 2016 Go Karumi S.L.
 */

package io.flowup.storage;

import android.content.Context;
import io.flowup.reporter.storage.FlowUpRealmModule;
import io.realm.Realm;
import io.realm.RealmConfiguration;

class RealmConfig {

  private static final String REALM_DB_NAME = "FlowUp.realm";
  private static final long REALM_SCHEMA_VERSION = 1;

  static RealmConfiguration getRealmConfig(Context context) {
    Realm.init(context);
    RealmConfiguration.Builder builder = new RealmConfiguration.Builder().name(REALM_DB_NAME)
        .schemaVersion(REALM_SCHEMA_VERSION)
        .modules(new FlowUpRealmModule())
        .deleteRealmIfMigrationNeeded();
    return builder.build();
  }
}
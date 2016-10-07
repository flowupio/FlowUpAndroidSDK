/*
 * Copyright (C) 2016 Go Karumi S.L.
 */

package com.flowup.reporter.storage;

import android.content.Context;
import com.flowup.reporter.model.Metrics;
import io.realm.Realm;
import io.realm.RealmConfiguration;

public class MetricsStorage {

  private static final String REALM_DB_NAME = "FlowUp";
  private static final long REALM_SCHEMA_VERSION = 1;

  private final Realm realm;

  public MetricsStorage(Context context, boolean persistent) {
    RealmConfiguration.Builder builder = getRealmConfig(context, persistent);
    this.realm = Realm.getInstance(builder.build());
  }

  private RealmConfiguration.Builder getRealmConfig(Context context, boolean persistent) {
    Realm.init(context);
    RealmConfiguration.Builder builder =
        new RealmConfiguration.Builder().name(REALM_DB_NAME).schemaVersion(REALM_SCHEMA_VERSION);
    if (persistent) {
      builder.inMemory();
    }
    return builder;
  }

  public void storeMetrics(Metrics metrics) {
    realm.beginTransaction();
    //Persist shit
    realm.commitTransaction();
  }
}

/*
 * Copyright (C) 2016 Go Karumi S.L.
 */

package com.flowup.reporter.storage;

import android.content.Context;
import com.codahale.metrics.Counter;
import com.codahale.metrics.Gauge;
import com.codahale.metrics.Histogram;
import com.codahale.metrics.Meter;
import com.codahale.metrics.Timer;
import com.flowup.reporter.Metrics;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import java.util.SortedMap;

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
    storeGauges(metrics.getGauges());
    storeCounters(metrics.getCounters());
    storeHistograms(metrics.getHistograms());
    storeMeters(metrics.getMeters());
    storeTimers(metrics.getTimers());
    realm.commitTransaction();
  }

  private void storeGauges(SortedMap<String, Gauge> gauges) {

  }

  private void storeCounters(SortedMap<String, Counter> counters) {

  }

  private void storeHistograms(SortedMap<String, Histogram> histograms) {

  }

  private void storeMeters(SortedMap<String, Meter> meters) {

  }

  private void storeTimers(SortedMap<String, Timer> timers) {

  }
}

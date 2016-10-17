/*
 * Copyright (C) 2016 Go Karumi S.L.
 */

package com.flowup.reporter.android;

import android.content.Context;
import android.os.Bundle;
import com.google.android.gms.gcm.GcmNetworkManager;
import com.google.android.gms.gcm.PeriodicTask;
import com.google.android.gms.gcm.Task;
import java.util.concurrent.TimeUnit;

import static com.flowup.reporter.android.WiFiSyncService.API_KEY_EXTRA;

public class WiFiSyncServiceScheduler {

  static final String SYNCHRONIZE_METRICS_REPORT = "SynchronizeMetricsReport";

  private static final long SYNC_PERIOD = TimeUnit.MINUTES.toSeconds(60);
  private static final long FLEX_PERIOD = TimeUnit.MINUTES.toSeconds(15);

  private final GcmNetworkManager gcmNetworkManager;
  private final String apiKey;

  public WiFiSyncServiceScheduler(Context context, String apiKey) {
    this.gcmNetworkManager = GcmNetworkManager.getInstance(context);
    this.apiKey = apiKey;
  }

  public void scheduleSyncTask() {
    Bundle extras = new Bundle();
    extras.putString(API_KEY_EXTRA, apiKey);
    PeriodicTask task = new PeriodicTask.Builder().setService(WiFiSyncService.class)
        .setTag(SYNCHRONIZE_METRICS_REPORT)
        .setPeriod(SYNC_PERIOD)
        .setFlex(FLEX_PERIOD)
        .setRequiredNetwork(Task.NETWORK_STATE_UNMETERED)
        .setExtras(extras)
        .build();
    gcmNetworkManager.schedule(task);
  }
}

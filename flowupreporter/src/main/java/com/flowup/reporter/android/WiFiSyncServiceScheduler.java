package com.flowup.reporter.android;

import android.content.Context;
import com.google.android.gms.gcm.GcmNetworkManager;
import com.google.android.gms.gcm.PeriodicTask;
import com.google.android.gms.gcm.Task;
import java.util.concurrent.TimeUnit;

public class WiFiSyncServiceScheduler {

  static final String SYNCHRONIZE_METRICS_REPORT = "SynchronizeMetricsReport";

  private static final long SYNC_PERIOD = TimeUnit.MINUTES.toSeconds(60);
  private static final long FLEX_PERIOD = TimeUnit.MINUTES.toSeconds(15);

  private final GcmNetworkManager gcmNetworkManager;

  public WiFiSyncServiceScheduler(Context context) {
    this.gcmNetworkManager = GcmNetworkManager.getInstance(context);
  }

  public void scheduleSync() {
    PeriodicTask task = new PeriodicTask.Builder().setService(WiFiSyncService.class)
        .setTag(SYNCHRONIZE_METRICS_REPORT)
        .setPeriod(SYNC_PERIOD)
        .setFlex(FLEX_PERIOD)
        .setRequiredNetwork(Task.NETWORK_STATE_UNMETERED)
        .setUpdateCurrent(true)
        .build();
    gcmNetworkManager.schedule(task);
  }
}

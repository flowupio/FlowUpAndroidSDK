/*
 * Copyright (C) 2016 Go Karumi S.L.
 */

package io.flowup.config.android;

import android.content.Context;
import android.os.Bundle;
import com.google.android.gms.gcm.GcmNetworkManager;
import com.google.android.gms.gcm.PeriodicTask;
import com.google.android.gms.gcm.Task;
import java.util.concurrent.TimeUnit;

public class ConfigSyncServiceScheduler {

  static final String SYNCHRONIZE_CONFIG = "SynchronizeConfig";

  private static final long SYNC_PERIOD = TimeUnit.HOURS.toSeconds(6);
  private static final long FLEX_PERIOD = TimeUnit.MINUTES.toSeconds(15);

  private final GcmNetworkManager gcmNetworkManager;
  private final String apiKey;

  public ConfigSyncServiceScheduler(Context context, String apiKey) {
    this.gcmNetworkManager = GcmNetworkManager.getInstance(context);
    this.apiKey = apiKey;
  }

  public void scheduleSyncTask() {
    Bundle extras = new Bundle();
    extras.putString(ConfigSyncService.API_KEY_EXTRA, apiKey);
    PeriodicTask task = new PeriodicTask.Builder().setService(ConfigSyncService.class)
        .setTag(SYNCHRONIZE_CONFIG)
        .setPeriod(SYNC_PERIOD)
        .setFlex(FLEX_PERIOD)
        .setRequiredNetwork(Task.NETWORK_STATE_CONNECTED)
        .setExtras(extras)
        .build();
    gcmNetworkManager.schedule(task);
  }
}

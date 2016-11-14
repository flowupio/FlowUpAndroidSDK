/*
 * Copyright (C) 2016 Go Karumi S.L.
 */

package io.flowup.reporter.android;

import android.content.Context;
import android.os.Bundle;
import com.google.android.gms.gcm.GcmNetworkManager;
import com.google.android.gms.gcm.PeriodicTask;
import java.util.concurrent.TimeUnit;

public class CleanOldReportsServiceScheduler {

  static final String CLEAN_OLD_REPORTS = "CleanOldReports";

  private static final long SYNC_PERIOD = TimeUnit.DAYS.toSeconds(1);
  private static final long FLEX_PERIOD = TimeUnit.HOURS.toSeconds(1);

  private final GcmNetworkManager gcmNetworkManager;

  public CleanOldReportsServiceScheduler(Context context) {
    this.gcmNetworkManager = GcmNetworkManager.getInstance(context);
  }

  public void scheduleCleanTask() {
    Bundle extras = new Bundle();
    PeriodicTask task = new PeriodicTask.Builder().setService(CleanOldReportsService.class)
        .setTag(CLEAN_OLD_REPORTS)
        .setPeriod(SYNC_PERIOD)
        .setFlex(FLEX_PERIOD)
        .setExtras(extras)
        .build();
    gcmNetworkManager.schedule(task);
  }
}

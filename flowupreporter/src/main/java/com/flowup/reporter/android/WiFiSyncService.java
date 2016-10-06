/*
 * Copyright (C) 2016 Go Karumi S.L.
 */

package com.flowup.reporter.android;

import com.google.android.gms.gcm.GcmTaskService;
import com.google.android.gms.gcm.TaskParams;

import static com.flowup.reporter.android.WiFiSyncServiceScheduler.SYNCHRONIZE_METRICS_REPORT;
import static com.google.android.gms.gcm.GcmNetworkManager.RESULT_FAILURE;
import static com.google.android.gms.gcm.GcmNetworkManager.RESULT_RESCHEDULE;
import static com.google.android.gms.gcm.GcmNetworkManager.RESULT_SUCCESS;

public class WiFiSyncService extends GcmTaskService {

  @Override public int onRunTask(TaskParams taskParams) {
    int result = RESULT_RESCHEDULE;
    if (isTaskSupported(taskParams)) {
      result = RESULT_FAILURE;
    } else if (isWiFiConnectionEnabled()) {
      if (synchronizeMetricsReport()) {
        result = RESULT_SUCCESS;
      } else {
        result = RESULT_RESCHEDULE;
      }
    }
    return result;
  }

  private boolean isTaskSupported(TaskParams taskParams) {
    return !taskParams.getTag().equals(SYNCHRONIZE_METRICS_REPORT);
  }

  private boolean isWiFiConnectionEnabled() {
    return false;
  }

  private boolean synchronizeMetricsReport() {
    return false;
  }
}

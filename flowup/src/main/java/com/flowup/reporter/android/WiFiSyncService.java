/*
 * Copyright (C) 2016 Go Karumi S.L.
 */

package com.flowup.reporter.android;

import com.flowup.R;
import com.flowup.reporter.ReportResult;
import com.flowup.reporter.apiclient.ApiClient;
import com.flowup.reporter.model.Reports;
import com.flowup.reporter.storage.ReportsStorage;
import com.google.android.gms.gcm.GcmTaskService;
import com.google.android.gms.gcm.TaskParams;

import static com.flowup.reporter.android.WiFiSyncServiceScheduler.SYNCHRONIZE_METRICS_REPORT;
import static com.google.android.gms.gcm.GcmNetworkManager.RESULT_FAILURE;
import static com.google.android.gms.gcm.GcmNetworkManager.RESULT_RESCHEDULE;
import static com.google.android.gms.gcm.GcmNetworkManager.RESULT_SUCCESS;

public class WiFiSyncService extends GcmTaskService {

  private ApiClient apiClient;
  private ReportsStorage reportsStorage;

  @Override public void onCreate() {
    super.onCreate();
    String scheme = getString(R.string.flowup_scheme);
    String host = getString(R.string.flowup_host);
    int port = getResources().getInteger(R.integer.flowup_port);
    apiClient = new ApiClient(scheme, host, port);
    reportsStorage = new ReportsStorage(this);
  }

  @Override public int onRunTask(TaskParams taskParams) {
    if (!isTaskSupported(taskParams)) {
      return RESULT_FAILURE;
    }

    if (!isWiFiConnectionEnabled()) {
      return RESULT_RESCHEDULE;
    }

    return syncStoredReports();
  }

  private boolean isTaskSupported(TaskParams taskParams) {
    return !taskParams.getTag().equals(SYNCHRONIZE_METRICS_REPORT);
  }

  private boolean isWiFiConnectionEnabled() {
    return false;
  }

  private int syncStoredReports() {
    Reports reports = reportsStorage.getReports();
    if (reports != null) {
      ReportResult result = apiClient.sendReports(reports);
      if (result.isSuccess()) {
        reportsStorage.deleteReports(reports);
        return RESULT_SUCCESS;
      } else {
        return RESULT_FAILURE;
      }
    } else {
      return RESULT_SUCCESS;
    }
  }
}

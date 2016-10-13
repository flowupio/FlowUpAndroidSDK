/*
 * Copyright (C) 2016 Go Karumi S.L.
 */

package com.flowup.reporter.android;

import android.util.Log;
import com.flowup.R;
import com.flowup.reporter.FlowUpReporter;
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

  private static final String LOGTAG = "FlowUp.WiFiSyncService";
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

    return syncStoredReports();
  }

  private boolean isTaskSupported(TaskParams taskParams) {
    return taskParams.getTag().equals(SYNCHRONIZE_METRICS_REPORT);
  }

  private int syncStoredReports() {
    Log.d(LOGTAG, "Let's start with the sync process");
    Reports reports = reportsStorage.getReports(FlowUpReporter.NUMBER_OF_REPORTS_PER_REQUEST);
    if (reports == null) {
      return RESULT_SUCCESS;
    }
    ReportResult.Error error;
    ReportResult result;
    do {
      Log.d(LOGTAG, reports.getReportsIds().size() + " reports to sync");
      result = apiClient.sendReports(reports);
      if (result.isSuccess()) {
        Log.d(LOGTAG, "Api response successful");
        reportsStorage.deleteReports(reports);
      } else {
        Log.d(LOGTAG, "Api response error");
      }
      reports = reportsStorage.getReports(FlowUpReporter.NUMBER_OF_REPORTS_PER_REQUEST);
      if (reports != null) {
        Log.d(LOGTAG, "Let's continue reporting, we have "
            + reports.getReportsIds().size()
            + " reports pending");
      }
      error = result.getError();
    } while (reports != null && result.isSuccess());
    if (error == ReportResult.Error.NETWORK_ERROR) {
      Log.d(LOGTAG, "The last sync failed due to a network error, so let's reschedule a new task");
      return RESULT_RESCHEDULE;
    } else if (!result.isSuccess()) {
      Log.d(LOGTAG, "The last sync failed due to an unknown error");
      return RESULT_FAILURE;
    } else {
      Log.d(LOGTAG, "Sync process finished with a successful result");
      return RESULT_SUCCESS;
    }
  }
}

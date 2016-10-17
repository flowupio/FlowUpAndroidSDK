/*
 * Copyright (C) 2016 Go Karumi S.L.
 */

package com.flowup.reporter.android;

import com.flowup.R;
import com.flowup.logger.Logger;
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

  static final String API_KEY_EXTRA = "apiKeyExtra";

  private ApiClient apiClient;
  private ReportsStorage reportsStorage;

  @Override public void onCreate() {
    super.onCreate();
    reportsStorage = new ReportsStorage(this);
  }

  @Override public int onRunTask(TaskParams taskParams) {
    if (!isTaskSupported(taskParams)) {
      return RESULT_FAILURE;
    }
    String apiKey = taskParams.getExtras().getString(API_KEY_EXTRA);
    String scheme = getString(R.string.flowup_scheme);
    String host = getString(R.string.flowup_host);
    int port = getResources().getInteger(R.integer.flowup_port);
    apiClient = new ApiClient(apiKey, scheme, host, port);
    return syncStoredReports();
  }

  private boolean isTaskSupported(TaskParams taskParams) {
    return taskParams.getTag().equals(SYNCHRONIZE_METRICS_REPORT);
  }

  private int syncStoredReports() {
    Logger.d("Let's start with the sync process");
    Reports reports = reportsStorage.getReports(FlowUpReporter.NUMBER_OF_REPORTS_PER_REQUEST);
    if (reports == null) {
      Logger.d("There are no reports to sync.");
      return RESULT_SUCCESS;
    }
    ReportResult.Error error;
    ReportResult result;
    do {
      Logger.d(reports.getReportsIds().size() + " reports to sync");
      Logger.d(reports.toString());
      result = apiClient.sendReports(reports);
      if (result.isSuccess()) {
        Logger.d("Api response successful");
        reportsStorage.deleteReports(reports);
      } else if (ReportResult.Error.UNAUTHORIZED == result.getError()) {
        Logger.e("Api response error: " + result.getError());
        reportsStorage.deleteReports(reports);
      } else {
        Logger.e("Api response error: " + result.getError());
      }
      reports = reportsStorage.getReports(FlowUpReporter.NUMBER_OF_REPORTS_PER_REQUEST);
      if (reports != null) {
        Logger.d("Let's continue reporting, we have "
            + reports.getReportsIds().size()
            + " reports pending");
      }
      error = result.getError();
    } while (reports != null && result.isSuccess());
    if (error == ReportResult.Error.NETWORK_ERROR) {
      Logger.e("The last sync failed due to a network error, so let's reschedule a new task");
      return RESULT_RESCHEDULE;
    } else if (!result.isSuccess()) {
      Logger.e("The last sync failed due to an unknown error");
      return RESULT_FAILURE;
    } else {
      Logger.e("Sync process finished with a successful result");
      return RESULT_SUCCESS;
    }
  }
}

/*
 * Copyright (C) 2016 Go Karumi S.L.
 */

package io.flowup.reporter.android;

import android.os.Bundle;
import com.google.android.gms.gcm.GcmTaskService;
import com.google.android.gms.gcm.TaskParams;
import io.flowup.R;
import io.flowup.logger.Logger;
import io.flowup.reporter.FlowUpReporter;
import io.flowup.apiclient.ApiClientResult;
import io.flowup.reporter.apiclient.ReporterApiClient;
import io.flowup.reporter.model.Reports;
import io.flowup.reporter.storage.ReportsStorage;

import static com.google.android.gms.gcm.GcmNetworkManager.RESULT_FAILURE;
import static com.google.android.gms.gcm.GcmNetworkManager.RESULT_RESCHEDULE;
import static com.google.android.gms.gcm.GcmNetworkManager.RESULT_SUCCESS;
import static io.flowup.reporter.android.WiFiSyncServiceScheduler.SYNCHRONIZE_METRICS_REPORT;

public class WiFiSyncService extends GcmTaskService {

  static final String API_KEY_EXTRA = "apiKeyExtra";

  private ReporterApiClient reporterApiClient;
  private ReportsStorage reportsStorage;

  @Override public int onRunTask(TaskParams taskParams) {
    if (!isTaskSupported(taskParams)) {
      return RESULT_FAILURE;
    }
    String apiKey = getApiKey(taskParams);
    String scheme = getString(R.string.flowup_scheme);
    String host = getString(R.string.flowup_host);
    int port = getResources().getInteger(R.integer.flowup_port);
    reportsStorage = new ReportsStorage(this);
    reporterApiClient = new ReporterApiClient(apiKey, scheme, host, port);
    return syncStoredReports();
  }

  private String getApiKey(TaskParams taskParams) {
    String apiKey = "";
    Bundle extras = taskParams.getExtras();
    if (extras != null) {
      return extras.getString(API_KEY_EXTRA);
    }
    return apiKey;
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
    ApiClientResult.Error error;
    ApiClientResult result;
    do {
      Logger.d(reports.getReportsIds().size() + " reports to sync");
      Logger.d(reports.toString());
      result = reporterApiClient.sendReports(reports);
      if (result.isSuccess()) {
        Logger.d("Api response successful");
        reportsStorage.deleteReports(reports);
      } else if (shouldDeleteReportsOnError(result)) {
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
    if (error == ApiClientResult.Error.NETWORK_ERROR) {
      Logger.e("The last sync failed due to a network error, so let's reschedule a new task");
      return RESULT_RESCHEDULE;
    } else if (error == ApiClientResult.Error.CLIENT_DISABLED) {
      Logger.e("The client trying to report data has been disabled");
      reportsStorage.clear();
      return RESULT_FAILURE;
    } else if (!result.isSuccess()) {
      Logger.e("The last sync failed due to an unknown error");
      return RESULT_FAILURE;
    } else {
      Logger.e("Sync process finished with a successful result");
      return RESULT_SUCCESS;
    }
  }

  private boolean shouldDeleteReportsOnError(ApiClientResult result) {
    ApiClientResult.Error error = result.getError();
    return ApiClientResult.Error.UNAUTHORIZED == error || ApiClientResult.Error.SERVER_ERROR == error;
  }
}

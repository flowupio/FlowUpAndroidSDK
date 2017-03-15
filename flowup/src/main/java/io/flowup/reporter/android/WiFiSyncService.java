/*
 * Copyright (C) 2016 Go Karumi S.L.
 */

package io.flowup.reporter.android;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import com.google.android.gms.gcm.TaskParams;
import io.flowup.R;
import io.flowup.android.Device;
import io.flowup.android.SafeGcmTaskService;
import io.flowup.apiclient.ApiClientResult;
import io.flowup.config.FlowUpConfig;
import io.flowup.config.apiclient.ConfigApiClient;
import io.flowup.config.storage.ConfigStorage;
import io.flowup.logger.Logger;
import io.flowup.reporter.FlowUpReporter;
import io.flowup.reporter.apiclient.ReportApiClient;
import io.flowup.reporter.model.Reports;
import io.flowup.reporter.storage.ReportsStorage;
import io.flowup.storage.SQLDelightfulOpenHelper;
import io.flowup.utils.Time;

import static com.google.android.gms.gcm.GcmNetworkManager.RESULT_FAILURE;
import static com.google.android.gms.gcm.GcmNetworkManager.RESULT_RESCHEDULE;
import static com.google.android.gms.gcm.GcmNetworkManager.RESULT_SUCCESS;
import static io.flowup.reporter.android.WiFiSyncServiceScheduler.SYNCHRONIZE_METRICS_REPORT;

public class WiFiSyncService extends SafeGcmTaskService {

  private ReportApiClient reportApiClient;
  private ReportsStorage reportsStorage;
  private FlowUpConfig flowUpConfig;
  private ConnectivityManager connectivityManager;

  @Override public int safeOnRunTask(TaskParams taskParams) {
    initializeDependencies(taskParams);
    return syncStoredReports();
  }

  @Override protected boolean isScheduledTaskSupported(TaskParams taskParams) {
    String apiKey = getApiKey(taskParams);
    boolean forceReportsEnabled = isDebugEnabled(taskParams);
    return taskParams.getTag().equals(SYNCHRONIZE_METRICS_REPORT) && isClientEnabled(apiKey,
        forceReportsEnabled);
  }

  private void initializeDependencies(TaskParams taskParams) {
    String apiKey = getApiKey(taskParams);
    boolean forceReportsEnabled = isDebugEnabled(taskParams);
    String scheme = getString(R.string.flowup_scheme);
    String host = getString(R.string.flowup_host);
    int port = getResources().getInteger(R.integer.flowup_port);
    Device device = new Device(this);
    reportsStorage = new ReportsStorage(SQLDelightfulOpenHelper.getInstance(this), new Time());
    reportApiClient = new ReportApiClient(apiKey, device, scheme, host, port, forceReportsEnabled);
    connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
  }

  private boolean isClientEnabled(String apiKey, boolean forceReportsEnabled) {
    String scheme = getString(R.string.flowup_scheme);
    String host = getString(R.string.flowup_host);
    int port = getResources().getInteger(R.integer.flowup_port);
    Device device = new Device(this);
    SQLDelightfulOpenHelper dbOpenHelper =
        SQLDelightfulOpenHelper.getInstance(getApplicationContext());
    flowUpConfig = new FlowUpConfig(new ConfigStorage(dbOpenHelper),
        new ConfigApiClient(apiKey, device, scheme, host, port, forceReportsEnabled));
    return flowUpConfig.getConfig().isEnabled();
  }

  private int syncStoredReports() {
    Logger.d("Let's start with the sync process");
    Reports reports = reportsStorage.getReports(FlowUpReporter.NUMBER_OF_REPORTS_PER_REQUEST);
    if (reports == null || reports.size() == 0) {
      Logger.d("There are no reports to sync.");
      return RESULT_SUCCESS;
    }
    ApiClientResult.Error error;
    ApiClientResult result;
    boolean isConnectedToWifi;
    do {
      Logger.d(reports.getReportsIds().size() + " reports to sync");
      Logger.d(reports.toString());
      result = reportApiClient.sendReports(reports);
      if (result.isSuccess()) {
        Logger.d("Api response successful");
        reportsStorage.deleteReports(reports);
      } else if (shouldDeleteReportsOnError(result)) {
        Logger.d("Api response error: " + result.getError());
        disableFlowUp();
        reportsStorage.deleteReports(reports);
      } else {
        Logger.d("Api response error: " + result.getError());
      }
      reports = reportsStorage.getReports(FlowUpReporter.NUMBER_OF_REPORTS_PER_REQUEST);
      if (reports != null) {
        Logger.d("Let's continue reporting, we have "
            + reports.getReportsIds().size()
            + " reports pending");
      }
      error = result.getError();
      isConnectedToWifi = isConnectedToWifi();
    } while (reports != null && result.isSuccess() && isConnectedToWifi);
    if (error == ApiClientResult.Error.NETWORK_ERROR || !isConnectedToWifi) {
      Logger.d("The last sync failed due to a network error, so let's reschedule a new task");
      return RESULT_RESCHEDULE;
    } else if (error == ApiClientResult.Error.CLIENT_DISABLED) {
      Logger.d("The client trying to report data has been disabled");
      disableFlowUp();
      reportsStorage.clear();
      return RESULT_FAILURE;
    } else if (!result.isSuccess()) {
      Logger.w("The last sync failed due to an unknown error");
      return RESULT_FAILURE;
    } else {
      Logger.d("Sync process finished with a successful result");
      return RESULT_SUCCESS;
    }
  }

  private void disableFlowUp() {
    flowUpConfig.disableClient();
  }

  private boolean shouldDeleteReportsOnError(ApiClientResult result) {
    ApiClientResult.Error error = result.getError();
    return ApiClientResult.Error.UNAUTHORIZED == error
        || ApiClientResult.Error.SERVER_ERROR == error;
  }

  public boolean isConnectedToWifi() {
    NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
    return activeNetworkInfo != null
        && activeNetworkInfo.getType() == ConnectivityManager.TYPE_WIFI
        && activeNetworkInfo.isConnected();
  }
}

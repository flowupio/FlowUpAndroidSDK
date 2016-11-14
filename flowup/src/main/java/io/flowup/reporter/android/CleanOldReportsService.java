/*
 * Copyright (C) 2016 Go Karumi S.L.
 */

package io.flowup.reporter.android;

import android.content.Context;
import com.google.android.gms.gcm.GcmTaskService;
import com.google.android.gms.gcm.TaskParams;
import io.flowup.reporter.storage.ReportsStorage;

import static com.google.android.gms.gcm.GcmNetworkManager.RESULT_FAILURE;
import static com.google.android.gms.gcm.GcmNetworkManager.RESULT_SUCCESS;
import static io.flowup.reporter.android.CleanOldReportsServiceScheduler.CLEAN_OLD_REPORTS;

public class CleanOldReportsService extends GcmTaskService {

  @Override public int onRunTask(TaskParams taskParams) {
    if (!isTaskSupported(taskParams)) {
      return RESULT_FAILURE;
    }
    deleteOldReports();
    return RESULT_SUCCESS;
  }

  private void deleteOldReports() {
    Context context = getApplicationContext();
    ReportsStorage storage = new ReportsStorage(context);
    storage.deleteOldReports();
  }

  private boolean isTaskSupported(TaskParams taskParams) {
    return taskParams.getTag().equals(CLEAN_OLD_REPORTS);
  }
}

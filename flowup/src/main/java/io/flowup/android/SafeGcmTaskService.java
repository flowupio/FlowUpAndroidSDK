/*
 * Copyright (C) 2016 Go Karumi S.L.
 */

package io.flowup.android;

import android.content.Context;
import android.os.Bundle;
import com.google.android.gms.gcm.GcmTaskService;
import com.google.android.gms.gcm.TaskParams;
import io.flowup.crashreporter.SafetyNet;

import static com.google.android.gms.gcm.GcmNetworkManager.RESULT_FAILURE;
import static com.google.android.gms.gcm.GcmNetworkManager.RESULT_SUCCESS;

public abstract class SafeGcmTaskService extends GcmTaskService {

  public static final String API_KEY_EXTRA = "apiKeyExtra";
  public static final String FORCE_REPORTS_EXTRA = "forceReportsExtra";

  @Override public int onRunTask(final TaskParams taskParams) {
    if (!isScheduledTaskSupported(taskParams)) {
      return RESULT_FAILURE;
    }
    Context context = getApplicationContext();
    SafetyNet safetyNet = new SafetyNet(context, getApiKey(taskParams), isDebugEnabled(taskParams));
    final int[] result = {
        RESULT_SUCCESS
    };
    safetyNet.executeSafely(new Runnable() {
      @Override public void run() {
        result[0] = safeOnRunTask(taskParams);
      }
    });
    return result[0];
  }

  protected abstract int safeOnRunTask(TaskParams taskParams);

  protected abstract boolean isScheduledTaskSupported(TaskParams taskParams);

  protected String getApiKey(TaskParams taskParams) {
    String apiKey = "";
    Bundle extras = taskParams.getExtras();
    if (extras != null) {
      return extras.getString(API_KEY_EXTRA);
    }
    return apiKey;
  }

  protected boolean isDebugEnabled(TaskParams taskParams) {
    Bundle extras = taskParams.getExtras();
    return extras != null && extras.getBoolean(FORCE_REPORTS_EXTRA);
  }
}

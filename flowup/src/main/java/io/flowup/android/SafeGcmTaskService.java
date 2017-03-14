/*
 * Copyright (C) 2016 Go Karumi S.L.
 */

package io.flowup.android;

import com.google.android.gms.gcm.GcmTaskService;
import com.google.android.gms.gcm.TaskParams;
import io.flowup.crashreporter.SafeNet;

import static com.google.android.gms.gcm.GcmNetworkManager.RESULT_SUCCESS;

public abstract class SafeGcmTaskService extends GcmTaskService {

  @Override public int onRunTask(final TaskParams taskParams) {
    SafeNet safeNet = new SafeNet();
    final int[] result = { RESULT_SUCCESS };
    safeNet.executeSafety(new Runnable() {
      @Override public void run() {
        result[0] = safeOnRunTask(taskParams);
      }
    });
    return result[0];
  }

  protected abstract int safeOnRunTask(TaskParams taskParams);
}

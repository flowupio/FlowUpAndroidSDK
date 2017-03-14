package io.flowup.android;

import com.google.android.gms.gcm.GcmTaskService;
import com.google.android.gms.gcm.TaskParams;
import io.flowup.crashreporter.SafeNet;

import static com.google.android.gms.gcm.GcmNetworkManager.RESULT_SUCCESS;

public abstract class SafeGcmTaskService extends GcmTaskService {

  @Override public int onRunTask(TaskParams taskParams) {
    SafeNet safeNet = new SafeNet();
    int result = RESULT_SUCCESS;
    try {
      result = safeOnRunTask(taskParams);
    } catch (Throwable t) {
      safeNet.reportException(t);
    }
    return result;
  }

  protected abstract int safeOnRunTask(TaskParams taskParams);
}

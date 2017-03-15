package io.flowup.android;

import com.google.android.gms.gcm.GcmTaskService;
import com.google.android.gms.gcm.TaskParams;
import io.flowup.crashreporter.SafetyNet;

import static com.google.android.gms.gcm.GcmNetworkManager.RESULT_SUCCESS;

public abstract class SafeGcmTaskService extends GcmTaskService {

  @Override public int onRunTask(final TaskParams taskParams) {
    SafetyNet safetyNet = new SafetyNet();
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
}

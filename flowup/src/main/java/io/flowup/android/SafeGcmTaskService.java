package io.flowup.android;

import com.google.android.gms.gcm.GcmTaskService;
import com.google.android.gms.gcm.TaskParams;
import io.flowup.crashreporter.CrashReporter;

import static com.google.android.gms.gcm.GcmNetworkManager.RESULT_SUCCESS;

public abstract class SaveGcmTaskService extends GcmTaskService {

  @Override public int onRunTask(TaskParams taskParams) {
    CrashReporter crashReporter = new CrashReporter();
    int result = RESULT_SUCCESS;
    try {
      result = safeOnRunTask(taskParams);
    } catch (Throwable t) {
      crashReporter.reportException(t);
    }
    return result;
  }

  protected abstract int safeOnRunTask(TaskParams taskParams);
}

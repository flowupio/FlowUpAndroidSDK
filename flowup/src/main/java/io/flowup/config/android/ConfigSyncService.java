/*
 * Copyright (C) 2016 Go Karumi S.L.
 */

package io.flowup.config.android;

import android.os.Bundle;
import com.google.android.gms.gcm.GcmTaskService;
import com.google.android.gms.gcm.TaskParams;
import io.flowup.R;
import io.flowup.android.Device;
import io.flowup.android.SafeGcmTaskService;
import io.flowup.config.FlowUpConfig;
import io.flowup.config.apiclient.ConfigApiClient;
import io.flowup.config.storage.ConfigStorage;
import io.flowup.logger.Logger;
import io.flowup.storage.SQLDelightfulOpenHelper;

import static com.google.android.gms.gcm.GcmNetworkManager.RESULT_FAILURE;
import static com.google.android.gms.gcm.GcmNetworkManager.RESULT_RESCHEDULE;
import static com.google.android.gms.gcm.GcmNetworkManager.RESULT_SUCCESS;

public class ConfigSyncService extends SafeGcmTaskService {

  static final String API_KEY_EXTRA = "apiKeyExtra";
  static final String FORCE_REPORTS_EXTRA = "forceReportsExtra";

  @Override public int safeOnRunTask(TaskParams taskParams) {
    Bundle extras = taskParams.getExtras();
    if (extras == null || extras.getString(API_KEY_EXTRA) == null || !taskParams.getTag()
        .equals(ConfigSyncServiceScheduler.SYNCHRONIZE_CONFIG)) {
      return RESULT_FAILURE;
    }
    Logger.d("Let's update the config!");
    boolean result =
        updateConfig(extras.getString(API_KEY_EXTRA), extras.getBoolean(FORCE_REPORTS_EXTRA));
    return result ? RESULT_SUCCESS : RESULT_RESCHEDULE;
  }

  private boolean updateConfig(String apiKey, boolean forceReportsEnabled) {
    String scheme = getString(R.string.flowup_scheme);
    String host = getString(R.string.flowup_host);
    int port = getResources().getInteger(R.integer.flowup_port);
    Device device = new Device(this);
    SQLDelightfulOpenHelper dbOpenHelper =
        SQLDelightfulOpenHelper.getInstance(getApplicationContext());
    FlowUpConfig flowUpConfig = new FlowUpConfig(new ConfigStorage(dbOpenHelper),
        new ConfigApiClient(apiKey, device, scheme, host, port, forceReportsEnabled));
    return flowUpConfig.updateConfig();
  }
}

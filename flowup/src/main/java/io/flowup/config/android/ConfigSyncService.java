/*
 * Copyright (C) 2016 Go Karumi S.L.
 */

package io.flowup.config.android;

import android.os.Bundle;
import com.google.android.gms.gcm.GcmTaskService;
import com.google.android.gms.gcm.TaskParams;
import io.flowup.R;
import io.flowup.android.Device;
import io.flowup.config.FlowUpConfig;
import io.flowup.config.apiclient.ConfigApiClient;
import io.flowup.config.storage.ConfigStorage;

import static com.google.android.gms.gcm.GcmNetworkManager.RESULT_FAILURE;
import static com.google.android.gms.gcm.GcmNetworkManager.RESULT_RESCHEDULE;
import static com.google.android.gms.gcm.GcmNetworkManager.RESULT_SUCCESS;

public class ConfigSyncService extends GcmTaskService {

  static final String API_KEY_EXTRA = "apiKeyExtra";

  @Override public int onRunTask(TaskParams taskParams) {
    Bundle extras = taskParams.getExtras();
    if (extras == null
        || extras.getString(API_KEY_EXTRA) == null
        || taskParams.getTag() != ConfigSyncServiceScheduler.SYNCHRONIZE_CONFIG) {
      return RESULT_FAILURE;
    }
    boolean result = updateConfig(extras.getString(API_KEY_EXTRA));
    return result ? RESULT_SUCCESS : RESULT_RESCHEDULE;
  }

  private boolean updateConfig(String apiKey) {
    String scheme = getString(R.string.flowup_scheme);
    String host = getString(R.string.flowup_host);
    int port = getResources().getInteger(R.integer.flowup_port);
    String uuid = new Device(this).getInstallationUUID();
    FlowUpConfig flowUpConfig = new FlowUpConfig(new ConfigStorage(getApplicationContext()),
        new ConfigApiClient(apiKey, uuid, scheme, host, port));
    return flowUpConfig.updateConfig();
  }
}

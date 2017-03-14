/*
 * Copyright (C) 2016 Go Karumi S.L.
 */

package io.flowup.config.android;

import android.os.Bundle;
import com.google.android.gms.gcm.TaskParams;
import io.flowup.R;
import io.flowup.android.Device;
import io.flowup.android.SafeGcmTaskService;
import io.flowup.config.FlowUpConfig;
import io.flowup.config.apiclient.ConfigApiClient;
import io.flowup.config.storage.ConfigStorage;
import io.flowup.logger.Logger;
import io.flowup.storage.SQLDelightfulOpenHelper;

import static com.google.android.gms.gcm.GcmNetworkManager.RESULT_RESCHEDULE;
import static com.google.android.gms.gcm.GcmNetworkManager.RESULT_SUCCESS;

public class ConfigSyncService extends SafeGcmTaskService {

  @Override public int safeOnRunTask(TaskParams taskParams) {
    Logger.d("Let's update the config!");
    Bundle extras = taskParams.getExtras();
    boolean result =
        updateConfig(extras.getString(API_KEY_EXTRA), extras.getBoolean(FORCE_REPORTS_EXTRA));
    return result ? RESULT_SUCCESS : RESULT_RESCHEDULE;
  }

  @Override protected boolean isTaskTagSupported(TaskParams taskParams) {
    return taskParams.getExtras() != null && taskParams.getTag()
        .equals(ConfigSyncServiceScheduler.SYNCHRONIZE_CONFIG);
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

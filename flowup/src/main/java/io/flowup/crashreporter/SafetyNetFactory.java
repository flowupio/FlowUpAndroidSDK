/*
 * Copyright (C) 2016 Go Karumi S.L.
 */

package io.flowup.crashreporter;

import android.content.Context;
import io.flowup.R;
import io.flowup.android.Device;
import io.flowup.crashreporter.apiclient.CrashReporterApiClient;

public class SafetyNetFactory {

  public static SafetyNet getSafetyNet(Context context, String apiKey, boolean debugEnabled) {
    String scheme = context.getString(R.string.flowup_scheme);
    String host = context.getString(R.string.flowup_host);
    int port = context.getResources().getInteger(R.integer.flowup_port);
    CrashReporterApiClient apiClient =
        new CrashReporterApiClient(apiKey, new Device(context), scheme, host, port, debugEnabled);
    return new SafetyNet(apiClient);
  }
}

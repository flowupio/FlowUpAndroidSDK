/*
 * Copyright (C) 2016 Go Karumi S.L.
 */

package io.flowup.crashreporter;

import android.content.Context;
import android.os.Looper;
import io.flowup.R;
import io.flowup.android.Device;
import io.flowup.crashreporter.apiclient.CrashReporterApiClient;
import io.flowup.logger.Logger;

public class SafeNet {

  private final CrashReporterApiClient crashReporterApiClient;

  public SafeNet(Context context, String apiKey, boolean debugEnabled) {
    if (context == null) {
      crashReporterApiClient = null;
    } else {
      String scheme = context.getString(R.string.flowup_scheme);
      String host = context.getString(R.string.flowup_host);
      int port = context.getResources().getInteger(R.integer.flowup_port);
      this.crashReporterApiClient =
          new CrashReporterApiClient(apiKey, new Device(context), scheme, host, port, debugEnabled);
    }
  }

  public void executeSafetyOnNewThread(final Runnable runnable) {
    new Thread(new Runnable() {
      @Override public void run() {
        executeSafety(runnable);
      }
    }).start();
  }

  public void executeSafety(Runnable runnable) {
    try {
      runnable.run();
    } catch (Throwable t) {
      Logger.e("Exception catch", t);
      reportException(t);
    }
  }

  private void reportException(final Throwable t) {
    if (Looper.getMainLooper() == Looper.myLooper()) {
      new Thread(new Runnable() {
        @Override public void run() {
          sendErrorReport(t);
        }
      }).start();
    } else {
      sendErrorReport(t);
    }
  }

  private void sendErrorReport(Throwable t) {
    crashReporterApiClient.reportError(t);
  }
}

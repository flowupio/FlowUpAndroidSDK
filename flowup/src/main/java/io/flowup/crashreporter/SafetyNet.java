/*
 * Copyright (C) 2016 Go Karumi S.L.
 */

package io.flowup.crashreporter;

import android.os.Looper;
import io.flowup.crashreporter.apiclient.CrashReporterApiClient;
import io.flowup.logger.Logger;

public class SafetyNet {

  private final CrashReporterApiClient crashReporterApiClient;
  private final boolean forceMainThreadReport;

  SafetyNet(CrashReporterApiClient crashReporterApiClient) {
    this(crashReporterApiClient, false);
  }

  SafetyNet(CrashReporterApiClient crashReporterApiClient, boolean forceMainThreadReport) {
    this.crashReporterApiClient = crashReporterApiClient;
    this.forceMainThreadReport = forceMainThreadReport;
  }

  public void executeSafelyOnNewThread(final Runnable runnable) {
    new Thread(new Runnable() {
      @Override public void run() {
        executeSafely(runnable);
      }
    }).start();
  }

  public void executeSafely(Runnable runnable) {
    try {
      runnable.run();
    } catch (Throwable t) {
      Logger.e("Exception catch", t);
      reportException(t);
    }
  }

  private void reportException(final Throwable t) {
    if (!forceMainThreadReport && Looper.getMainLooper() == Looper.myLooper()) {
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

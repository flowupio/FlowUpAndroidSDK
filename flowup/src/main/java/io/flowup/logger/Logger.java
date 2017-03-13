package io.flowup.logger;

import android.util.Log;

public class Logger {

  private static String LOGTAG = "FlowUp";

  private static boolean enabled = false;

  public static void setEnabled(boolean enabled) {
    Logger.enabled = enabled;
  }

  public static boolean isLogEnabled() {
    return enabled;
  }

  public static void d(String message) {
    if (!enabled) {
      return;
    }
    Log.d(LOGTAG, message);
  }

  public static void w(String message) {
    if (!enabled) {
      return;
    }
    Log.w(LOGTAG, message);
  }

  public static void e(String message) {
    e(message, null);
  }

  public static void e(String message, Throwable error) {
    if (!enabled) {
      return;
    }
    Log.e(LOGTAG, message, error);
  }
}

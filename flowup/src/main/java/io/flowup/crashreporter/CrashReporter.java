/*
 * Copyright (C) 2016 Go Karumi S.L.
 */

package io.flowup.crashreporter;

import android.util.Log;

public class CrashReporter implements Thread.UncaughtExceptionHandler {

  @Override public void uncaughtException(Thread thread, Throwable ex) {
    Log.e("DEPURAR", "FUCK YEAH!!!! Exception catch in thread " + thread.getName(), ex);
  }
}

/*
 * Copyright (C) 2016 Go Karumi S.L.
 */

package io.flowup.android;

import android.os.Handler;
import android.os.Looper;

public class MainThread {

  private static final Handler UI_THREAD_HANDLER = new Handler(Looper.getMainLooper());

  public void post(Runnable runnable) {
    validateRunnable(runnable);
    UI_THREAD_HANDLER.post(runnable);
  }

  private void validateRunnable(Runnable runnable) {
    if (runnable == null) {
      throw new IllegalArgumentException("The runnable passed as parameter can't be null");
    }
  }
}

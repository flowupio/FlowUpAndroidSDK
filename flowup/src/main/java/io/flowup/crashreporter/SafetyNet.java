/*
 * Copyright (C) 2016 Go Karumi S.L.
 */

package io.flowup.crashreporter;

import io.flowup.logger.Logger;

public class SafetyNet {

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

  public void reportException(Throwable t) {

  }
}

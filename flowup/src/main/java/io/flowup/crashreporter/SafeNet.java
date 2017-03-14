/*
 * Copyright (C) 2016 Go Karumi S.L.
 */

package io.flowup.crashreporter;

import io.flowup.logger.Logger;

public class SafeNet {

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

  public void reportException(Throwable t) {

  }
}

/*
 * Copyright (C) 2016 Go Karumi S.L.
 */

package io.flowup.crashreporter;

public class FakeSafetyNet extends SafetyNet {

  public FakeSafetyNet() {
    super(null);
  }

  @Override public void executeSafelyOnNewThread(Runnable runnable) {
    runnable.run();
  }

  @Override public void executeSafely(Runnable runnable) {
    runnable.run();
  }
}

/*
 * Copyright (C) 2016 Go Karumi S.L.
 */

package io.flowup.crashreporter;

public class FakeSafeNet extends SafeNet {

  public FakeSafeNet() {
    super(null, null, false);
  }

  @Override public void executeSafetyOnNewThread(Runnable runnable) {
    runnable.run();
  }

  @Override public void executeSafety(Runnable runnable) {
    runnable.run();
  }
}

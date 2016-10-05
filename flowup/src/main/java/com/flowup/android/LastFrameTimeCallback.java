/*
 * Copyright (C) 2016 Go Karumi S.L.
 */

package com.flowup.android;

import android.view.Choreographer;

import static java.util.concurrent.TimeUnit.NANOSECONDS;

public abstract class LastFrameTimeCallback implements Choreographer.FrameCallback {

  private final Choreographer choreographer;

  private Long lastFrameTimeNanos = null;

  public LastFrameTimeCallback(Choreographer choreographer) {
    this.choreographer = choreographer;
  }

  @Override public void doFrame(long frameTimeNanos) {
    if (lastFrameTimeNanos != null) {
      long frameTimeMillis = NANOSECONDS.toMillis(frameTimeNanos - lastFrameTimeNanos);
      onFrameTimeMeasured(frameTimeMillis);
    }
    this.lastFrameTimeNanos = frameTimeNanos;
    choreographer.postFrameCallback(this);
  }

  protected abstract void onFrameTimeMeasured(long frameTimeMillis);
}

/*
 * Copyright (C) 2016 Go Karumi S.L.
 */

package io.flowup.android;

import android.annotation.SuppressLint;
import android.view.Choreographer;

@SuppressLint("NewApi")
public abstract class LastFrameTimeCallback implements Choreographer.FrameCallback {

  private final Choreographer choreographer;

  private Long lastFrameTimeNanos = null;

  public LastFrameTimeCallback(Choreographer choreographer) {
    this.choreographer = choreographer;
  }

  @Override public void doFrame(long frameTimeNanos) {
    if (lastFrameTimeNanos != null) {
      long frameTime = frameTimeNanos - lastFrameTimeNanos;
      onFrameTimeMeasured(frameTime);
    }
    this.lastFrameTimeNanos = frameTimeNanos;
    choreographer.removeFrameCallback(this);
    choreographer.postFrameCallback(this);
  }

  protected abstract void onFrameTimeMeasured(long frameTimeMillis);
}

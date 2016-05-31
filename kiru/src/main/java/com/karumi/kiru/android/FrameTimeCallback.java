/*
 * Copyright (C) 2016 Go Karumi S.L.
 */

package com.karumi.kiru.android;

import android.view.Choreographer;

public class FrameTimeCallback implements Choreographer.FrameCallback {

  private final Choreographer choreographer;

  private long frameTimeNanos = 0;

  public FrameTimeCallback(Choreographer choreographer) {
    this.choreographer = choreographer;
  }

  @Override public void doFrame(long frameTimeNanos) {
    this.frameTimeNanos = frameTimeNanos;
    choreographer.postFrameCallback(this);
  }

  public long getFrameTimeNanos() {
    return frameTimeNanos;
  }

  public void reset() {
    frameTimeNanos = 0;
  }
}

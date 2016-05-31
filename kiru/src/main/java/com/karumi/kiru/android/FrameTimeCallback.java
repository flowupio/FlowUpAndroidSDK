/*
 * Copyright (C) 2016 Go Karumi S.L.
 */

package com.karumi.kiru.android;

import android.view.Choreographer;

public class FrameTimeCallback implements Choreographer.FrameCallback {

  private long frameTimeNanos = 0;

  @Override public void doFrame(long frameTimeNanos) {
    this.frameTimeNanos = frameTimeNanos;
  }

  public long getFrameTimeNanos() {
    return frameTimeNanos;
  }

  public void reset() {
    frameTimeNanos = 0;
  }
}

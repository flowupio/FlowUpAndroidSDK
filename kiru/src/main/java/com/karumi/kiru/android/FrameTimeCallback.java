/*
 * Copyright (C) 2016 Go Karumi S.L.
 */

package com.karumi.kiru.android;

import android.view.Choreographer;

public class FrameTimeCallback implements Choreographer.FrameCallback {

  private final Choreographer choreographer;

  private long numberOfFrames = 0;
  private long frameTimeNanos = 0;

  public FrameTimeCallback(Choreographer choreographer) {
    this.choreographer = choreographer;
  }

  @Override public void doFrame(long frameTimeNanos) {
    this.numberOfFrames++;
    this.frameTimeNanos += frameTimeNanos;
    choreographer.postFrameCallback(this);
  }

  public long getFrameTimeNanos() {
    return frameTimeNanos / numberOfFrames;
  }

  public void reset() {
    numberOfFrames = 0;
    frameTimeNanos = 0;
  }
}

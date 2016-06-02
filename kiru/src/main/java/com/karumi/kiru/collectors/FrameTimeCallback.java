/*
 * Copyright (C) 2016 Go Karumi S.L.
 */

package com.karumi.kiru.collectors;

import android.view.Choreographer;

class FrameTimeCallback implements Choreographer.FrameCallback {

  private final Choreographer choreographer;

  private long numberOfFrames = 0;
  private long accumulatedFrameTime = 0;
  private long lastFrameTimeNanos = 0;

  FrameTimeCallback(Choreographer choreographer) {
    this.choreographer = choreographer;
  }

  @Override public void doFrame(long frameTimeNanos) {
    if (lastFrameTimeNanos != 0) {
      this.numberOfFrames++;
      this.accumulatedFrameTime += frameTimeNanos - lastFrameTimeNanos;
    }
    this.lastFrameTimeNanos = frameTimeNanos;
    choreographer.postFrameCallback(this);
  }

  long getFrameTime() {
    if (numberOfFrames == 0) {
      return 0;
    }
    return accumulatedFrameTime / numberOfFrames;
  }

  void reset() {
    numberOfFrames = 0;
    accumulatedFrameTime = 0;
  }

  protected long getNumberOfFrames() {
    return numberOfFrames;
  }
}

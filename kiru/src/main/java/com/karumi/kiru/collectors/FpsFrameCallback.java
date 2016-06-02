/*
 * Copyright (C) 2016 Go Karumi S.L.
 */

package com.karumi.kiru.collectors;

import android.view.Choreographer;

class FpsFrameCallback implements Choreographer.FrameCallback {

  private final Choreographer choreographer;

  private long numberOfFrames = 0;
  private long frameTimeNanos = 0;

  FpsFrameCallback(Choreographer choreographer) {
    this.choreographer = choreographer;
  }

  @Override public void doFrame(long frameTimeNanos) {
    this.numberOfFrames++;
    this.frameTimeNanos += frameTimeNanos;
    choreographer.postFrameCallback(this);
  }

  int getFPS() {
    long averageFrameTime = frameTimeNanos / numberOfFrames;
    return (int) (1000000000 / averageFrameTime);
  }

  void reset() {
    numberOfFrames = 0;
    frameTimeNanos = 0;
  }
}

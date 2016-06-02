/*
 * Copyright (C) 2016 Go Karumi S.L.
 */

package com.karumi.kiru.collectors;

import android.view.Choreographer;

class FpsFrameCallback implements Choreographer.FrameCallback {

  private final Choreographer choreographer;

  private long numberOfFrames = 0;
  private double frameTimeNanos = 0;

  FpsFrameCallback(Choreographer choreographer) {
    this.choreographer = choreographer;
  }

  @Override public void doFrame(long frameTimeNanos) {
    this.numberOfFrames++;
    this.frameTimeNanos += frameTimeNanos;
    choreographer.postFrameCallback(this);
  }

  double getFPS() {
    if (numberOfFrames == 0) {
      return 0;
    }

    return (numberOfFrames / frameTimeNanos) * 10000000000L;
  }

  void reset() {
    numberOfFrames = 0;
    frameTimeNanos = 0;
  }
}

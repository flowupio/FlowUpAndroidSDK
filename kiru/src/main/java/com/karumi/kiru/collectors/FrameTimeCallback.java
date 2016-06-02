/*
 * Copyright (C) 2016 Go Karumi S.L.
 */

package com.karumi.kiru.collectors;

import android.view.Choreographer;

class FrameTimeCallback implements Choreographer.FrameCallback {

  private final Choreographer choreographer;

  private long numberOfFrames = 0;
  private long frameTimeNanos = 0;

  FrameTimeCallback(Choreographer choreographer) {
    this.choreographer = choreographer;
  }

  @Override public void doFrame(long frameTimeNanos) {
    this.numberOfFrames++;
    this.frameTimeNanos += frameTimeNanos;
    choreographer.postFrameCallback(this);
  }

  long getFrameTimeNanos() {
    if(numberOfFrames == 0) {
      return 0;
    }
    return frameTimeNanos / numberOfFrames;
  }

  void reset() {
    numberOfFrames = 0;
    frameTimeNanos = 0;
  }
}

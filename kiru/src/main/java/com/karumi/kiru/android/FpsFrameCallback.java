/*
 * Copyright (C) 2016 Go Karumi S.L.
 */

package com.karumi.kiru.android;

import android.view.Choreographer;

//TODO: Implement this
public class FpsFrameCallback implements Choreographer.FrameCallback {

  private final Choreographer choreographer;

  public FpsFrameCallback(Choreographer choreographer) {
    this.choreographer = choreographer;
  }

  @Override public void doFrame(long frameTimeNanos) {
    choreographer.postFrameCallback(this);
  }

  public int getFPS() {
    return 0;
  }

  public void reset() {

  }
}

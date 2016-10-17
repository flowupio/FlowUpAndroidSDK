/*
 * Copyright (C) 2016 Go Karumi S.L.
 */

package io.flowup.collectors;

import android.view.Choreographer;
import com.codahale.metrics.Timer;
import io.flowup.android.LastFrameTimeCallback;

import static java.util.concurrent.TimeUnit.NANOSECONDS;

class FrameTimeCallback extends LastFrameTimeCallback {

  private final Timer timer;

  FrameTimeCallback(Timer timer, Choreographer choreographer) {
    super(choreographer);
    this.timer = timer;
  }

  @Override protected void onFrameTimeMeasured(long frameTimeNanos) {
    timer.update(frameTimeNanos, NANOSECONDS);
  }
}

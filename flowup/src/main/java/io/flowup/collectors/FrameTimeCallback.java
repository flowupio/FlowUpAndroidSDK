/*
 * Copyright (C) 2016 Go Karumi S.L.
 */

package io.flowup.collectors;

import android.view.Choreographer;
import com.codahale.metrics.Histogram;
import com.codahale.metrics.Timer;
import io.flowup.android.LastFrameTimeCallback;

import static java.util.concurrent.TimeUnit.NANOSECONDS;

class FrameTimeCallback extends LastFrameTimeCallback {

  private final Timer timer;
  private final Histogram histogram;

  FrameTimeCallback(Timer timer, Histogram histogram, Choreographer choreographer) {
    super(choreographer);
    this.timer = timer;
    this.histogram = histogram;
  }

  @Override protected void onFrameTimeMeasured(long frameTimeNanos) {
    timer.update(frameTimeNanos, NANOSECONDS);
    double fps = 1000000000d / frameTimeNanos;
    histogram.update((int) fps);
  }
}

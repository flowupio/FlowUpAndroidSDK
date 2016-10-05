/*
 * Copyright (C) 2016 Go Karumi S.L.
 */

package com.flowup.collectors;

import android.util.Log;
import android.view.Choreographer;
import com.codahale.metrics.Histogram;
import com.flowup.android.LastFrameTimeCallback;

class FpsFrameCallback extends LastFrameTimeCallback {

  private final Histogram histogram;

  FpsFrameCallback(Histogram histogram, Choreographer choreographer) {
    super(choreographer);
    this.histogram = histogram;
  }

  @Override protected void onFrameTimeMeasured(long frameTimeMillis) {
    double fps = 1000d / frameTimeMillis;
    Log.d("FlowUp", "Collecting FPS -> " + fps);
    histogram.update((int) fps);
  }
}

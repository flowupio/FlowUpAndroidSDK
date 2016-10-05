/*
 * Copyright (C) 2016 Go Karumi S.L.
 */

package com.flowup.collectors;

import android.util.Log;
import android.view.Choreographer;
import com.codahale.metrics.Timer;
import com.flowup.android.LastFrameTimeCallback;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

class FrameTimeCallback extends LastFrameTimeCallback {

  private final Timer timer;

  FrameTimeCallback(Timer timer, Choreographer choreographer) {
    super(choreographer);
    this.timer = timer;
  }

  @Override protected void onFrameTimeMeasured(long frameTimeMillis) {
    Log.d("FlowUp", "Collecting frame time in milliseconds -> " + frameTimeMillis);
    timer.update(frameTimeMillis, MILLISECONDS);
  }
}

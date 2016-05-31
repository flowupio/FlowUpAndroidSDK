/*
 * Copyright (C) 2016 Go Karumi S.L.
 */

package com.karumi.kiru.collectors;

import android.app.Activity;
import android.app.Application;
import android.util.Log;
import android.view.Choreographer;
import com.codahale.metrics.Gauge;
import com.codahale.metrics.MetricRegistry;
import com.karumi.kiru.android.EmptyActivityLifecycleCallback;
import com.karumi.kiru.android.FrameTimeCallback;
import com.karumi.kiru.metricnames.MetricNamesFactory;

class FrameTimeCollector extends EmptyActivityLifecycleCallback implements Collector {

  private final Application application;
  private final Choreographer choreographer;
  private final FrameTimeCallback frameTimeCallback;

  FrameTimeCollector(Application application) {
    this.application = application;
    this.choreographer = Choreographer.getInstance();
    this.frameTimeCallback = new FrameTimeCallback();
  }

  @Override public void initialize(MetricRegistry registry) {
    application.registerActivityLifecycleCallbacks(this);
    initializeGauge(registry);
  }

  @Override public void onActivityResumed(Activity activity) {
    choreographer.postFrameCallback(frameTimeCallback);
  }

  @Override public void onActivityPaused(Activity activity) {
    choreographer.removeFrameCallback(frameTimeCallback);
    frameTimeCallback.reset();
  }

  private void initializeGauge(MetricRegistry registry) {
    String fpsMetricName = MetricNamesFactory.getFrameTimeMetricName(application);
    registry.register(fpsMetricName, new Gauge<Long>() {
      @Override public Long getValue() {
        Log.d("KIRU", "Collecting frame time metric-> " + frameTimeCallback.getFrameTimeNanos());
        return frameTimeCallback.getFrameTimeNanos();
      }
    });
  }
}

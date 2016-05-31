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
import com.karumi.kiru.android.FpsFrameCallback;
import com.karumi.kiru.metricnames.MetricNamesFactory;

class FpsCollector extends EmptyActivityLifecycleCallback implements Collector {

  private final Application application;
  private final Choreographer choreographer;
  private final FpsFrameCallback fpsFrameCallback;

  FpsCollector(Application application) {
    this.application = application;
    this.choreographer = Choreographer.getInstance();
    this.fpsFrameCallback = new FpsFrameCallback();
  }

  @Override public void initialize(MetricRegistry registry) {
    application.registerActivityLifecycleCallbacks(this);
    initializeGauge(registry);
    choreographer.postFrameCallback(fpsFrameCallback);
  }

  @Override public void onActivityResumed(Activity activity) {
    choreographer.postFrameCallback(fpsFrameCallback);
  }

  @Override public void onActivityPaused(Activity activity) {
    choreographer.removeFrameCallback(fpsFrameCallback);
    fpsFrameCallback.reset();
  }

  private void initializeGauge(MetricRegistry registry) {
    String fpsMetricName = MetricNamesFactory.getFPSMetricName(application);
    registry.register(fpsMetricName, new Gauge<Integer>() {
      @Override public Integer getValue() {
        Log.d("KIRU", "Collecting FPS metric-> " + fpsFrameCallback.getFPS());
        return fpsFrameCallback.getFPS();
      }
    });
  }
}

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
import com.karumi.kiru.metricnames.MetricNamesGenerator;

class FpsCollector extends EmptyActivityLifecycleCallback implements Collector {

  private final MetricNamesGenerator metricNamesGenerator;
  private final Application application;
  private final Choreographer choreographer;
  private final FpsFrameCallback fpsFrameCallback;

  private MetricRegistry registry;

  FpsCollector(MetricNamesGenerator metricNamesGenerator, Application application) {
    this.metricNamesGenerator = metricNamesGenerator;
    this.application = application;
    this.choreographer = Choreographer.getInstance();
    this.fpsFrameCallback = new FpsFrameCallback(choreographer);
  }

  @Override public void initialize(MetricRegistry registry) {
    application.registerActivityLifecycleCallbacks(this);
    initializeGauge(registry);
    choreographer.postFrameCallback(fpsFrameCallback);
  }

  @Override public void onActivityResumed(Activity activity) {
    initializeGauge(registry);
    choreographer.postFrameCallback(fpsFrameCallback);
  }

  @Override public void onActivityPaused(Activity activity) {
    choreographer.removeFrameCallback(fpsFrameCallback);
    fpsFrameCallback.reset();
    removeGauge();
  }

  private void initializeGauge(MetricRegistry registry) {
    this.registry = registry;
    String fpsMetricName = metricNamesGenerator.getFPSMetricName();
    registry.register(fpsMetricName, new Gauge<Double>() {
      @Override public Double getValue() {
        double fps = fpsFrameCallback.getFPS();
        fpsFrameCallback.reset();
        Log.d("KIRU", "Collecting FPS metric-> " + fps);
        return fps;
      }
    });
  }

  private void removeGauge() {
    registry.remove(metricNamesGenerator.getFPSMetricName());
  }
}

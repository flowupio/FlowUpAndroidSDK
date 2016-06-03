/*
 * Copyright (C) 2016 Go Karumi S.L.
 */

package com.karumi.kiru.collectors;

import android.app.Application;
import android.util.Log;
import android.view.Choreographer;
import com.codahale.metrics.Gauge;
import com.codahale.metrics.MetricRegistry;
import com.karumi.kiru.metricnames.MetricNamesGenerator;

class FrameTimeCollector extends ApplicationLifecycleCollector implements Collector {

  private final MetricNamesGenerator metricNamesGenerator;
  private final Choreographer choreographer;
  private final FrameTimeCallback frameTimeCallback;

  private MetricRegistry registry;

  FrameTimeCollector(Application application, MetricNamesGenerator metricNamesGenerator) {
    super(application);
    this.metricNamesGenerator = metricNamesGenerator;
    this.choreographer = Choreographer.getInstance();
    this.frameTimeCallback = new FrameTimeCallback(choreographer);
  }

  @Override public void initialize(MetricRegistry registry) {
    super.initialize(registry);
    initializeGauge(registry);
    choreographer.postFrameCallback(frameTimeCallback);
  }

  @Override protected void onApplicationResumed() {
    initializeGauge(registry);
    choreographer.postFrameCallback(frameTimeCallback);
  }

  @Override protected void onApplicationPaused() {
    choreographer.removeFrameCallback(frameTimeCallback);
    frameTimeCallback.reset();
    removeGauge();
  }

  private void initializeGauge(MetricRegistry registry) {
    this.registry = registry;
    String fpsMetricName = metricNamesGenerator.getFrameTimeMetricName();
    registry.register(fpsMetricName, new Gauge<Long>() {
      @Override public Long getValue() {
        long frameTimeNanos = frameTimeCallback.getFrameTime();
        frameTimeCallback.reset();
        Log.d("KIRU", "Collecting frame time metric-> " + frameTimeNanos);
        return frameTimeNanos;
      }
    });
  }

  private void removeGauge() {
    registry.remove(metricNamesGenerator.getFrameTimeMetricName());
  }
}

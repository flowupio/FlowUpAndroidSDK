/*
 * Copyright (C) 2016 Go Karumi S.L.
 */

package com.flowup.collectors;

import android.app.Application;
import android.util.Log;
import android.view.Choreographer;
import com.codahale.metrics.Gauge;
import com.codahale.metrics.MetricRegistry;
import com.flowup.metricnames.MetricNamesGenerator;

class FrameTimeCollector extends ApplicationLifecycleCollector implements Collector {

  private final MetricNamesGenerator metricNamesGenerator;
  private final Choreographer choreographer;
  private final FrameTimeCallback frameTimeCallback;

  FrameTimeCollector(Application application, MetricNamesGenerator metricNamesGenerator) {
    super(application);
    this.metricNamesGenerator = metricNamesGenerator;
    this.choreographer = Choreographer.getInstance();
    this.frameTimeCallback = new FrameTimeCallback(choreographer);
  }

  @Override protected void onApplicationResumed(MetricRegistry registry) {
    initializeGauge(registry);
    choreographer.postFrameCallback(frameTimeCallback);
  }

  @Override protected void onApplicationPaused(MetricRegistry registry) {
    choreographer.removeFrameCallback(frameTimeCallback);
    frameTimeCallback.reset();
    removeGauge(registry);
  }

  private void initializeGauge(MetricRegistry registry) {
    String fpsMetricName = metricNamesGenerator.getFrameTimeMetricName();
    registry.register(fpsMetricName, new Gauge<Long>() {
      @Override public Long getValue() {
        long frameTimeNanos = frameTimeCallback.getFrameTime();
        frameTimeCallback.reset();
        Log.d("FlowUp", "Collecting frame time metric-> " + frameTimeNanos);
        return frameTimeNanos;
      }
    });
  }

  private void removeGauge(MetricRegistry registry) {
    registry.remove(metricNamesGenerator.getFrameTimeMetricName());
  }
}

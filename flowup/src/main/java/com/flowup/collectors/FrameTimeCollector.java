/*
 * Copyright (C) 2016 Go Karumi S.L.
 */

package com.flowup.collectors;

import android.app.Application;
import android.view.Choreographer;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import com.flowup.metricnames.MetricNamesGenerator;

class FrameTimeCollector extends ApplicationLifecycleCollector implements Collector {

  private final MetricNamesGenerator metricNamesGenerator;
  private final Choreographer choreographer;

  private FrameTimeCallback frameTimeCallback;

  FrameTimeCollector(Application application, MetricNamesGenerator metricNamesGenerator) {
    super(application);
    this.metricNamesGenerator = metricNamesGenerator;
    this.choreographer = Choreographer.getInstance();
  }

  @Override protected void onApplicationResumed(MetricRegistry registry) {
    Timer timer = initializeTimer(registry);
    frameTimeCallback = new FrameTimeCallback(timer, choreographer);
    choreographer.postFrameCallback(frameTimeCallback);
  }

  @Override protected void onApplicationPaused(MetricRegistry registry) {
    choreographer.removeFrameCallback(frameTimeCallback);
    removeTimer(registry);
  }

  private Timer initializeTimer(MetricRegistry registry) {
    String fpsMetricName = metricNamesGenerator.getFrameTimeMetricName();
    return registry.timer(fpsMetricName);
  }

  private void removeTimer(MetricRegistry registry) {
    registry.remove(metricNamesGenerator.getFrameTimeMetricName());
  }
}

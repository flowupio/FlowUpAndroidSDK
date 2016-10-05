/*
 * Copyright (C) 2016 Go Karumi S.L.
 */

package com.flowup.collectors;

import android.app.Activity;
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

  @Override protected void onApplicationResumed(Activity activity, MetricRegistry registry) {
    Timer timer = initializeTimer(activity, registry);
    frameTimeCallback = new FrameTimeCallback(timer, choreographer);
    choreographer.postFrameCallback(frameTimeCallback);
  }

  @Override protected void onApplicationPaused(Activity activity, MetricRegistry registry) {
    choreographer.removeFrameCallback(frameTimeCallback);
    removeTimer(activity, registry);
  }

  private Timer initializeTimer(Activity activity, MetricRegistry registry) {
    String fpsMetricName = metricNamesGenerator.getFrameTimeMetricName(activity);
    return registry.timer(fpsMetricName);
  }

  private void removeTimer(Activity activity, MetricRegistry registry) {
    registry.remove(metricNamesGenerator.getFrameTimeMetricName(activity));
  }
}

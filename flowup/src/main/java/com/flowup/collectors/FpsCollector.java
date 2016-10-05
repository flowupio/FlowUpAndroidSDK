/*
 * Copyright (C) 2016 Go Karumi S.L.
 */

package com.flowup.collectors;

import android.app.Activity;
import android.app.Application;
import android.view.Choreographer;
import com.codahale.metrics.Histogram;
import com.codahale.metrics.MetricRegistry;
import com.flowup.metricnames.MetricNamesGenerator;

class FpsCollector extends ApplicationLifecycleCollector implements Collector {

  private final MetricNamesGenerator metricNamesGenerator;
  private final Choreographer choreographer;
  private FpsFrameCallback fpsFrameCallback;

  FpsCollector(Application application, MetricNamesGenerator metricNamesGenerator) {
    super(application);
    this.metricNamesGenerator = metricNamesGenerator;
    this.choreographer = Choreographer.getInstance();
  }

  @Override protected void onApplicationResumed(Activity activity, MetricRegistry registry) {
    Histogram histogram = initializeHistogram(activity, registry);
    fpsFrameCallback = new FpsFrameCallback(histogram, choreographer);
    choreographer.postFrameCallback(fpsFrameCallback);
  }

  @Override protected void onApplicationPaused(Activity activity, MetricRegistry registry) {
    choreographer.removeFrameCallback(fpsFrameCallback);
    removeCounter(activity, registry);
  }

  private Histogram initializeHistogram(Activity activity, MetricRegistry registry) {
    String fpsMetricName = metricNamesGenerator.getFPSMetricName(activity);
    return registry.histogram(fpsMetricName);
  }

  private void removeCounter(Activity activity, MetricRegistry registry) {
    registry.remove(metricNamesGenerator.getFPSMetricName(activity));
  }
}

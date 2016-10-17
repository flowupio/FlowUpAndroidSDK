/*
 * Copyright (C) 2016 Go Karumi S.L.
 */

package com.flowup.collectors;

import android.app.Activity;
import android.app.Application;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.view.Choreographer;
import com.codahale.metrics.Histogram;
import com.codahale.metrics.Metric;
import com.codahale.metrics.MetricFilter;
import com.codahale.metrics.MetricRegistry;
import com.flowup.metricnames.MetricNamesGenerator;

@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN) class FpsCollector
    extends ApplicationLifecycleCollector implements Collector {

  private final MetricNamesGenerator metricNamesGenerator;
  private final Choreographer choreographer;
  private FpsFrameCallback fpsFrameCallback;
  private Histogram histogram;

  FpsCollector(Application application, MetricNamesGenerator metricNamesGenerator) {
    super(application);
    this.metricNamesGenerator = metricNamesGenerator;
    this.choreographer = Choreographer.getInstance();
  }

  @Override protected void onApplicationResumed(Activity activity, MetricRegistry registry) {
    histogram = initializeHistogram(activity, registry);
    fpsFrameCallback = new FpsFrameCallback(histogram, choreographer);
    choreographer.postFrameCallback(fpsFrameCallback);
  }

  @Override protected void onApplicationPaused(Activity activity, MetricRegistry registry) {
    choreographer.removeFrameCallback(fpsFrameCallback);
    removeHistogram(registry);
  }

  private Histogram initializeHistogram(Activity activity, MetricRegistry registry) {
    String fpsMetricName = metricNamesGenerator.getFPSMetricName(activity);
    return registry.histogram(fpsMetricName);
  }

  private void removeHistogram(MetricRegistry registry) {
    registry.removeMatching(new MetricFilter() {
      @Override public boolean matches(String name, Metric metric) {
        return metric.equals(histogram);
      }
    });
  }
}

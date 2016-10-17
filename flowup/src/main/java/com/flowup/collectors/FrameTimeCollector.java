/*
 * Copyright (C) 2016 Go Karumi S.L.
 */

package com.flowup.collectors;

import android.app.Activity;
import android.app.Application;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.view.Choreographer;
import com.codahale.metrics.Metric;
import com.codahale.metrics.MetricFilter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import com.flowup.metricnames.MetricNamesGenerator;

@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN) class FrameTimeCollector
    extends ApplicationLifecycleCollector implements Collector {

  private final MetricNamesGenerator metricNamesGenerator;
  private final Choreographer choreographer;

  private FrameTimeCallback frameTimeCallback;
  private Timer timer;

  FrameTimeCollector(Application application, MetricNamesGenerator metricNamesGenerator) {
    super(application);
    this.metricNamesGenerator = metricNamesGenerator;
    this.choreographer = Choreographer.getInstance();
  }

  @Override protected void onApplicationResumed(Activity activity, MetricRegistry registry) {
    timer = initializeTimer(activity, registry);
    frameTimeCallback = new FrameTimeCallback(timer, choreographer);
    choreographer.postFrameCallback(frameTimeCallback);
  }

  @Override protected void onApplicationPaused(Activity activity, MetricRegistry registry) {
    choreographer.removeFrameCallback(frameTimeCallback);
    removeTimer(registry);
  }

  private Timer initializeTimer(Activity activity, MetricRegistry registry) {
    String fpsMetricName = metricNamesGenerator.getFrameTimeMetricName(activity);
    return registry.timer(fpsMetricName);
  }

  private void removeTimer(MetricRegistry registry) {
    registry.removeMatching(new MetricFilter() {
      @Override public boolean matches(String name, Metric metric) {
        return metric.equals(timer);
      }
    });
  }
}

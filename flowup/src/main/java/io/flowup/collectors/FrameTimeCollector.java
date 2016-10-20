/*
 * Copyright (C) 2016 Go Karumi S.L.
 */

package io.flowup.collectors;

import android.app.Activity;
import android.app.Application;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.view.Choreographer;
import com.codahale.metrics.Histogram;
import com.codahale.metrics.Metric;
import com.codahale.metrics.MetricFilter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import io.flowup.metricnames.MetricNamesGenerator;
import java.util.HashMap;
import java.util.Map;

@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN) class FrameTimeCollector
    extends ApplicationLifecycleCollector implements Collector {

  private final MetricNamesGenerator metricNamesGenerator;
  private final Choreographer choreographer;

  private FrameTimeCallback frameTimeCallback;
  private Map<String, Timer> timers;
  private Map<String, Histogram> histograms;

  FrameTimeCollector(Application application, MetricNamesGenerator metricNamesGenerator) {
    super(application);
    this.metricNamesGenerator = metricNamesGenerator;
    this.choreographer = Choreographer.getInstance();
    this.timers = new HashMap<>();
    this.histograms = new HashMap<>();
  }

  @Override protected void onApplicationResumed(Activity activity, MetricRegistry registry) {
    Timer timer = getTimer(activity, registry);
    Histogram histogram = getHistogram(activity, registry);
    frameTimeCallback = new FrameTimeCallback(timer, histogram, choreographer);
    choreographer.postFrameCallback(frameTimeCallback);
  }

  @Override protected void onApplicationPaused(Activity activity, MetricRegistry registry) {
    choreographer.removeFrameCallback(frameTimeCallback);
    removeTimer(activity, registry);
    removeHistogram(activity, registry);
  }

  private Histogram getHistogram(Activity activity, MetricRegistry registry) {
    String activityName = activity.getClass().getName();
    Histogram histogram = histograms.get(activityName);
    if (histogram == null) {
      histogram = initializeHistogram(activity, registry);
      histograms.put(activityName, histogram);
    }
    return histogram;
  }

  private Timer getTimer(Activity activity, MetricRegistry registry) {
    String activityName = activity.getClass().getName();
    Timer timer = timers.get(activityName);
    if (timer == null) {
      timer = initializeTimer(activity, registry);
      timers.put(activityName, timer);
    }
    return timer;
  }

  private Timer initializeTimer(Activity activity, MetricRegistry registry) {
    String frameTimeMetricName = metricNamesGenerator.getFrameTimeMetricName(activity);
    return registry.timer(frameTimeMetricName);
  }

  private Histogram initializeHistogram(Activity activity, MetricRegistry registry) {
    String fpsMetricName = metricNamesGenerator.getFPSMetricName(activity);
    return registry.histogram(fpsMetricName);
  }

  private void removeTimer(final Activity activity, final MetricRegistry registry) {
    registry.removeMatching(new MetricFilter() {
      @Override public boolean matches(String name, Metric metric) {
        Timer timer = getTimer(activity, registry);
        return metric.equals(timer);
      }
    });
    timers.remove(activity.getClass().getName());
  }

  private void removeHistogram(final Activity activity, final MetricRegistry registry) {
    registry.removeMatching(new MetricFilter() {
      @Override public boolean matches(String name, Metric metric) {
        Histogram histogram = getHistogram(activity, registry);
        return metric.equals(histogram);
      }
    });
    histograms.remove(activity.getClass().getName());
  }
}

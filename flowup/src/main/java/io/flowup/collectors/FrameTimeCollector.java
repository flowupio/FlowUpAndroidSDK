/*
 * Copyright (C) 2016 Go Karumi S.L.
 */

package io.flowup.collectors;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Application;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.view.Choreographer;
import com.codahale.metrics.Histogram;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import io.flowup.metricnames.MetricNamesGenerator;
import java.util.HashMap;
import java.util.Map;

@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN) class FrameTimeCollector
    extends ApplicationLifecycleCollector {

  private final MetricNamesGenerator metricNamesGenerator;
  private final Choreographer choreographer;

  private Activity lastActivityResumed;
  private FrameTimeCallback frameTimeCallback;
  private Map<String, Timer> timers;
  private Map<String, Histogram> histograms;

  @TargetApi(Build.VERSION_CODES.JELLY_BEAN) FrameTimeCollector(Application application,
      MetricNamesGenerator metricNamesGenerator) {
    super(application);
    this.metricNamesGenerator = metricNamesGenerator;
    this.choreographer = Choreographer.getInstance();
    this.timers = new HashMap<>();
    this.histograms = new HashMap<>();
  }

  @Override public void forceUpdate(MetricRegistry registry) {
    if (lastActivityResumed == null) {
      return;
    }
    timers.clear();
    histograms.clear();
    Timer timer = getTimer(lastActivityResumed, registry);
    Histogram histogram = getHistogram(lastActivityResumed, registry);
    if (isInForeground) {
      frameTimeCallback = new FrameTimeCallback(timer, histogram, choreographer);
      choreographer.postFrameCallback(frameTimeCallback);
    }
  }

  @TargetApi(Build.VERSION_CODES.JELLY_BEAN) @Override
  protected void onApplicationResumed(Activity activity, MetricRegistry registry) {
    this.lastActivityResumed = activity;
    Timer timer = getTimer(activity, registry);
    Histogram histogram = getHistogram(activity, registry);
    frameTimeCallback = new FrameTimeCallback(timer, histogram, choreographer);
    choreographer.postFrameCallback(frameTimeCallback);
  }

  @TargetApi(Build.VERSION_CODES.JELLY_BEAN) @Override
  protected void onApplicationPaused(Activity activity, MetricRegistry registry) {
    this.lastActivityResumed = null;
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
    timers.remove(activity.getClass().getName());
  }

  private void removeHistogram(final Activity activity, final MetricRegistry registry) {
    histograms.remove(activity.getClass().getName());
  }
}

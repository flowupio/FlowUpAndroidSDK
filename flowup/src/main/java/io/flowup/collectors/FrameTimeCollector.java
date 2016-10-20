/*
 * Copyright (C) 2016 Go Karumi S.L.
 */

package io.flowup.collectors;

import android.app.Activity;
import android.app.Application;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.view.Choreographer;
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

  FrameTimeCollector(Application application, MetricNamesGenerator metricNamesGenerator) {
    super(application);
    this.metricNamesGenerator = metricNamesGenerator;
    this.choreographer = Choreographer.getInstance();
    this.timers = new HashMap<>();
  }

  @Override protected void onApplicationResumed(Activity activity, MetricRegistry registry) {
    Timer timer = getTimer(activity, registry);
    frameTimeCallback = new FrameTimeCallback(timer, choreographer);
    choreographer.postFrameCallback(frameTimeCallback);
  }

  @Override protected void onApplicationPaused(Activity activity, MetricRegistry registry) {
    choreographer.removeFrameCallback(frameTimeCallback);
    removeTimer(activity, registry);
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
    String fpsMetricName = metricNamesGenerator.getFrameTimeMetricName(activity);
    return registry.timer(fpsMetricName);
  }

  private void removeTimer(final Activity activity, final MetricRegistry registry) {
    registry.removeMatching(new MetricFilter() {
      @Override public boolean matches(String name, Metric metric) {
        Timer timer = getTimer(activity, registry);
        return metric.equals(timer);
      }
    });
  }
}

/*
 * Copyright (C) 2016 Go Karumi S.L.
 */

package io.flowup.collectors;

import android.app.Activity;
import android.app.Application;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import io.flowup.android.EmptyActivityLifecycleCallback;
import io.flowup.metricnames.MetricNamesGenerator;
import io.flowup.utils.Time;
import java.util.concurrent.TimeUnit;

class ActivityVisibleCollector implements Collector {

  private final Application application;
  private final MetricNamesGenerator generator;
  private final Time time;

  private long resumeTimeInNanos;

  ActivityVisibleCollector(Application application, MetricNamesGenerator generator, Time time) {
    this.application = application;
    this.generator = generator;
    this.time = time;
  }

  @Override public void initialize(final MetricRegistry registry) {
    application.registerActivityLifecycleCallbacks(new EmptyActivityLifecycleCallback() {

      @Override public void onActivityResumed(final Activity activity) {
        resumeTimeInNanos = time.nowInNanos();
      }

      @Override public void onActivityPaused(Activity activity) {
        long activityVisibleTimeInNanos = time.nowInNanos() - resumeTimeInNanos;
        String metricName = generator.getActivityVisibleMetricName(activity);
        Timer timer = registry.timer(metricName);
        timer.update(activityVisibleTimeInNanos, TimeUnit.NANOSECONDS);
      }
    });
  }
}

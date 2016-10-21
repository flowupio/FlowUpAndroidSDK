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

class ActivityVisibleCollector implements Collector {

  private final Application application;
  private final MetricNamesGenerator generator;

  private Timer.Context timerContext;

  ActivityVisibleCollector(Application application, MetricNamesGenerator generator) {
    this.application = application;
    this.generator = generator;
  }

  @Override public void initialize(final MetricRegistry registry) {
    application.registerActivityLifecycleCallbacks(new EmptyActivityLifecycleCallback() {

      @Override public void onActivityResumed(final Activity activity) {
        String metricName = generator.getActivityVisibleMetricName(activity);
        timerContext = registry.timer(metricName).time();
      }

      @Override public void onActivityPaused(Activity activity) {
        timerContext.stop();
      }
    });
  }
}

/*
 * Copyright (C) 2016 Go Karumi S.L.
 */

package io.flowup.collectors;

import android.app.Activity;
import android.app.Application;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.RequiresApi;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import io.flowup.android.EmptyActivityLifecycleCallback;
import io.flowup.metricnames.MetricNamesGenerator;

@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN) class ActivityLifecycleCollector
    implements Collector {

  private final Application application;
  private final MetricNamesGenerator metricNamesGenerator;

  ActivityLifecycleCollector(Application application, MetricNamesGenerator metricNamesGenerator) {
    this.application = application;
    this.metricNamesGenerator = metricNamesGenerator;
  }

  @Override public void initialize(final MetricRegistry registry) {
    application.registerActivityLifecycleCallbacks(new EmptyActivityLifecycleCallback() {

      private Timer.Context onCreateTimer;
      private Timer.Context onStartTimer;
      private Timer.Context onResumeTimer;
      private Timer.Context activityVisibleTimer;
      private Timer.Context onPauseTimer;
      private Timer.Context onStopTimer;
      private Timer.Context onDestroyTimer;

      @Override public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        onCreateTimer =
            registry.timer(metricNamesGenerator.getOnActivityCreatedMetricName(activity)).time();
      }

      @Override public void onActivityStarted(Activity activity) {
        onCreateTimer.stop();
        onStartTimer =
            registry.timer(metricNamesGenerator.getOnActivityStartedMetricName(activity)).time();
      }

      @Override public void onActivityResumed(Activity activity) {
        onStartTimer.stop();
        onResumeTimer =
            registry.timer(metricNamesGenerator.getOnActivityResumedMetricName(activity)).time();
        new Handler(Looper.getMainLooper()).post(new Runnable() {
          @Override public void run() {
            onResumeTimer.stop();
          }
        });
        activityVisibleTimer =
            registry.timer(metricNamesGenerator.getActivityVisibleMetricName(activity)).time();
      }

      @Override public void onActivityPaused(Activity activity) {
        activityVisibleTimer.stop();
        onPauseTimer =
            registry.timer(metricNamesGenerator.getOnActivityPausedMetricName(activity)).time();
      }

      @Override public void onActivityStopped(Activity activity) {
        onPauseTimer.stop();
        onStopTimer =
            registry.timer(metricNamesGenerator.getOnActivityStoppedMetricName(activity)).time();
      }

      @Override public void onActivityDestroyed(Activity activity) {
        onStopTimer.stop();
        onDestroyTimer =
            registry.timer(metricNamesGenerator.getOnActivityDestroyedMetricName(activity)).time();
        new Handler(Looper.myLooper()).post(new Runnable() {
          @Override public void run() {
            onDestroyTimer.stop();
          }
        });
      }
    });
  }
}

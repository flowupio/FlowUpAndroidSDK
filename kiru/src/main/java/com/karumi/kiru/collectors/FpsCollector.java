/*
 * Copyright (C) 2016 Go Karumi S.L.
 */

package com.karumi.kiru.collectors;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import com.codahale.metrics.Gauge;
import com.codahale.metrics.MetricRegistry;
import com.karumi.kiru.metricnames.MetricNamesFactory;

public class FpsCollector implements Collector, Application.ActivityLifecycleCallbacks {

  private final Application application;

  public FpsCollector(Application application) {
    this.application = application;
  }

  @Override public void initialize(MetricRegistry registry) {
    application.registerActivityLifecycleCallbacks(this);
    initializeGauge(registry);
  }

  private void initializeGauge(MetricRegistry registry) {
    String fpsMetricName = MetricNamesFactory.getFPSMetricName();
    registry.register(fpsMetricName, new Gauge<Integer>() {
      @Override public Integer getValue() {
        return null;
      }
    });
  }

  @Override public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

  }

  @Override public void onActivityStarted(Activity activity) {

  }

  @Override public void onActivityResumed(Activity activity) {

  }

  @Override public void onActivityPaused(Activity activity) {

  }

  @Override public void onActivityStopped(Activity activity) {

  }

  @Override public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

  }

  @Override public void onActivityDestroyed(Activity activity) {

  }
}

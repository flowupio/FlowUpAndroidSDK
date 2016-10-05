/*
 * Copyright (C) 2016 Go Karumi S.L.
 */

package com.flowup.collectors;

import android.app.Activity;
import android.app.Application;
import com.codahale.metrics.MetricRegistry;
import com.flowup.android.EmptyActivityLifecycleCallback;

abstract class ApplicationLifecycleCollector implements Collector {

  private final Application application;
  private MetricRegistry registry;

  ApplicationLifecycleCollector(Application application) {
    this.application = application;
  }

  @Override public void initialize(MetricRegistry registry) {
    this.registry = registry;
    registerActivityLifecycleCallbacks();
  }

  private void registerActivityLifecycleCallbacks() {
    application.registerActivityLifecycleCallbacks(new EmptyActivityLifecycleCallback() {
      @Override public void onActivityResumed(Activity activity) {
        super.onActivityResumed(activity);
        onApplicationResumed(activity, registry);
      }

      @Override public void onActivityPaused(Activity activity) {
        super.onActivityPaused(activity);
        onApplicationPaused(activity, registry);
      }
    });
  }

  protected abstract void onApplicationResumed(Activity activity, MetricRegistry registry);

  protected abstract void onApplicationPaused(Activity activity, MetricRegistry registry);
}

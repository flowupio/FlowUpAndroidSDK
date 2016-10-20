/*
 * Copyright (C) 2016 Go Karumi S.L.
 */

package io.flowup.collectors;

import android.app.Activity;
import android.app.Application;
import com.codahale.metrics.MetricRegistry;
import io.flowup.android.EmptyActivityLifecycleCallback;

abstract class ApplicationLifecycleCollector implements Collector {

  private static boolean isInForeground = false;

  private final Application application;
  private MetricRegistry registry;
  private EmptyActivityLifecycleCallback callback;
  private boolean isFirstTime = true;

  ApplicationLifecycleCollector(Application application) {
    this.application = application;
  }

  @Override public void initialize(MetricRegistry registry) {
    this.registry = registry;
    registerActivityLifecycleCallbacks();
  }

  private void registerActivityLifecycleCallbacks() {
    if (callback != null) {
      application.unregisterActivityLifecycleCallbacks(callback);
    }
    callback = new EmptyActivityLifecycleCallback() {
      @Override public void onActivityResumed(Activity activity) {
        super.onActivityResumed(activity);
        if(isInForeground || isFirstTime) {
          isFirstTime = false;
          onApplicationResumed(activity, registry);
        }
        isInForeground = true;
      }

      @Override public void onActivityPaused(Activity activity) {
        super.onActivityPaused(activity);
        isInForeground = false;
        onApplicationPaused(activity, registry);
      }
    };
    application.registerActivityLifecycleCallbacks(callback);
  }

  protected abstract void onApplicationResumed(Activity activity, MetricRegistry registry);

  protected abstract void onApplicationPaused(Activity activity, MetricRegistry registry);
}

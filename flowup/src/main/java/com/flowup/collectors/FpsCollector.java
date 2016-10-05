/*
 * Copyright (C) 2016 Go Karumi S.L.
 */

package com.flowup.collectors;

import android.app.Application;
import android.util.Log;
import android.view.Choreographer;
import com.codahale.metrics.Gauge;
import com.codahale.metrics.MetricRegistry;
import com.flowup.metricnames.MetricNamesGenerator;

class FpsCollector extends ApplicationLifecycleCollector implements Collector {

  private final MetricNamesGenerator metricNamesGenerator;
  private final Choreographer choreographer;
  private final FpsFrameCallback fpsFrameCallback;

  FpsCollector(Application application, MetricNamesGenerator metricNamesGenerator) {
    super(application);
    this.metricNamesGenerator = metricNamesGenerator;
    this.choreographer = Choreographer.getInstance();
    this.fpsFrameCallback = new FpsFrameCallback(choreographer);
  }

  @Override protected void onApplicationResumed(MetricRegistry registry) {
    initializeGauge(registry);
    choreographer.postFrameCallback(fpsFrameCallback);
  }

  @Override protected void onApplicationPaused(MetricRegistry registry) {
    choreographer.removeFrameCallback(fpsFrameCallback);
    fpsFrameCallback.reset();
    removeGauge(registry);
  }

  private void initializeGauge(MetricRegistry registry) {
    String fpsMetricName = metricNamesGenerator.getFPSMetricName();
    registry.register(fpsMetricName, new Gauge<Double>() {
      @Override public Double getValue() {
        double fps = fpsFrameCallback.getFPS();
        fpsFrameCallback.reset();
        Log.d("FlowUp", "Collecting FPS metric-> " + fps);
        return fps;
      }
    });
  }

  private void removeGauge(MetricRegistry registry) {
    registry.remove(metricNamesGenerator.getFPSMetricName());
  }
}

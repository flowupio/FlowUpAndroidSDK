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
import io.flowup.metricnames.MetricNamesGenerator;
import java.util.HashMap;
import java.util.Map;

@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN) class FpsCollector
    extends ApplicationLifecycleCollector implements Collector {

  private final MetricNamesGenerator metricNamesGenerator;
  private final Choreographer choreographer;
  private FpsFrameCallback fpsFrameCallback;
  private Map<String, Histogram> histograms;

  FpsCollector(Application application, MetricNamesGenerator metricNamesGenerator) {
    super(application);
    this.metricNamesGenerator = metricNamesGenerator;
    this.choreographer = Choreographer.getInstance();
    this.histograms = new HashMap<>();
  }

  @Override protected void onApplicationResumed(Activity activity, MetricRegistry registry) {
    Histogram histogram = getHistogram(activity, registry);
    fpsFrameCallback = new FpsFrameCallback(histogram, choreographer);
    choreographer.postFrameCallback(fpsFrameCallback);
  }

  @Override protected void onApplicationPaused(Activity activity, MetricRegistry registry) {
    choreographer.removeFrameCallback(fpsFrameCallback);
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

  private Histogram initializeHistogram(Activity activity, MetricRegistry registry) {
    String fpsMetricName = metricNamesGenerator.getFPSMetricName(activity);
    return registry.histogram(fpsMetricName);
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

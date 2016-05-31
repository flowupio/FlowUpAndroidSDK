/*
 * Copyright (C) 2015 Go Karumi S.L.
 */

package com.karumi.kiru;

import android.content.Context;
import com.codahale.metrics.Gauge;
import com.codahale.metrics.MetricFilter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.graphite.Graphite;
import com.codahale.metrics.graphite.GraphiteReporter;
import com.karumi.kiru.metricnames.MetricNamesFactory;
import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

public class Kiru {

  private final Context context;
  private static MetricRegistry registry;

  static Kiru with(Context context) {
    return new Kiru(context);
  }

  Kiru(Context context) {
    this.context = context.getApplicationContext();
  }

  void start() {
    if (kiruHasBeenInitialized()) {
      return;
    }
    initializeMetrics();
    configureFPSGauge();
  }

  private boolean kiruHasBeenInitialized() {
    return registry != null;
  }

  private void initializeMetrics() {
    Graphite graphite = new Graphite(new InetSocketAddress("carbon.hostedgraphite.com", 2003));
    registry = new MetricRegistry();
    GraphiteReporter reporter = GraphiteReporter.forRegistry(registry)
        .prefixedWith("6f9a168a-ea09-4fdd-8d11-b4c2c36f14e0")
        .convertRatesTo(TimeUnit.SECONDS)
        .convertDurationsTo(TimeUnit.MILLISECONDS)
        .filter(MetricFilter.ALL)
        .build(graphite);
    reporter.start(1, TimeUnit.MINUTES);
  }

  private void configureFPSGauge() {
    String fpsMetricName = MetricNamesFactory.getFPSMetricName();
    registry.register(fpsMetricName, new Gauge<Integer>() {
      @Override public Integer getValue() {
        return null;
      }
    });
  }
}

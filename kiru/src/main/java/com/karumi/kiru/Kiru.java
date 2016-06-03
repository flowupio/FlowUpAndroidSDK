/*
 * Copyright (C) 2016 Go Karumi S.L.
 */

package com.karumi.kiru;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;
import com.codahale.metrics.ConsoleReporter;
import com.codahale.metrics.MetricFilter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.graphite.Graphite;
import com.codahale.metrics.graphite.GraphiteReporter;
import com.karumi.kiru.collectors.Collector;
import com.karumi.kiru.collectors.Collectors;
import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

public class Kiru {

  private final Application application;
  private static MetricRegistry registry;

  public static Kiru with(Application application) {
    return new Kiru(application);
  }

  Kiru(Application application) {
    if (application == null) {
      throw new IllegalArgumentException(
          "The application instance used to initialize Kiru can not be null.");
    }
    this.application = application;
  }

  public void start() {
    if (hasBeenInitialized()) {
      return;
    }
    new Thread(new Runnable() {
      @Override public void run() {
        initializeMetrics();
        new Handler(Looper.getMainLooper()).post(new Runnable() {
          @Override public void run() {
            initializeForegroundCollectors();
            initializeHttpCollectors();
          }
        });
      }
    }).start();
  }

  private boolean hasBeenInitialized() {
    return registry != null;
  }

  private void initializeMetrics() {
    Graphite graphite = new Graphite(new InetSocketAddress("carbon.hostedgraphite.com", 2003));
    registry = new MetricRegistry();
    ConsoleReporter reporter = ConsoleReporter.forRegistry(registry).build();

    reporter.start(10, TimeUnit.SECONDS);
  }

  private void initializeForegroundCollectors() {
    initializeFPSCollector();
    initializeFrameTimeCollector();
  }

  private void initializeFPSCollector() {
    Collector fpsCollector = Collectors.getFPSCollector(application);
    fpsCollector.initialize(registry);
  }

  private void initializeFrameTimeCollector() {
    Collector frameTimeCollector = Collectors.getFrameTimeCollector(application);
    frameTimeCollector.initialize(registry);
  }

  private void initializeHttpCollectors() {
    Collector httpBytesDownloadedCollector = Collectors.getHttpBytesDownloadedCollector(application);
    httpBytesDownloadedCollector.initialize(registry);
    Collector httpBytesUploadedCollector = Collectors.getHttpBytesUploadedCollector(application);
    httpBytesUploadedCollector.initialize(registry);
  }
}

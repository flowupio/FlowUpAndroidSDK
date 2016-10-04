/*
 * Copyright (C) 2016 Go Karumi S.L.
 */

package com.flowup;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;
import com.codahale.metrics.ConsoleReporter;
import com.codahale.metrics.MetricFilter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.graphite.Graphite;
import com.codahale.metrics.graphite.GraphiteReporter;
import com.flowup.collectors.Collector;
import com.flowup.collectors.Collectors;
import com.readytalk.metrics.StatsDReporter;
import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

public class FlowUp {

  private final Application application;
  private static MetricRegistry registry;

  public static FlowUp with(Application application) {
    return new FlowUp(application);
  }

  FlowUp(Application application) {
    if (application == null) {
      throw new IllegalArgumentException(
          "The application instance used to initialize FlowUp can not be null.");
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
    registry = new MetricRegistry();
    initializeConsoleReporter();
    initializeHostedGraphiteReporter();
    initializeKarumiStatsDReporter();
  }

  private void initializeConsoleReporter() {
    ConsoleReporter.forRegistry(registry).build().start(10, TimeUnit.SECONDS);
  }

  private void initializeKarumiStatsDReporter() {
    StatsDReporter.forRegistry(registry)
        .convertRatesTo(TimeUnit.SECONDS)
        .convertDurationsTo(TimeUnit.MILLISECONDS)
        .filter(MetricFilter.ALL)
        .build("54.194.240.147", 8125)
        .start(10, TimeUnit.SECONDS);
  }

  private void initializeHostedGraphiteReporter() {
    Graphite graphite = new Graphite(new InetSocketAddress("carbon.hostedgraphite.com", 2003));
    GraphiteReporter.forRegistry(registry)
        .prefixedWith("6f9a168a-ea09-4fdd-8d11-b4c2c36f14e0")
        .convertRatesTo(TimeUnit.SECONDS)
        .convertDurationsTo(TimeUnit.MILLISECONDS)
        .filter(MetricFilter.ALL)
        .build(graphite)
        .start(10, TimeUnit.SECONDS);
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
    Collector httpBytesDownloadedCollector =
        Collectors.getHttpBytesDownloadedCollector(application);
    httpBytesDownloadedCollector.initialize(registry);
    Collector httpBytesUploadedCollector = Collectors.getHttpBytesUploadedCollector(application);
    httpBytesUploadedCollector.initialize(registry);
  }
}

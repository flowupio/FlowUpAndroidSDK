/*
 * Copyright (C) 2016 Go Karumi S.L.
 */

package com.flowup;

import android.app.Application;
import com.codahale.metrics.ConsoleReporter;
import com.codahale.metrics.MetricFilter;
import com.codahale.metrics.MetricRegistry;
import com.flowup.collectors.Collector;
import com.flowup.collectors.Collectors;
import com.flowup.reporter.FlowUpReporter;
import com.readytalk.metrics.StatsDReporter;
import java.util.concurrent.TimeUnit;

public class FlowUp {

  public static final int SAMPLING_INTERVAL = 10;
  private static final TimeUnit SAMPLING_TIME_UNIT = TimeUnit.SECONDS;

  private final Application application;
  private final boolean debuggable;

  private static MetricRegistry registry;

  FlowUp(Application application, boolean debuggable) {
    if (application == null) {
      throw new IllegalArgumentException(
          "The application instance used to initialize FlowUp can not be null.");
    }
    this.application = application;
    this.debuggable = debuggable;
  }

  public void start() {
    if (hasBeenInitialized()) {
      return;
    }
    initializeMetrics();
    initializeReporters();
    initializeForegroundCollectors();
    initializeNetworkCollectors();
  }

  private void initializeMetrics() {
    registry = new MetricRegistry();
  }

  private boolean hasBeenInitialized() {
    return registry != null;
  }

  private void initializeReporters() {
    //initializeConsoleReporter();
    initializeKarumiGrafanaReporter();
    initializeFlowUpReporter();
  }

  private void initializeConsoleReporter() {
    ConsoleReporter.forRegistry(registry).build().start(10, TimeUnit.SECONDS);
  }

  private void initializeKarumiGrafanaReporter() {
    String host = application.getString(R.string.karumi_grafana_host);
    int port = application.getResources().getInteger(R.integer.karumi_grafana_port);
    StatsDReporter.forRegistry(registry)
        .convertRatesTo(TimeUnit.MILLISECONDS)
        .convertDurationsTo(TimeUnit.MILLISECONDS)
        .filter(MetricFilter.ALL)
        .build(host, port)
        .start(SAMPLING_INTERVAL, SAMPLING_TIME_UNIT);
  }

  private void initializeFlowUpReporter() {
    String scheme = application.getString(R.string.flowup_scheme);
    String host = application.getString(R.string.flowup_host);
    int port = application.getResources().getInteger(R.integer.flowup_port);
    FlowUpReporter.forRegistry(registry, application)
        .rateUnit(TimeUnit.SECONDS)
        .durationUnit(TimeUnit.MILLISECONDS)
        .filter(MetricFilter.ALL)
        .debuggable(debuggable)
        .build(scheme, host, port)
        .start(SAMPLING_INTERVAL, TimeUnit.SECONDS);
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

  private void initializeNetworkCollectors() {
    Collector bytesDownloadedCollector =
        Collectors.getBytesDownloadedCollector(application, SAMPLING_INTERVAL, SAMPLING_TIME_UNIT);
    bytesDownloadedCollector.initialize(registry);

    Collector bytesUploadedCollector =
        Collectors.getBytesUploadedCollector(application, SAMPLING_INTERVAL, SAMPLING_TIME_UNIT);
    bytesUploadedCollector.initialize(registry);
  }

  public static class Builder {

    Application application;
    boolean debuggable;

    Builder() {
    }

    public static Builder with(Application application) {
      Builder builder = new Builder();
      builder.application = application;
      return builder;
    }

    public Builder debuggable(boolean debuggable) {
      this.debuggable = debuggable;
      return this;
    }

    public void start() {
      new FlowUp(application, debuggable).start();
    }
  }
}

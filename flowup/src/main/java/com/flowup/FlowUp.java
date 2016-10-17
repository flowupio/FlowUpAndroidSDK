/*
 * Copyright (C) 2016 Go Karumi S.L.
 */

package com.flowup;

import android.app.Application;
import android.util.Log;
import com.codahale.metrics.MetricFilter;
import com.codahale.metrics.MetricRegistry;
import com.flowup.android.App;
import com.flowup.android.CPU;
import com.flowup.android.FileSystem;
import com.flowup.collectors.Collector;
import com.flowup.collectors.Collectors;
import com.flowup.reporter.FlowUpReporter;
import com.flowup.unix.Terminal;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.readytalk.metrics.StatsDReporter;
import java.util.concurrent.TimeUnit;

public class FlowUp {

  public static final int SAMPLING_INTERVAL = 10;
  private static final TimeUnit SAMPLING_TIME_UNIT = TimeUnit.SECONDS;
  private static final String LOGTAG = "FlowUp";

  private final Application application;
  private final boolean forceReports;

  private static MetricRegistry registry;

  FlowUp(Application application, boolean forceReports) {
    if (application == null) {
      throw new IllegalArgumentException(
          "The application instance used to initialize FlowUp can not be null.");
    }
    this.application = application;
    this.forceReports = forceReports;
  }

  public void start() {
    if (hasBeenInitialized() || !doesSupportGooglePlayServices()) {
      return;
    }
    initializeMetrics();
    initializeReporters();
    initializeForegroundCollectors();
    initializeNetworkCollectors();
    initializeCPUCollectors();
    initializeMemoryCollectors();
    initializeDiskCollectors();
  }

  private void initializeMetrics() {
    registry = new MetricRegistry();
  }

  private boolean hasBeenInitialized() {
    return registry != null;
  }

  private boolean doesSupportGooglePlayServices() {
    if (forceReports) {
      return true;
    }
    GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
    int resultCode = googleApiAvailability.isGooglePlayServicesAvailable(application);
    boolean isGooglePlayServicesSupported = resultCode == ConnectionResult.SUCCESS;
    Log.d(LOGTAG, "Google play services supported = " + isGooglePlayServicesSupported);
    return isGooglePlayServicesSupported;
  }

  private void initializeReporters() {
    initializeKarumiGrafanaReporter();
    initializeFlowUpReporter();
  }

  private void initializeKarumiGrafanaReporter() {
    new Thread(new Runnable() {
      @Override public void run() {
        String host = application.getString(R.string.karumi_graphite_server);
        int port = application.getResources().getInteger(R.integer.karumi_graphite_port);
        StatsDReporter.forRegistry(registry)
            .convertRatesTo(TimeUnit.NANOSECONDS)
            .convertDurationsTo(TimeUnit.NANOSECONDS)
            .filter(MetricFilter.ALL)
            .build(host, port)
            .start(SAMPLING_INTERVAL, SAMPLING_TIME_UNIT);
      }
    }).start();
  }

  private void initializeFlowUpReporter() {
    String scheme = application.getString(R.string.flowup_scheme);
    String host = application.getString(R.string.flowup_host);
    int port = application.getResources().getInteger(R.integer.flowup_port);
    FlowUpReporter.forRegistry(registry, application)
        .filter(MetricFilter.ALL)
        .forceReports(forceReports)
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

  private void initializeCPUCollectors() {
    Collector cpuUsageCollector =
        Collectors.getCPUUsageCollector(application, SAMPLING_INTERVAL, SAMPLING_TIME_UNIT,
            new CPU(new App(application), new Terminal()));
    cpuUsageCollector.initialize(registry);
  }

  private void initializeMemoryCollectors() {
    Collector memoryUsageCollector =
        Collectors.getMemoryUsageCollector(application, SAMPLING_INTERVAL, SAMPLING_TIME_UNIT,
            new App(application));
    memoryUsageCollector.initialize(registry);
  }

  private void initializeDiskCollectors() {
    Collector diskUsageCollector =
        Collectors.getDiskUsageCollector(application, SAMPLING_INTERVAL, SAMPLING_TIME_UNIT,
            new FileSystem(application));
    diskUsageCollector.initialize(registry);
  }

  public static class Builder {

    Application application;
    boolean forceReports;
    boolean logEnabled;

    Builder() {
    }

    public static Builder with(Application application) {
      Builder builder = new Builder();
      builder.application = application;
      return builder;
    }

    public Builder forceReports(boolean forceReports) {
      this.forceReports = forceReports;
      return this;
    }

    public Builder logEnabled(boolean logEnabled) {
      this.logEnabled = logEnabled;
      return this;
    }

    public void start() {
      new FlowUp(application, forceReports).start();
    }
  }
}

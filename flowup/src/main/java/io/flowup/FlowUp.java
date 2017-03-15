/*
 * Copyright (C) 2016 Go Karumi S.L.
 */

package io.flowup;

import android.app.Application;
import android.os.Build;
import android.support.annotation.RequiresApi;
import com.codahale.metrics.Metric;
import com.codahale.metrics.MetricFilter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import io.flowup.android.App;
import io.flowup.android.BuildConfigExtractor;
import io.flowup.android.CPU;
import io.flowup.android.Device;
import io.flowup.android.FileSystem;
import io.flowup.android.UIStateWatcher;
import io.flowup.collectors.Collector;
import io.flowup.collectors.Collectors;
import io.flowup.collectors.UpdatableCollector;
import io.flowup.config.FlowUpConfig;
import io.flowup.config.android.ConfigSyncServiceScheduler;
import io.flowup.config.apiclient.ConfigApiClient;
import io.flowup.config.storage.ConfigStorage;
import io.flowup.crashreporter.SafetyNet;
import io.flowup.logger.Logger;
import io.flowup.metricnames.MetricNamesExtractor;
import io.flowup.reporter.DropwizardReport;
import io.flowup.reporter.FlowUpReporter;
import io.flowup.reporter.FlowUpReporterListener;
import io.flowup.storage.SQLDelightfulOpenHelper;
import io.flowup.unix.Terminal;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public final class FlowUp {

  public static final int SAMPLING_INTERVAL = 10;
  private static final TimeUnit SAMPLING_TIME_UNIT = TimeUnit.SECONDS;
  private static final Object INITIALIZATION_LOCK = new Object();

  private final Application application;
  private final String apiKey;
  private final boolean forceReports;

  private static MetricRegistry registry;
  private static FlowUpReporter reporter;
  private FlowUpConfig flowUpConfig;

  FlowUp(Application application, String apiKey, boolean forceReports) {
    validateConstructionParams(application, apiKey);
    this.application = application;
    this.apiKey = apiKey;
    this.forceReports = forceReports;
  }

  void start() {
    synchronized (INITIALIZATION_LOCK) {
      SafetyNet safetyNet = new SafetyNet();
      safetyNet.executeSafetyOnNewThread(new Runnable() {
        @Override public void run() {
          initializeFlowUp();
        }
      });
    }
  }

  private void initializeFlowUp() {
    if (hasBeenInitialized()) {
      return;
    }
    if (!doesSupportGooglePlayServices()) {
      Logger.e(
          "FlowUp hasn't been initialized. Google play services is not supported in this device");
      return;
    }
    initializeConfigScheduler();
    if (!isFlowUpEnabled()) {
      Logger.d("FlowUp is disabled for this device");
      return;
    }
    initializeMetrics();
    initializeForegroundCollectors();
    initializeNetworkCollectors();
    initializeCPUCollectors();
    initializeMemoryCollectors();
    initializeDiskCollectors();
    initializeUIStateWatcher();
    initializeFlowUpReporter();
    Logger.d("FlowUp initialized");
  }

  private void validateConstructionParams(Application application, String apiKey) {
    if (application == null) {
      throw new IllegalArgumentException(
          "The application instance used to initialize FlowUp can not be null.");
    }
    if (apiKey == null || apiKey.isEmpty()) {
      throw new IllegalArgumentException(
          "The apiKey instance used to initialize FlowUp can not be null or empty.");
    }
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
    return isGooglePlayServicesSupported;
  }

  private boolean isFlowUpEnabled() {
    String scheme = application.getString(R.string.flowup_scheme);
    String host = application.getString(R.string.flowup_host);
    int port = application.getResources().getInteger(R.integer.flowup_port);
    Device device = new Device(application);
    SQLDelightfulOpenHelper dbOpenHelper = SQLDelightfulOpenHelper.getInstance(application);
    flowUpConfig = new FlowUpConfig(new ConfigStorage(dbOpenHelper),
        new ConfigApiClient(apiKey, device, scheme, host, port, forceReports));
    return flowUpConfig.getConfig().isEnabled();
  }

  private void initializeFlowUpReporter() {
    String scheme = application.getString(R.string.flowup_scheme);
    String host = application.getString(R.string.flowup_host);
    int port = application.getResources().getInteger(R.integer.flowup_port);
    reporter = FlowUpReporter.forRegistry(registry, application)
        .filter(MetricFilter.ALL)
        .forceReports(forceReports)
        .listener(new FlowUpReporterListener() {
          @Override public void onReport(DropwizardReport report) {
            removeActivityTimers(report);
            restartUpdatableCollectors();
          }

          @Override public void onFlowUpDisabled() {
            registry.removeMatching(new MetricFilter() {
              @Override public boolean matches(String name, Metric metric) {
                return true;
              }
            });
            reporter.stop();
            flowUpConfig.disableClient();
          }
        })
        .build(apiKey, scheme, host, port);
    reporter.start(SAMPLING_INTERVAL, TimeUnit.SECONDS);
  }

  private void removeActivityTimers(DropwizardReport report) {
    MetricNamesExtractor extractor = new MetricNamesExtractor();
    List<String> metricsToRemoveAfterReport = new LinkedList<>();
    metricsToRemoveAfterReport.addAll(report.getTimers().keySet());
    metricsToRemoveAfterReport.addAll(report.getHistograms().keySet());
    for (String metricName : metricsToRemoveAfterReport) {
      if (extractor.isUIMetric(metricName)) {
        if (extractor.isActivityVisibleMetric(metricName)) {
          Timer timer = registry.getTimers().get(metricName);
          if (timer.getCount() > 0) {
            registry.remove(metricName);
          }
        } else {
          registry.remove(metricName);
        }
      }
    }
  }

  private void restartUpdatableCollectors() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
      restartFrameTimeCollector();
    }
  }

  private void initializeForegroundCollectors() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
      initializeFrameTimeCollector();
      initializeActivityLifecycleCollector();
      initializeActivityVisibleCollector();
    }
  }

  @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN) private void initializeFrameTimeCollector() {
    Collector frameTimeCollector = Collectors.getFrameTimeCollector(application);
    frameTimeCollector.initialize(registry);
  }

  @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN) private void restartFrameTimeCollector() {
    UpdatableCollector frameTimeCollector = Collectors.getFrameTimeCollector(application);
    frameTimeCollector.forceUpdate(registry);
  }

  @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
  private void initializeActivityLifecycleCollector() {
    Collector activityLifecycleCollector = Collectors.getActivityLifecycleCollector(application);
    activityLifecycleCollector.initialize(registry);
  }

  @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
  private void initializeActivityVisibleCollector() {
    Collector activityLifecycleCollector = Collectors.getActivityVisibleCollector(application);
    activityLifecycleCollector.initialize(registry);
  }

  private void initializeConfigScheduler() {
    ConfigSyncServiceScheduler scheduler =
        new ConfigSyncServiceScheduler(application, apiKey, forceReports);
    scheduler.scheduleSyncTask();
  }

  private void initializeNetworkCollectors() {
    Collector bytesUploadedCollector =
        Collectors.getNetworkUsageCollector(application, SAMPLING_INTERVAL, SAMPLING_TIME_UNIT);
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

  private void initializeUIStateWatcher() {
    App app = new App(application);
    UIStateWatcher callback = new UIStateWatcher(app);
    application.registerActivityLifecycleCallbacks(callback);
  }

  public static final class Builder {

    Application application;
    String apiKey;
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

    public Builder apiKey(String apiKey) {
      this.apiKey = apiKey;
      return this;
    }

    public Builder logEnabled(boolean logEnabled) {
      this.logEnabled = logEnabled;
      return this;
    }

    private void initializeLogger() {
      Logger.setEnabled(logEnabled);
    }

    public void start() {
      initializeLogger();
      try {
        BuildConfigExtractor buildConfigExtractor = new BuildConfigExtractor();
        forceReports = forceReports && buildConfigExtractor.isApplicationDebuggable(application);
        Logger.d("Starting FlowUp Android SDK. Force reports config = " + forceReports);
      } catch (Exception e) {
        Logger.d("Exception catch trying to know if the app is in debug mode or not");
      }
      new FlowUp(application, apiKey, forceReports).start();
    }
  }
}

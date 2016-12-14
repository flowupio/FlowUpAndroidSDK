/*
 * Copyright (C) 2016 Go Karumi S.L.
 */

package io.flowup.collectors;

import com.codahale.metrics.CachedGauge;
import com.codahale.metrics.MetricRegistry;
import io.flowup.android.App;
import io.flowup.android.FileSystem;
import io.flowup.metricnames.MetricNamesGenerator;
import java.util.concurrent.TimeUnit;

class DiskUsageCollector implements Collector {

  private final MetricNamesGenerator metricNamesGenerator;
  private final long samplingInterval;
  private final TimeUnit timeUnit;
  private final FileSystem fileSystem;
  private final App app;

  DiskUsageCollector(MetricNamesGenerator metricNamesGenerator, long samplingInterval,
      TimeUnit timeUnit, FileSystem fileSystem, App app) {
    this.metricNamesGenerator = metricNamesGenerator;
    this.samplingInterval = samplingInterval;
    this.timeUnit = timeUnit;
    this.fileSystem = fileSystem;
    this.app = app;
  }

  @Override public void initialize(MetricRegistry registry) {
    registerCachedGaugerForTheInternalStorage(registry, false);
    registerCachedGaugeForSharedPreferences(registry, false);
    registerCachedGaugerForTheInternalStorage(registry, true);
    registerCachedGaugeForSharedPreferences(registry, true);
  }

  private void registerCachedGaugerForTheInternalStorage(MetricRegistry registry,
      final boolean isInBackground) {
    registry.register(metricNamesGenerator.getInternalStorageWrittenBytes(isInBackground),
        new CachedGauge<Long>(samplingInterval, timeUnit) {
          @Override protected Long loadValue() {
            if ((isInBackground && app.isApplicaitonInForeground()) || (!isInBackground
                && app.isApplicationInBackground())) {
              return null;
            }
            return fileSystem.getInternalStorageWrittenBytes();
          }
        });
  }

  private void registerCachedGaugeForSharedPreferences(MetricRegistry registry,
      final boolean isInBackground) {
    registry.register(metricNamesGenerator.getSharedPreferencesWrittenBytes(isInBackground),
        new CachedGauge<Long>(samplingInterval, timeUnit) {
          @Override protected Long loadValue() {
            if ((isInBackground && app.isApplicaitonInForeground()) || (!isInBackground
                && app.isApplicationInBackground())) {
              return null;
            }
            return fileSystem.getSharedPreferencesWrittenBytes();
          }
        });
  }
}

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
    registry.register(metricNamesGenerator.getInternalStorageWrittenBytes(false),
        new CachedGauge<Long>(samplingInterval, timeUnit) {
          @Override protected Long loadValue() {
            if (app.isApplicationInBackground()) {
              return null;
            }
            return fileSystem.getInternalStorageWrittenBytes();
          }
        });

    registry.register(metricNamesGenerator.getSharedPreferencesWrittenBytes(false),
        new CachedGauge<Long>(samplingInterval, timeUnit) {
          @Override protected Long loadValue() {
            if (app.isApplicationInBackground()) {
              return null;
            }
            return fileSystem.getSharedPreferencesWrittenBytes();
          }
        });

    registry.register(metricNamesGenerator.getInternalStorageWrittenBytes(true),
        new CachedGauge<Long>(samplingInterval, timeUnit) {
          @Override protected Long loadValue() {
            if (app.isApplicaitonInForeground()) {
              return null;
            }
            return fileSystem.getInternalStorageWrittenBytes();
          }
        });

    registry.register(metricNamesGenerator.getSharedPreferencesWrittenBytes(true),
        new CachedGauge<Long>(samplingInterval, timeUnit) {
          @Override protected Long loadValue() {
            if (app.isApplicaitonInForeground()) {
              return null;
            }
            return fileSystem.getSharedPreferencesWrittenBytes();
          }
        });
  }
}

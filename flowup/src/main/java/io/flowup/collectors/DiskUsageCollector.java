/*
 * Copyright (C) 2016 Go Karumi S.L.
 */

package io.flowup.collectors;

import com.codahale.metrics.CachedGauge;
import com.codahale.metrics.MetricRegistry;
import io.flowup.android.FileSystem;
import io.flowup.metricnames.MetricNamesGenerator;
import java.util.concurrent.TimeUnit;

class DiskUsageCollector implements Collector {

  private final MetricNamesGenerator metricNamesGenerator;
  private final long samplingInterval;
  private final TimeUnit timeUnit;
  private final FileSystem fileSystem;

  DiskUsageCollector(MetricNamesGenerator metricNamesGenerator, long samplingInterval,
      TimeUnit timeUnit, FileSystem fileSystem) {
    this.metricNamesGenerator = metricNamesGenerator;
    this.samplingInterval = samplingInterval;
    this.timeUnit = timeUnit;
    this.fileSystem = fileSystem;
  }

  @Override public void initialize(MetricRegistry registry) {
    registry.register(metricNamesGenerator.getInternalStorageWrittenBytes(),
        new CachedGauge<Long>(samplingInterval, timeUnit) {
          @Override protected Long loadValue() {
            return fileSystem.getInternalStorageWrittenBytes();
          }
        });

    registry.register(metricNamesGenerator.getSharedPreferencesWrittenBytes(),
        new CachedGauge<Long>(samplingInterval, timeUnit) {
          @Override protected Long loadValue() {
            return fileSystem.getSharedPreferencesWrittenBytes();
          }
        });
  }
}

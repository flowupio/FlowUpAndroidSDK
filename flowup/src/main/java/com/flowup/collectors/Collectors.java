/*
 * Copyright (C) 2016 Go Karumi S.L.
 */

package com.flowup.collectors;

import android.app.Application;
import com.flowup.android.App;
import com.flowup.android.CPU;
import com.flowup.android.Device;
import com.flowup.metricnames.MetricNamesGenerator;
import com.flowup.utils.Time;
import java.util.concurrent.TimeUnit;

public class Collectors {

  public static Collector getFPSCollector(Application application) {
    return new FpsCollector(application, getMetricNamesGenerator(application));
  }

  public static Collector getFrameTimeCollector(Application application) {
    return new FrameTimeCollector(application, getMetricNamesGenerator(application));
  }

  public static Collector getBytesDownloadedCollector(Application application,
      long samplingInterval, TimeUnit timeUnit) {
    return new BytesDownloadedCollector(getMetricNamesGenerator(application), samplingInterval,
        timeUnit);
  }

  public static Collector getBytesUploadedCollector(Application application, long samplingInterval,
      TimeUnit timeUnit) {
    return new BytesUploadedCollector(getMetricNamesGenerator(application), samplingInterval,
        timeUnit);
  }

  public static Collector getCPUUsageCollector(Application application, int samplingInterval,
      TimeUnit samplingTimeUnit, CPU cpu) {
    return new CPUUsageCollector(getMetricNamesGenerator(application), samplingInterval,
        samplingTimeUnit, cpu);
  }

  public static Collector getMemoryUsageCollector(Application application, int samplingInterval,
      TimeUnit samplingTimeUnit, Device device) {
    return new MemoryUsageCollector(getMetricNamesGenerator(application), samplingInterval,
        samplingTimeUnit, device);
  }

  private static MetricNamesGenerator getMetricNamesGenerator(Application application) {
    return new MetricNamesGenerator(new App(application), new Device(application), new Time());
  }
}

/*
 * Copyright (C) 2016 Go Karumi S.L.
 */

package io.flowup.collectors;

import android.app.Application;
import android.os.Build;
import android.support.annotation.RequiresApi;
import io.flowup.android.App;
import io.flowup.android.CPU;
import io.flowup.android.Device;
import io.flowup.android.FileSystem;
import io.flowup.metricnames.MetricNamesGenerator;
import io.flowup.utils.Time;
import java.util.concurrent.TimeUnit;

public class Collectors {

  @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
  public static Collector getFPSCollector(Application application) {
    return new FpsCollector(application, getMetricNamesGenerator(application));
  }

  @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
  public static Collector getFrameTimeCollector(Application application) {
    return new FrameTimeCollector(application, getMetricNamesGenerator(application));
  }

  @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN) public static Collector getActivityLifecycleCollector(Application application) {
    return new ActivityLifecycleCollector(application, getMetricNamesGenerator(application));
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
      TimeUnit samplingTimeUnit, App app) {
    return new MemoryUsageCollector(getMetricNamesGenerator(application), samplingInterval,
        samplingTimeUnit, app);
  }

  public static Collector getDiskUsageCollector(Application application, int samplingInterval,
      TimeUnit samplingTimeUnit, FileSystem fileSystem) {
    return new DiskUsageCollector(getMetricNamesGenerator(application), samplingInterval,
        samplingTimeUnit, fileSystem);
  }

  private static MetricNamesGenerator getMetricNamesGenerator(Application application) {
    return new MetricNamesGenerator(new App(application), new Device(application), new Time());
  }
}

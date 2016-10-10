/*
 * Copyright (C) 2016 Go Karumi S.L.
 */

package com.flowup.collectors;

import android.app.Application;
import com.flowup.metricnames.App;
import com.flowup.metricnames.Device;
import com.flowup.metricnames.MetricNamesGenerator;
import com.flowup.utils.Time;
import java.util.concurrent.TimeUnit;

public class Collectors {

  public static Collector getFPSCollector(Application application) {
    return new FpsCollector(application,
        new MetricNamesGenerator(new App(application), new Device(application), new Time()));
  }

  public static Collector getFrameTimeCollector(Application application) {
    return new FrameTimeCollector(application,
        new MetricNamesGenerator(new App(application), new Device(application), new Time()));
  }

  public static Collector getBytesDownloadedCollector(Application application,
      long samplingInterval, TimeUnit timeUnit) {
    return new BytesDownloadedCollector(
        new MetricNamesGenerator(new App(application), new Device(application), new Time()),
        samplingInterval, timeUnit);
  }

  public static Collector getBytesUploadedCollector(Application application, long samplingInterval,
      TimeUnit timeUnit) {
    return new BytesUploadedCollector(
        new MetricNamesGenerator(new App(application), new Device(application), new Time()),
        samplingInterval, timeUnit);
  }
}

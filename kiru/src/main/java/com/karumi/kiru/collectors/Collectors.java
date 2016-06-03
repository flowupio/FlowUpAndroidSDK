/*
 * Copyright (C) 2016 Go Karumi S.L.
 */

package com.karumi.kiru.collectors;

import android.app.Application;
import com.karumi.kiru.metricnames.MetricNamesGenerator;

public class Collectors {

  public static Collector getFPSCollector(Application application) {
    return new FpsCollector(application, new MetricNamesGenerator(application));
  }

  public static Collector getFrameTimeCollector(Application application) {
    return new FrameTimeCollector(application, new MetricNamesGenerator(application));
  }

  public static Collector getHttpBytesDownloadedCollector(Application application) {
    return new HttpBytesDownloadedCollector(new MetricNamesGenerator(application));
  }
}

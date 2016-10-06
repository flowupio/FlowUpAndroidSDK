/*
 * Copyright (C) 2016 Go Karumi S.L.
 */

package com.flowup.collectors;

import android.app.Application;
import com.flowup.metricnames.MetricNamesGenerator;
import com.flowup.utils.Time;

public class Collectors {

  public static Collector getFPSCollector(Application application) {
    return new FpsCollector(application, new MetricNamesGenerator(application, new Time()));
  }

  public static Collector getFrameTimeCollector(Application application) {
    return new FrameTimeCollector(application, new MetricNamesGenerator(application, new Time()));
  }

  public static Collector getHttpBytesDownloadedCollector(Application application) {
    return new HttpBytesDownloadedCollector(new MetricNamesGenerator(application, new Time()));
  }

  public static Collector getHttpBytesUploadedCollector(Application application) {
    return new HttpBytesUploadedCollector(new MetricNamesGenerator(application, new Time()));
  }
}

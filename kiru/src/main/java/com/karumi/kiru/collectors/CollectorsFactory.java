/*
 * Copyright (C) 2016 Go Karumi S.L.
 */

package com.karumi.kiru.collectors;

import android.app.Application;
import com.karumi.kiru.metricnames.MetricNamesGenerator;

public class CollectorsFactory {

  public static Collector getFPSCollector(Application application) {
    return new FpsCollector(new MetricNamesGenerator(application), application);
  }

  public static Collector getFrameTimeCollector(Application application) {
    return new FrameTimeCollector(new MetricNamesGenerator(application), application);
  }
}

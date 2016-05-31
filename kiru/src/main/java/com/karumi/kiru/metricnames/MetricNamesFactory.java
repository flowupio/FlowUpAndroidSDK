/*
 * Copyright (C) 2016 Go Karumi S.L.
 */

package com.karumi.kiru.metricnames;

import android.app.Application;

public class MetricNamesFactory {

  public static String getFPSMetricName(Application application) {
    return "fps";
  }

  public static String getFrameTimeMetricName(Application application) {
    return "frame-time";
  }
}

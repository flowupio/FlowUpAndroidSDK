/*
 * Copyright (C) 2016 Go Karumi S.L.
 */

package com.karumi.kiru.collectors;

import android.app.Application;

public class CollectorsFactory {

  public static Collector getFPSCollector(Application application) {
    return new FpsCollector(application);
  }

  public static Collector getFrameTimeCollector(Application application) {
    return new FrameTimeCollector(application);
  }
}

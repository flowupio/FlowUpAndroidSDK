/*
 * Copyright (C) 2016 Go Karumi S.L.
 */

package io.flowup.utils;

import android.net.TrafficStats;

public class TrafficStatsUtils {

  public static boolean isAPISupported(long totalBytes) {
    return totalBytes != TrafficStats.UNSUPPORTED;
  }
}

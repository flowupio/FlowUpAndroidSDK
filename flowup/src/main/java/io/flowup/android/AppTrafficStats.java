/*
 * Copyright (C) 2016 Go Karumi S.L.
 */

package io.flowup.android;

import android.net.TrafficStats;
import android.os.Process;

public class AppTrafficStats {

  private final int applicationUid;

  public AppTrafficStats() {
    this.applicationUid = Process.myUid();
  }

  public long getTxBytes() {
    return TrafficStats.getUidTxBytes(applicationUid);
  }

  public long getRxBytes() {
    return TrafficStats.getUidRxBytes(applicationUid);
  }
}

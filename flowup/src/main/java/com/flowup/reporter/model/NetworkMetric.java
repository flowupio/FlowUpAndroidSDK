/*
 * Copyright (C) 2016 Go Karumi S.L.
 */

package com.flowup.reporter.model;

import com.google.gson.annotations.SerializedName;

public class NetworkMetric extends MetricReport {

  @SerializedName("bytesUploaded") private final long bytesUploaded;
  @SerializedName("bytesDownloaded") private final long bytesDownloaded;

  public NetworkMetric(long timestamp, String appVersionName, String osVersion,
      boolean batterySaverOn, long bytesUploaded, long bytesDownloaded) {
    super(timestamp, appVersionName, osVersion, batterySaverOn);
    this.bytesUploaded = bytesUploaded;
    this.bytesDownloaded = bytesDownloaded;
  }

  public long getBytesUploaded() {
    return bytesUploaded;
  }

  public long getBytesDownloaded() {
    return bytesDownloaded;
  }
}

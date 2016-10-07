/*
 * Copyright (C) 2016 Go Karumi S.L.
 */

package com.flowup.reporter.model;

public class NetworkMetricReportReport extends MetricReport {

  private final long bytesUploaded;
  private final long bytesDownloaded;

  public NetworkMetricReportReport(long timestamp, String appVersionName, String osVersion,
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

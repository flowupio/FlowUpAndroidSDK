/*
 * Copyright (C) 2016 Go Karumi S.L.
 */

package io.flowup.reporter.model;

import com.google.gson.annotations.SerializedName;

public class Metric {

  @SerializedName("timestamp") private final long timestamp;
  @SerializedName("appVersionName") private final String appVersionName;
  @SerializedName("androidOSVersion") private final String osVersion;
  @SerializedName("batterySaverOn") private final boolean batterySaverOn;
  @SerializedName("isInBackground") private final boolean isInBackground;

  public Metric(long timestamp, String appVersionName, String osVersion, boolean batterySaverOn,
      boolean isInBackground) {
    this.timestamp = timestamp;
    this.appVersionName = appVersionName;
    this.osVersion = osVersion;
    this.batterySaverOn = batterySaverOn;
    this.isInBackground = isInBackground;
  }

  public long getTimestamp() {
    return timestamp;
  }

  public String getAppVersionName() {
    return appVersionName;
  }

  public String getOsVersion() {
    return osVersion;
  }

  public boolean isBatterySaverOn() {
    return batterySaverOn;
  }
}

/*
 * Copyright (C) 2016 Go Karumi S.L.
 */

package io.flowup.crashreporter.apiclient;

import com.google.gson.annotations.SerializedName;

class ErrorReport {

  @SerializedName("deviceModel") private final String deviceModel;
  @SerializedName("osVersion") private final String osVersion;
  @SerializedName("batterySaverOn") private final boolean batterySaverOn;
  @SerializedName("message") private final String message;
  @SerializedName("stackTrace") private final String stackTrace;

  public ErrorReport(String deviceModel, String osVersion, boolean batterySaverOn, String message,
      String stackTrace) {
    this.deviceModel = deviceModel;
    this.osVersion = osVersion;
    this.batterySaverOn = batterySaverOn;
    this.message = message;
    this.stackTrace = stackTrace;
  }
}

/*
 * Copyright (C) 2016 Go Karumi S.L.
 */

package com.flowup.reporter.apiclient;

import com.google.gson.annotations.SerializedName;
import java.util.List;

class MetricsDTO {

  @SerializedName("appPackage") private final String appPackage;
  @SerializedName("appVersion") private final String appVersion;
  @SerializedName("osVersion") private final String osVersion;
  @SerializedName("uuid") private final String uuid;
  @SerializedName("deviceModel") private final String deviceModel;
  @SerializedName("screenDensity") private final String screenDensity;
  @SerializedName("isPowerSaverEnabled") private final boolean powerSavedEnabled;
  @SerializedName("screenSize") private final String screenSize;
  @SerializedName("numberOfCores") private final int numberOfCores;

  @SerializedName("network") private final List<NetworkMetricDTO> networkMetrics;
  @SerializedName("ui") private final List<UIMetricDTO> uiMetrics;

  public MetricsDTO(String appPackage, String appVersion, String osVersion, String uuid, String deviceModel,
      boolean powerSavedEnabled, String screenDensity, String screenSize, int numberOfCores,
      List<NetworkMetricDTO> networkMetrics, List<UIMetricDTO> uiMetrics) {
    this.appPackage = appPackage;
    this.appVersion = appVersion;
    this.osVersion = osVersion;
    this.uuid = uuid;
    this.deviceModel = deviceModel;
    this.powerSavedEnabled = powerSavedEnabled;
    this.screenDensity = screenDensity;
    this.screenSize = screenSize;
    this.numberOfCores = numberOfCores;
    this.networkMetrics = networkMetrics;
    this.uiMetrics = uiMetrics;
  }
}

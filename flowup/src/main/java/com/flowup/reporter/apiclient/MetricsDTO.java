/*
 * Copyright (C) 2016 Go Karumi S.L.
 */

package com.flowup.reporter.apiclient;

import com.google.gson.annotations.SerializedName;
import java.util.List;

class MetricsDTO {

  @SerializedName("appPackage")
  private String appPackage;
  @SerializedName("deviceModel")
  private String deviceModel;
  @SerializedName("screenDensity")
  private String screenDensity;
  @SerializedName("screenSize")
  private String installationUUID;
  @SerializedName("numberOfCores")
  private String numberOfCores;

  @SerializedName("network")
  private List<NetworkMetricDTO> networkMetrics;
  @SerializedName("ui")
  private List<UIMetricDTO> uiMetrics;


}

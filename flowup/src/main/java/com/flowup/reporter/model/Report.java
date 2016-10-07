/*
 * Copyright (C) 2016 Go Karumi S.L.
 */

package com.flowup.reporter.model;

import com.google.gson.annotations.SerializedName;

public class Report {

  @SerializedName("appPackage") private final String appPackage;
  @SerializedName("uuid") private final String uuid;
  @SerializedName("deviceModel") private final String deviceModel;
  @SerializedName("screenDensity") private final String screenDensity;
  @SerializedName("screenSize") private final String screenSize;
  @SerializedName("numberOfCores") private final int numberOfCores;
  @SerializedName("network") private final NetworkMetric networkMetrics;
  @SerializedName("ui") private final UIMetric uiMetrics;

  public Report(String appPackage, String uuid, String deviceModel, String screenDensity,
      String screenSize, int numberOfCores, NetworkMetric networkMetrics, UIMetric uiMetrics) {
    this.appPackage = appPackage;
    this.uuid = uuid;
    this.deviceModel = deviceModel;
    this.screenDensity = screenDensity;
    this.screenSize = screenSize;
    this.numberOfCores = numberOfCores;
    this.networkMetrics = networkMetrics;
    this.uiMetrics = uiMetrics;
  }
}

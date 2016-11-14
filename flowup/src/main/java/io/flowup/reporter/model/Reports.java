/*
 * Copyright (C) 2016 Go Karumi S.L.
 */

package io.flowup.reporter.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class Reports {

  private final transient List<String> reportsIds;
  @SerializedName("appPackage") private final String appPackage;
  @SerializedName("installationUUID") private final String uuid;
  @SerializedName("deviceModel") private final String deviceModel;
  @SerializedName("screenDensity") private final String screenDensity;
  @SerializedName("screenSize") private final String screenSize;
  @SerializedName("numberOfCores") private final Integer numberOfCores;
  @SerializedName("network") private final List<NetworkMetric> networkMetricsReports;
  @SerializedName("ui") private final List<UIMetric> uiMetrics;
  @SerializedName("cpu") private final List<CPUMetric> cpuMetrics;
  @SerializedName("memory") private final List<MemoryMetric> memoryMetrics;
  @SerializedName("disk") private final List<DiskMetric> diskMetrics;

  public Reports(List<String> reportsIds) {
    this(reportsIds, null, null, null, null, null, null, null, null, null, null, null);
  }

  public Reports(List<String> reportsIds, String appPackage, String uuid, String deviceModel,
      String screenDensity, String screenSize, Integer numberOfCores,
      List<NetworkMetric> networkMetricsReports, List<UIMetric> uiMetrics,
      List<CPUMetric> cpuMetrics, List<MemoryMetric> memoryMetrics, List<DiskMetric> diskMetrics) {
    this.reportsIds = reportsIds;
    this.appPackage = appPackage;
    this.uuid = uuid;
    this.deviceModel = deviceModel;
    this.screenDensity = screenDensity;
    this.screenSize = screenSize;
    this.numberOfCores = numberOfCores;
    this.networkMetricsReports = networkMetricsReports;
    this.uiMetrics = uiMetrics;
    this.cpuMetrics = cpuMetrics;
    this.memoryMetrics = memoryMetrics;
    this.diskMetrics = diskMetrics;
  }

  public int size() {
    return getReportsIds().size();
  }

  public List<String> getReportsIds() {
    return reportsIds;
  }

  public String getAppPackage() {
    return appPackage;
  }

  public String getUUID() {
    return uuid;
  }

  public String getDeviceModel() {
    return deviceModel;
  }

  public String getScreenDensity() {
    return screenDensity;
  }

  public String getScreenSize() {
    return screenSize;
  }

  public Integer getNumberOfCores() {
    return numberOfCores;
  }

  public List<NetworkMetric> getNetworkMetrics() {
    return networkMetricsReports;
  }

  public List<UIMetric> getUIMetrics() {
    return uiMetrics;
  }

  public List<CPUMetric> getCpuMetrics() {
    return cpuMetrics;
  }

  public List<MemoryMetric> getMemoryMetrics() {
    return memoryMetrics;
  }

  public List<DiskMetric> getDiskMetrics() {
    return diskMetrics;
  }

  @Override public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    Reports reports = (Reports) o;

    return reportsIds.equals(reports.reportsIds);
  }

  @Override public int hashCode() {
    return reportsIds.hashCode();
  }

  @Override public String toString() {
    return "Reports{"
        + "reportsIds="
        + reportsIds
        + ", \n"
        + "appPackage="
        + appPackage
        + ", \n"
        + "uuid="
        + uuid
        + ", \n"
        + "deviceModel="
        + deviceModel
        + ", \n"
        + "screenDensity="
        + screenDensity
        + ", \n"
        + "screenSize="
        + screenSize
        + ", \n"
        + "numberOfCores="
        + numberOfCores
        + ", \n"
        + "networkMetricsReports="
        + networkMetricsReports
        + ", \n"
        + "uiMetrics="
        + uiMetrics
        + ", \n"
        + "cpuMetrics="
        + cpuMetrics
        + ", \n"
        + "memoryMetrics="
        + memoryMetrics
        + ", \n"
        + "diskMetrics="
        + diskMetrics
        + "\n"
        + '}';
  }
}

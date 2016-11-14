/*
 * Copyright (C) 2016 Go Karumi S.L.
 */

package io.flowup.reporter;

import com.google.gson.Gson;
import io.flowup.FlowUp;
import io.flowup.reporter.model.CPUMetric;
import io.flowup.reporter.model.DiskMetric;
import io.flowup.reporter.model.MemoryMetric;
import io.flowup.reporter.model.NetworkMetric;
import io.flowup.reporter.model.Reports;
import io.flowup.reporter.model.StatisticalValue;
import io.flowup.reporter.model.UIMetric;
import java.util.LinkedList;
import java.util.List;
import org.junit.Ignore;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

public class NumberOfReportsPerBatchTest {

  private static final double MAX_REQUEST_SIZE_WITHOUT_COMPRESSION_IN_BYTES = 9.5d * 1024 * 1024;

  private static final String ANY_VERSION_NAME = "1.0.0";
  private static final String ANY_OS_VERSION = "API24";
  private static final boolean ANY_BATTERY_SAVER_ON = true;
  private static final long BYTES_UPLOADED = Long.MAX_VALUE;
  private static final long BYTES_DOWNLOADED = Long.MAX_VALUE;
  private static final int CPU_USAGE = 100;
  private static final long BYTES_ALLOCATED = Long.MAX_VALUE;
  private static final int MEMORY_USAGE = 4;
  private static final long BYTES_WRITTEN = Long.MAX_VALUE;

  private final Gson gson = new Gson();

  @Test public void doesNotSendMoreThan100KBOfBodyInAReportRequest() throws Exception {
    Reports reports = givenAReportsInstanceFullOfData(FlowUpReporter.NUMBER_OF_REPORTS_PER_REQUEST);

    long bytes = toBytes(reports);

    assertTrue(
        "Your modification to the Reports classes used to send data to our severs has increased the"
            + " request body size exceeding the maximum size supported. The new max number of reports"
            + " we can send in one request is: "
            + calculateMaxNumberOfReportsPerRequest(),
        bytes <= MAX_REQUEST_SIZE_WITHOUT_COMPRESSION_IN_BYTES);
  }

  @Test @Ignore public void doesNotSendLessThanTheOptimumNumberOfReportsPerRequest()
      throws Exception {
    int optimumNumberOfReports = calculateOptimumNumberOfReportsPerRequest();

    assertEquals(optimumNumberOfReports, FlowUpReporter.NUMBER_OF_REPORTS_PER_REQUEST);
  }

  private int calculateMaxNumberOfReportsPerRequest() throws Exception {
    int newMax;
    for (newMax = FlowUpReporter.NUMBER_OF_REPORTS_PER_REQUEST; newMax >= 0; newMax--) {
      System.out.println("Trying with a new max value " + newMax);
      Reports reports = givenAReportsInstanceFullOfData(newMax);
      long bytes = toBytes(reports);
      if (bytes <= MAX_REQUEST_SIZE_WITHOUT_COMPRESSION_IN_BYTES) {
        break;
      }
    }
    return newMax;
  }

  private int calculateOptimumNumberOfReportsPerRequest() throws Exception {
    int optimumNumberOfReports = 0;
    long bytes = 0;
    while (bytes < MAX_REQUEST_SIZE_WITHOUT_COMPRESSION_IN_BYTES) {
      System.out.println("Trying with " + optimumNumberOfReports);
      Reports reports = givenAReportsInstanceFullOfData(optimumNumberOfReports);
      bytes = toBytes(reports);
      optimumNumberOfReports++;
    }

    return optimumNumberOfReports;
  }

  private long toBytes(Reports reports) throws Exception {
    return gson.toJson(reports).getBytes("UTF-8").length;
  }

  private Reports givenAReportsInstanceFullOfData(int numberOfReports) {
    List<String> reportIds = givenSomeIds(numberOfReports);
    String appPackage = "io.flowup.androidsdk";
    String uuid = "1e54751e.28be.404a.88c0.5004140323d8";
    String deviceModel = "Samsung Galaxy S3";
    String screenDensity = "xxxhdpi";
    String screenSize = "1080X1794";
    int numberOfCores = 6;
    List<NetworkMetric> networkMetrics = givenSomeNetworkMetrics(numberOfReports);
    List<UIMetric> uiMetrics = givenSomeUIMetrics(numberOfReports);
    List<CPUMetric> cpuMetrics = givenSomeCPUMetrics(numberOfReports);
    List<MemoryMetric> memoryMetrics = givenSomeMemoryMetrics(numberOfReports);
    List<DiskMetric> diskMetrics = givenSomeDiskMetrics(numberOfReports);
    return new Reports(reportIds, appPackage, uuid, deviceModel, screenDensity, screenSize,
        numberOfCores, networkMetrics, uiMetrics, cpuMetrics, memoryMetrics, diskMetrics);
  }

  private List<NetworkMetric> givenSomeNetworkMetrics(int numberOfReports) {
    List<NetworkMetric> networkMetrics = new LinkedList<>();
    for (int i = 0; i < numberOfReports; i++) {
      NetworkMetric networkMetric = generateAnyNetworkMetric(i);
      networkMetrics.add(networkMetric);
    }
    return networkMetrics;
  }

  private List<CPUMetric> givenSomeCPUMetrics(int numberOfReports) {
    List<CPUMetric> cpuMetrics = new LinkedList<>();
    for (int i = 0; i < numberOfReports; i++) {
      CPUMetric networkMetric = generateAnyCPUMetric(i);
      cpuMetrics.add(networkMetric);
    }
    return cpuMetrics;
  }

  private List<MemoryMetric> givenSomeMemoryMetrics(int numberOfReports) {
    List<MemoryMetric> memoryMetrics = new LinkedList<>();
    for (int i = 0; i < numberOfReports; i++) {
      MemoryMetric memoryMetric = generateAMemoryMetric(i);
      memoryMetrics.add(memoryMetric);
    }
    return memoryMetrics;
  }

  private List<DiskMetric> givenSomeDiskMetrics(int numberOfReports) {
    List<DiskMetric> diskMetrics = new LinkedList<>();
    for (int i = 0; i < numberOfReports; i++) {
      DiskMetric diskMetric = generateADiskMetric(i);
      diskMetrics.add(diskMetric);
    }
    return diskMetrics;
  }

  private List<UIMetric> givenSomeUIMetrics(int numberOfReports) {
    List<UIMetric> uiMetrics = new LinkedList<>();
    for (int i = 0; i < numberOfReports * FlowUp.SAMPLING_INTERVAL; i++) {
      UIMetric uiMetric = generateAnyUIMetric(i);
      uiMetrics.add(uiMetric);
    }
    return uiMetrics;
  }

  private UIMetric generateAnyUIMetric(long timestamp) {
    StatisticalValue frameTime = givenAnyStatisticalValue();
    StatisticalValue onActivityCreatedTime = givenAnyStatisticalValue();
    StatisticalValue onActivityStartedTime = givenAnyStatisticalValue();
    StatisticalValue onActivityResumedTime = givenAnyStatisticalValue();
    StatisticalValue activityVisibleTime = givenAnyStatisticalValue();
    StatisticalValue onActivityPausedTime = givenAnyStatisticalValue();
    StatisticalValue onActivityStoppedTime = givenAnyStatisticalValue();
    StatisticalValue onActivityDestroyedTime = givenAnyStatisticalValue();
    return new UIMetric(timestamp, ANY_VERSION_NAME, ANY_OS_VERSION, ANY_BATTERY_SAVER_ON,
        "MainActivity", frameTime, onActivityCreatedTime, onActivityStartedTime,
        onActivityResumedTime, activityVisibleTime, onActivityPausedTime, onActivityStoppedTime,
        onActivityDestroyedTime);
  }

  private StatisticalValue givenAnyStatisticalValue() {
    return new StatisticalValue(1, Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE,
        Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE,
        Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE);
  }

  private NetworkMetric generateAnyNetworkMetric(long timestamp) {
    return new NetworkMetric(timestamp, ANY_VERSION_NAME, ANY_OS_VERSION, ANY_BATTERY_SAVER_ON,
        BYTES_UPLOADED, BYTES_DOWNLOADED);
  }

  private CPUMetric generateAnyCPUMetric(int timestamp) {
    return new CPUMetric(timestamp, ANY_VERSION_NAME, ANY_OS_VERSION, ANY_BATTERY_SAVER_ON,
        CPU_USAGE);
  }

  private MemoryMetric generateAMemoryMetric(int timestamp) {
    return new MemoryMetric(timestamp, ANY_VERSION_NAME, ANY_OS_VERSION, ANY_BATTERY_SAVER_ON,
        BYTES_ALLOCATED, MEMORY_USAGE);
  }

  private DiskMetric generateADiskMetric(int timestamp) {
    return new DiskMetric(timestamp, ANY_VERSION_NAME, ANY_OS_VERSION, ANY_BATTERY_SAVER_ON,
        BYTES_WRITTEN, BYTES_WRITTEN);
  }

  private List<String> givenSomeIds(int numberOfReports) {
    List<String> ids = new LinkedList<>();
    for (int i = 0; i < numberOfReports; i++) {
      ids.add(String.valueOf(i));
    }
    return ids;
  }
}

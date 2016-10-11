/*
 * Copyright (C) 2016 Go Karumi S.L.
 */

package com.flowup.reporter;

import com.flowup.FlowUp;
import com.flowup.reporter.model.NetworkMetric;
import com.flowup.reporter.model.Reports;
import com.flowup.reporter.model.StatisticalValue;
import com.flowup.reporter.model.UIMetric;
import com.google.gson.Gson;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.zip.GZIPOutputStream;
import org.junit.Test;

import static junit.framework.Assert.assertTrue;

public class NumberOfReportsPerBatchTest {

  private static final String ANY_VERSION_NAME = "1.0.0";
  private static final String ANY_OS_VERSION = "API24";
  private static final boolean ANY_BATTERY_SAVER_ON = true;
  private static final long BYTES_UPLOADED = Long.MAX_VALUE;
  private static final long BYTES_DOWNLOADED = Long.MAX_VALUE;

  private static final long MAX_REQUEST_SIZE_IN_BYTES = 90 * 1024;

  private final Gson gson = new Gson();

  @Test public void shouldNotSendMoreThan100KBOfBodyInAReportRequest() throws Exception {
    Reports reports = givenAReportsInstanceFullOfData(FlowUpReporter.NUMBER_OF_REPORTS_PER_REQUEST);

    long bytes = toBytes(reports);

    assertTrue(bytes <= MAX_REQUEST_SIZE_IN_BYTES);
  }

  private long toBytes(Reports reports) throws Exception {
    String body = gson.toJson(reports);
    return gzip(body).length;
  }

  private byte[] gzip(String str) throws IOException {
    if ((str == null) || (str.length() == 0)) {
      return null;
    }
    ByteArrayOutputStream obj = new ByteArrayOutputStream();
    GZIPOutputStream gzip = new GZIPOutputStream(obj);
    gzip.write(str.getBytes("UTF-8"));
    gzip.close();
    return obj.toByteArray();
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
    List<UIMetric> uiMetricsReports = givenSomeUIMetrics(numberOfReports);
    return new Reports(reportIds, appPackage, uuid, deviceModel, screenDensity, screenSize,
        numberOfCores, networkMetrics, uiMetricsReports);
  }

  private List<NetworkMetric> givenSomeNetworkMetrics(int numberOfReports) {
    List<NetworkMetric> networkMetrics = new LinkedList<>();
    for (int i = 0; i < numberOfReports; i++) {
      NetworkMetric networkMetric = generateAnyNetworkMetric(i);
      networkMetrics.add(networkMetric);
    }
    return networkMetrics;
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
    StatisticalValue fps = givenAnyStatisticalValue();
    String screenName = "MainActivity";
    return new UIMetric(timestamp, ANY_VERSION_NAME, ANY_OS_VERSION, ANY_BATTERY_SAVER_ON,
        screenName, frameTime, fps);
  }

  private StatisticalValue givenAnyStatisticalValue() {
    return new StatisticalValue(1, Long.MAX_VALUE, Long.MAX_VALUE, Long.MAX_VALUE, Long.MAX_VALUE,
        Long.MAX_VALUE, Long.MAX_VALUE, Long.MAX_VALUE, Long.MAX_VALUE, Long.MAX_VALUE,
        Long.MAX_VALUE, Long.MAX_VALUE, Long.MAX_VALUE, Long.MAX_VALUE, Long.MAX_VALUE,
        Long.MAX_VALUE, Long.MAX_VALUE, Long.MAX_VALUE, Long.MAX_VALUE, Long.MAX_VALUE,
        Long.MAX_VALUE, Long.MAX_VALUE, Long.MAX_VALUE);
  }

  private NetworkMetric generateAnyNetworkMetric(long timestamp) {
    return new NetworkMetric(timestamp, ANY_VERSION_NAME, ANY_OS_VERSION, ANY_BATTERY_SAVER_ON,
        BYTES_UPLOADED, BYTES_DOWNLOADED);
  }

  private List<String> givenSomeIds(int numberOfReports) {
    List<String> ids = new LinkedList<>();
    for (int i = 0; i < numberOfReports; i++) {
      ids.add(String.valueOf(i));
    }
    return ids;
  }
}

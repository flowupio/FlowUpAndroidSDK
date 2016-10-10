/*
 * Copyright (C) 2016 Go Karumi S.L.
 */

package com.flowup.reporter.apiclient;

import com.flowup.BuildConfig;
import com.flowup.MockWebServerTestCase;
import com.flowup.reporter.ReportResult;
import com.flowup.reporter.model.NetworkMetric;
import com.flowup.reporter.model.Reports;
import com.flowup.reporter.model.StatisticalValue;
import com.flowup.reporter.model.UIMetric;
import java.util.Collections;
import java.util.List;
import org.junit.Before;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.TestCase.assertTrue;

public class ApiClientTest extends MockWebServerTestCase {

  private static final long ANY_TIMESTAMP = 123456789;
  private static final String ANY_VERSION_NAME = "1.0.0";
  private static final String ANY_OS_VERSION = "API24";
  private static final boolean ANY_BATTERY_SAVER_ON = true;

  private ApiClient apiClient;

  @Before public void setUp() throws Exception {
    super.setUp();
    apiClient = new ApiClient(getScheme(), getHost(), getPort());
  }

  @Test public void sendsAcceptApplicationJsonHeader() throws Exception {
    enqueueMockResponse();
    Reports reports = givenSomeReports();

    apiClient.sendReports(reports);

    assertRequestContainsHeader("Accept", "application/json");
  }

  @Test public void sendsContentTypeJsonHeader() throws Exception {
    enqueueMockResponse();
    Reports reports = givenSomeReports();

    apiClient.sendReports(reports);

    assertRequestContainsHeader("Content-Type", "application/json; charset=utf-8");
  }

  @Test public void sendsApiKeyHeader() throws Exception {
    enqueueMockResponse();
    Reports reports = givenSomeReports();

    apiClient.sendReports(reports);

    assertRequestContainsHeader("X-Api-key", "<This will be implemented in the future>");
  }

  @Test public void sendsUserAgentHeader() throws Exception {
    enqueueMockResponse();
    Reports reports = givenSomeReports();

    apiClient.sendReports(reports);

    assertRequestContainsHeader("User-Agent", "FlowUpAndroidSDK/" + BuildConfig.VERSION_NAME);
  }

  @Test public void sendsReportRequestToTheCorrectPath() throws Exception {
    enqueueMockResponse();
    Reports reports = givenSomeReports();

    apiClient.sendReports(reports);

    assertRequestSentTo("/report");
  }

  @Test public void sendsTheCorrectBodyBasedOnAReportsInstance() throws Exception {
    enqueueMockResponse();
    Reports reports = givenAReportsInstanceBasedOnJustOneReport();

    apiClient.sendReports(reports);

    assertRequestBodyEquals("simpleReportRequestBody.json");
  }

  @Test public void returnsSuccessResultIfTheHttpStatusCodeIsOk() throws Exception {
    enqueueMockResponse(OK_CODE);
    Reports reports = givenSomeReports();

    ReportResult result = apiClient.sendReports(reports);

    assertTrue(result.isSuccess());
  }

  @Test
  public void returnsErrorIfServerHasAnInternalError() throws Exception {
    enqueueMockResponse(ANY_SERVER_ERROR_CODE);
    Reports reports = givenSomeReports();

    ReportResult result = apiClient.sendReports(reports);

    assertFalse(result.isSuccess());
  }

  @Test
  public void returnsTheReportsSentAsPartOfTheReportResultIfTheResponseIsOk() throws Exception {
    enqueueMockResponse(OK_CODE);
    Reports reports = givenSomeReports();

    ReportResult result = apiClient.sendReports(reports);

    assertEquals(reports, result.getReports());
  }

  private Reports givenSomeReports() {
    return givenAReportsInstanceBasedOnJustOneReport();
  }

  private Reports givenAReportsInstanceBasedOnJustOneReport() {
    List reportIds = Collections.EMPTY_LIST;
    String appPackage = "io.flowup.example";
    String uuid = "123456789";
    String deviceModel = "Nexus 5X";
    String screenDensity = "xxhdpi";
    String screenSize = "800X600";
    int numberOfCores = 4;
    List<NetworkMetric> networkMetrics = Collections.singletonList(givenANetworkMetric());
    List<UIMetric> uiMetrics = Collections.singletonList(givenAUIMetric());
    return new Reports(reportIds, appPackage, uuid, deviceModel, screenDensity, screenSize,
        numberOfCores, networkMetrics, uiMetrics);
  }

  private NetworkMetric givenANetworkMetric() {
    return new NetworkMetric(ANY_TIMESTAMP, ANY_VERSION_NAME, ANY_OS_VERSION, ANY_BATTERY_SAVER_ON,
        1024, 2048);
  }

  private UIMetric givenAUIMetric() {
    StatisticalValue frameTime = givenAnyStatisticalValue();
    StatisticalValue fps = givenAnyStatisticalValue();
    return new UIMetric(ANY_TIMESTAMP, ANY_VERSION_NAME, ANY_OS_VERSION, ANY_BATTERY_SAVER_ON,
        "MainActivity", frameTime, fps);
  }

  private StatisticalValue givenAnyStatisticalValue() {
    return new StatisticalValue(1, 60, 60, 60, 0, 60, 60, 60, 60, 60, 60, 60, 60, 60, 60, 60, 60,
        60, 60, 60, 60, 60, 60);
  }
}
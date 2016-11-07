/*
 * Copyright (C) 2016 Go Karumi S.L.
 */

package io.flowup.reporter.apiclient;

import io.flowup.BuildConfig;
import io.flowup.MockWebServerTestCase;
import io.flowup.apiclient.ApiClientResult;
import io.flowup.reporter.model.CPUMetric;
import io.flowup.reporter.model.DiskMetric;
import io.flowup.reporter.model.MemoryMetric;
import io.flowup.reporter.model.NetworkMetric;
import io.flowup.reporter.model.Reports;
import io.flowup.reporter.model.StatisticalValue;
import io.flowup.reporter.model.UIMetric;
import java.util.Collections;
import java.util.List;
import org.junit.Before;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.TestCase.assertTrue;

public class ReporterApiClientTest extends MockWebServerTestCase {

  private static final long ANY_TIMESTAMP = 123456789;
  private static final String ANY_VERSION_NAME = "1.0.0";
  private static final String ANY_OS_VERSION = "API24";
  private static final boolean ANY_BATTERY_SAVER_ON = true;
  private static final int ANY_CPU_USAGE_PERCENTAGE = 10;
  private static final int ANY_BYTES_ALLOCATED = 1024;
  private static final int ANY_MEMORY_PERCENTAGE_USAGE = 3;
  private static final long ANY_INTERNAL_STORAGE_WRITTEN_BYTES = 2048;
  private static final long ANY_SHARED_PREFS_WRITTEN_BYTES = 1024;
  private static final String ANY_API_KEY = "15207698c544f617e2c11151ada4972e1e7d6e8e";

  private ReporterApiClient reporterApiClient;

  @Before public void setUp() throws Exception {
    super.setUp();
    boolean useGzip = false;
    reporterApiClient = givenAnApiClient(useGzip);
  }

  @Test public void sendsAcceptApplicationJsonHeader() throws Exception {
    enqueueMockResponse();
    Reports reports = givenSomeReports();

    reporterApiClient.sendReports(reports);

    assertRequestContainsHeader("Accept", "application/json");
  }

  @Test public void sendsContentTypeJsonHeader() throws Exception {
    enqueueMockResponse();
    Reports reports = givenSomeReports();

    reporterApiClient.sendReports(reports);

    assertRequestContainsHeader("Content-Type", "application/json; charset=utf-8");
  }

  @Test public void sendsGzipSupportHeader() throws Exception {
    enqueueMockResponse();
    Reports reports = givenSomeReports();

    givenAnApiClient(true).sendReports(reports);

    assertRequestContainsHeader("Content-Encoding", "gzip");
  }

  @Test public void sendsApiKeyHeader() throws Exception {
    enqueueMockResponse();
    Reports reports = givenSomeReports();

    reporterApiClient.sendReports(reports);

    assertRequestContainsHeader("X-Api-key", ANY_API_KEY);
  }

  @Test public void sendsUserAgentHeader() throws Exception {
    enqueueMockResponse();
    Reports reports = givenSomeReports();

    reporterApiClient.sendReports(reports);

    assertRequestContainsHeader("User-Agent", "FlowUpAndroidSDK/" + BuildConfig.VERSION_NAME);
  }

  @Test public void sendsReportRequestToTheCorrectPath() throws Exception {
    enqueueMockResponse();
    Reports reports = givenSomeReports();

    reporterApiClient.sendReports(reports);

    assertRequestSentTo("/report");
  }

  @Test public void sendsTheCorrectBodyBasedOnAReportsInstance() throws Exception {
    enqueueMockResponse();
    Reports reports = givenAReportsInstanceBasedOnJustOneReport();

    reporterApiClient.sendReports(reports);

    assertRequestBodyEquals("apiClient/simpleReportRequestBody.json");
  }

  @Test public void returnsSuccessResultIfTheHttpStatusCodeIsOk() throws Exception {
    enqueueMockResponse(OK_CODE);
    Reports reports = givenSomeReports();

    ApiClientResult result = reporterApiClient.sendReports(reports);

    assertTrue(result.isSuccess());
  }

  @Test public void returnsErrorIfServerHasAnInternalError() throws Exception {
    enqueueMockResponse(SERVER_ERROR_CODE);
    Reports reports = givenSomeReports();

    ApiClientResult result = reporterApiClient.sendReports(reports);

    assertFalse(result.isSuccess());
  }

  @Test public void returnsTheReportsSentAsPartOfTheReportResultIfTheResponseIsOk()
      throws Exception {
    enqueueMockResponse(OK_CODE);
    Reports reports = givenSomeReports();

    ApiClientResult result = reporterApiClient.sendReports(reports);

    assertEquals(reports, result.getValue());
  }

  @Test public void returnsUnauthorizedErrorIfTheServerSideResponseIsA401() throws Exception {
    enqueueMockResponse(UNAUTHORIZED_ERROR_CODE);
    Reports reports = givenSomeReports();

    ApiClientResult result = reporterApiClient.sendReports(reports);

    assertFalse(result.isSuccess());
    assertEquals(ApiClientResult.Error.UNAUTHORIZED, result.getError());
  }

  @Test public void returnsUnauthorizedErrorIfTheServerSideResponseIsA403() throws Exception {
    enqueueMockResponse(FORBIDDEN_ERROR_CODE);
    Reports reports = givenSomeReports();

    ApiClientResult result = reporterApiClient.sendReports(reports);

    assertFalse(result.isSuccess());
    assertEquals(ApiClientResult.Error.UNAUTHORIZED, result.getError());
  }

  @Test public void returnsServerErrorIfTheServerSideResponseIsA500() throws Exception {
    enqueueMockResponse(SERVER_ERROR_CODE);
    Reports reports = givenSomeReports();

    ApiClientResult result = reporterApiClient.sendReports(reports);

    assertFalse(result.isSuccess());
    assertEquals(ApiClientResult.Error.SERVER_ERROR, result.getError());
  }

  private ReporterApiClient givenAnApiClient(boolean useGzip) {
    return new ReporterApiClient(ANY_API_KEY, getScheme(), getHost(), getPort(), useGzip);
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
    List<CPUMetric> cpuMetrics = Collections.singletonList(givenACPUMetric());
    List<MemoryMetric> memoryMetrics = Collections.singletonList(givenAMemoryMetric());
    List<DiskMetric> diskMetrics = Collections.singletonList(givenADiskMetric());
    return new Reports(reportIds, appPackage, uuid, deviceModel, screenDensity, screenSize,
        numberOfCores, networkMetrics, uiMetrics, cpuMetrics, memoryMetrics, diskMetrics);
  }

  private CPUMetric givenACPUMetric() {
    return new CPUMetric(ANY_TIMESTAMP, ANY_VERSION_NAME, ANY_OS_VERSION, ANY_BATTERY_SAVER_ON,
        ANY_CPU_USAGE_PERCENTAGE);
  }

  private MemoryMetric givenAMemoryMetric() {
    return new MemoryMetric(ANY_TIMESTAMP, ANY_VERSION_NAME, ANY_OS_VERSION, ANY_BATTERY_SAVER_ON,
        ANY_BYTES_ALLOCATED, ANY_MEMORY_PERCENTAGE_USAGE);
  }

  private DiskMetric givenADiskMetric() {
    return new DiskMetric(ANY_TIMESTAMP, ANY_VERSION_NAME, ANY_OS_VERSION, ANY_BATTERY_SAVER_ON,
        ANY_INTERNAL_STORAGE_WRITTEN_BYTES, ANY_SHARED_PREFS_WRITTEN_BYTES);
  }

  private NetworkMetric givenANetworkMetric() {
    return new NetworkMetric(ANY_TIMESTAMP, ANY_VERSION_NAME, ANY_OS_VERSION, ANY_BATTERY_SAVER_ON,
        1024, 2048);
  }

  private UIMetric givenAUIMetric() {
    StatisticalValue frameTime = givenAnyStatisticalValue();
    StatisticalValue fps = givenAnyStatisticalValue();
    StatisticalValue onActivityCreatedTime = givenAnyStatisticalValue();
    StatisticalValue onActivityStartedTime = givenAnyStatisticalValue();
    StatisticalValue onActivityResumedTime = givenAnyStatisticalValue();
    StatisticalValue activityVisibleTime = givenAnyStatisticalValue();
    StatisticalValue onActivityPausedTime = givenAnyStatisticalValue();
    StatisticalValue onActivityStoppedTime = givenAnyStatisticalValue();
    StatisticalValue onActivityDestroyedTime = givenAnyStatisticalValue();
    return new UIMetric(ANY_TIMESTAMP, ANY_VERSION_NAME, ANY_OS_VERSION, ANY_BATTERY_SAVER_ON,
        "MainActivity", frameTime, fps, onActivityCreatedTime, onActivityStartedTime,
        onActivityResumedTime, activityVisibleTime, onActivityPausedTime, onActivityStoppedTime,
        onActivityDestroyedTime);
  }

  private StatisticalValue givenAnyStatisticalValue() {
    return new StatisticalValue(1, 60, 60, 60, 0, 60, 60, 60, 60, 60, 60, 60, 60, 60);
  }
}
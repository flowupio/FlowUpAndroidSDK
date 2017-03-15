/*
 * Copyright (C) 2016 Go Karumi S.L.
 */

package io.flowup.crashreporter.apiclient;

import io.flowup.BuildConfig;
import io.flowup.MockWebServerTestCase;
import io.flowup.android.Device;
import io.flowup.apiclient.ApiClientResult;
import io.flowup.doubles.AnyDevice;
import org.junit.Before;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

public class CrashReporterApiClientTest extends MockWebServerTestCase {

  private static final String ANY_API_KEY = "123456";
  private static final Throwable ANY_EXCEPTION = new NullPointerException();

  private CrashReporterApiClient apiClient;
  private Device device = new AnyDevice();

  @Before public void setUp() throws Exception {
    super.setUp();
    apiClient = givenAnApiClient(false);
  }

  private CrashReporterApiClient givenAnApiClient(boolean debugEnabled) {
    return new CrashReporterApiClient(ANY_API_KEY, device, getScheme(), getHost(), getPort(),
        debugEnabled);
  }

  @Test public void sendsAcceptApplicationJsonHeader() throws Exception {
    enqueueMockResponse();

    apiClient.reportError(ANY_EXCEPTION);

    assertRequestContainsHeader("Accept", "application/json");
  }

  @Test public void sendsApiKeyHeader() throws Exception {
    enqueueMockResponse();

    apiClient.reportError(ANY_EXCEPTION);

    assertRequestContainsHeader("X-Api-key", ANY_API_KEY);
  }

  @Test public void sendsUUIDHeader() throws Exception {
    enqueueMockResponse();

    apiClient.reportError(ANY_EXCEPTION);

    assertRequestContainsHeader("X-UUID", device.getInstallationUUID());
  }

  @Test public void sendsUserAgentHeader() throws Exception {
    enqueueMockResponse();

    apiClient.reportError(ANY_EXCEPTION);

    assertRequestContainsHeader("User-Agent", "FlowUpAndroidSDK/" + BuildConfig.VERSION_NAME);
  }

  @Test public void sendsUserAgentHeaderIncludingTheDebugInformation() throws Exception {
    enqueueMockResponse();
    apiClient = givenAnApiClient(true);

    apiClient.reportError(ANY_EXCEPTION);

    assertRequestContainsHeader("User-Agent",
        "FlowUpAndroidSDK/" + BuildConfig.VERSION_NAME + "-DEBUG");
  }

  @Test public void sendsDebugHeaderUsingTheForceReportInformationIfIsDisabled() throws Exception {
    enqueueMockResponse();
    boolean forceReportsEnabled = false;
    apiClient = givenAnApiClient(forceReportsEnabled);

    apiClient.reportError(ANY_EXCEPTION);

    assertRequestContainsHeader("X-Debug-Mode", String.valueOf(forceReportsEnabled));
  }

  @Test public void sendsDebugHeaderUsingTheForceReportInformationIfIsEnabled() throws Exception {
    enqueueMockResponse();
    boolean forceReportsEnabled = true;
    apiClient = givenAnApiClient(forceReportsEnabled);

    apiClient.reportError(ANY_EXCEPTION);

    assertRequestContainsHeader("X-Debug-Mode", String.valueOf(forceReportsEnabled));
  }

  @Test public void sendsPostConfigRequestToTheCorrectPath() throws Exception {
    enqueueMockResponse();

    apiClient.reportError(ANY_EXCEPTION);

    assertRequestSentTo("/errorReport");
  }

  @Test public void returnsAErrorReportedProperlyIfTheResponseStatusCodeIsNotAnError()
      throws Exception {
    enqueueMockResponse(CREATED_CODE);

    ApiClientResult<Object> result = apiClient.reportError(ANY_EXCEPTION);

    assertTrue(result.isSuccess());
  }

  @Test public void returnsUnknownErrorIfSomethingWentWrong() throws Exception {
    enqueueMockResponse(SERVER_ERROR_CODE);

    ApiClientResult<Object> result = apiClient.reportError(ANY_EXCEPTION);

    assertEquals(ApiClientResult.Error.UNKNOWN, result.getError());
  }

  @Test public void sendsTheStackTraceAsPartOfTheRequestBody() throws Exception {
    enqueueMockResponse();

    Throwable throwable = new NullPointerException("Crash collecting data");
    StackTraceElement[] stackTrace = new StackTraceElement[1];
    stackTrace[0] = new StackTraceElement("Declaring class", "method name", "file name", 11);
    throwable.setStackTrace(stackTrace);
    apiClient.reportError(throwable);

    assertRequestBodyEquals("crashreporter/reportErrorRequest.json");
  }
}
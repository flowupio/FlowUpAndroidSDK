/*
 * Copyright (C) 2016 Go Karumi S.L.
 */

package com.flowup.reporter.apiclient;

import com.flowup.BuildConfig;
import com.flowup.MockWebServerTestCase;
import com.flowup.reporter.model.Reports;
import java.util.Collections;
import org.junit.Before;
import org.junit.Test;

public class ApiClientTest extends MockWebServerTestCase {

  private ApiClient apiClient;

  @Before public void setUp() throws Exception {
    super.setUp();
    apiClient = new ApiClient(getScheme(), getHost(), getPort());
  }

  @Test public void sendsAcceptApplicationJsonHeader() throws Exception {
    enqueueMockResponse();
    Reports metrics = givenAnyMetrics();

    apiClient.sendReports(metrics);

    assertRequestContainsHeader("Accept", "application/json");
  }

  @Test public void sendsContentTypeJsonHeader() throws Exception {
    enqueueMockResponse();
    Reports metrics = givenAnyMetrics();

    apiClient.sendReports(metrics);

    assertRequestContainsHeader("Content-Type", "application/json; charset=utf-8");
  }

  @Test public void sendsApiKeyHeader() throws Exception {
    enqueueMockResponse();
    Reports metrics = givenAnyMetrics();

    apiClient.sendReports(metrics);

    assertRequestContainsHeader("X-Api-key", "<This will be implemented in the future>");
  }

  @Test public void sendsUserAgentHeader() throws Exception {
    enqueueMockResponse();
    Reports metrics = givenAnyMetrics();

    apiClient.sendReports(metrics);

    assertRequestContainsHeader("User-Agent", "FlowUpAndroidSDK/" + BuildConfig.VERSION_NAME);
  }

  @Test public void sendsReportRequestToTheCorrectPath() throws Exception {
    enqueueMockResponse();
    Reports metrics = givenAnyMetrics();

    apiClient.sendReports(metrics);

    assertRequestSentTo("/report");
  }

  private Reports givenAnyMetrics() {
    return new Reports(Collections.EMPTY_LIST, "", "", "", "", "", 0, null, null);
  }
}
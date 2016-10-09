/*
 * Copyright (C) 2016 Go Karumi S.L.
 */

package com.flowup.reporter.apiclient;

import com.flowup.BuildConfig;
import com.flowup.MockWebServerTestCase;
import com.flowup.reporter.model.Report;
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
    Report metrics = givenAnyMetrics();

    apiClient.sendMetrics(metrics);

    assertRequestContainsHeader("Accept", "application/json");
  }

  @Test public void sendsContentTypeJsonHeader() throws Exception {
    enqueueMockResponse();
    Report metrics = givenAnyMetrics();

    apiClient.sendMetrics(metrics);

    assertRequestContainsHeader("Content-Type", "application/json; charset=utf-8");
  }

  @Test public void sendsApiKeyHeader() throws Exception {
    enqueueMockResponse();
    Report metrics = givenAnyMetrics();

    apiClient.sendMetrics(metrics);

    assertRequestContainsHeader("X-Api-key", "<This will be implemented in the future>");
  }

  @Test public void sendsUserAgentHeader() throws Exception {
    enqueueMockResponse();
    Report metrics = givenAnyMetrics();

    apiClient.sendMetrics(metrics);

    assertRequestContainsHeader("User-Agent", "FlowUpAndroidSDK/" + BuildConfig.VERSION_NAME);
  }

  private Report givenAnyMetrics() {
    return new Report("", "", "", "", "", 0, null, null);
  }
}
/*
 * Copyright (C) 2016 Go Karumi S.L.
 */

package com.flowup.reporter.apiclient;

import com.flowup.BuildConfig;
import com.flowup.MockWebServerTestCase;
import com.flowup.reporter.model.Metrics;
import java.util.Collections;
import org.junit.Before;
import org.junit.Test;

public class ApiClientTestCase extends MockWebServerTestCase {

  private ApiClient apiClient;

  @Before public void setUp() throws Exception {
    super.setUp();
    apiClient = new ApiClient(getScheme(), getHost(), getPort());
  }

  @Test public void sendsAcceptApplicationJsonHeader() throws Exception {
    enqueueMockResponse();
    Metrics metrics = givenAnyMetrics();

    apiClient.sendMetrics(metrics);

    assertRequestContainsHeader("Accept", "application/json");
  }

  @Test public void sendsContentTypeJsonHeader() throws Exception {
    enqueueMockResponse();
    Metrics metrics = givenAnyMetrics();

    apiClient.sendMetrics(metrics);

    assertRequestContainsHeader("Content-Type", "application/json; charset=utf-8");
  }

  @Test public void sendsAcceptEncodingGzipDeflateHeader() throws Exception {
    enqueueMockResponse();
    Metrics metrics = givenAnyMetrics();

    apiClient.sendMetrics(metrics);

    assertRequestContainsHeader("Accept-Encoding", "gzip");
  }

  @Test public void sendsApiKeyHeader() throws Exception {
    enqueueMockResponse();
    Metrics metrics = givenAnyMetrics();

    apiClient.sendMetrics(metrics);

    assertRequestContainsHeader("X-Api-key", "<This will be implemented in the future>");
  }

  @Test public void sendsUserAgentHeader() throws Exception {
    enqueueMockResponse();
    Metrics metrics = givenAnyMetrics();

    apiClient.sendMetrics(metrics);

    assertRequestContainsHeader("User-Agent", "FlowUpAndroidSDK/" + BuildConfig.VERSION_NAME);
  }

  private Metrics givenAnyMetrics() {
    return new Metrics("", "", "", "", "", 0, Collections.EMPTY_LIST, Collections.EMPTY_LIST);
  }
}
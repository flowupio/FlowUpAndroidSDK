/*
 * Copyright (C) 2016 Go Karumi S.L.
 */

package com.flowup.reporter.apiclient;

import com.codahale.metrics.Counter;
import com.codahale.metrics.Gauge;
import com.codahale.metrics.Histogram;
import com.codahale.metrics.Meter;
import com.codahale.metrics.Timer;
import com.flowup.BuildConfig;
import com.flowup.MockWebServerTest;
import com.flowup.reporter.Metrics;
import java.util.SortedMap;
import java.util.TreeMap;
import org.junit.Before;
import org.junit.Test;

public class ApiClientTest extends MockWebServerTest {

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

    assertRequestContainsHeader("Accept-Encoding", "gzip, deflate");
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
    SortedMap<String, Gauge> gauges = new TreeMap<>();
    SortedMap<String, Counter> counters = new TreeMap<>();
    SortedMap<String, Histogram> histograms = new TreeMap<>();
    SortedMap<String, Meter> meters = new TreeMap<>();
    SortedMap<String, Timer> timers = new TreeMap<>();
    return new Metrics(gauges, counters, histograms, meters, timers);
  }
}
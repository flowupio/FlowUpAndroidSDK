/*
 * Copyright (C) 2016 Go Karumi S.L.
 */

package io.flowup.config;

import io.flowup.MockWebServerTestCase;
import org.junit.Before;
import org.junit.Test;

public class ConfigApiClientTest extends MockWebServerTestCase {

  private ConfigApiClient apiClient;

  @Before public void setUp() throws Exception {
    super.setUp();
    String anyApiKey = "123456";
    apiClient = new ConfigApiClient(anyApiKey, getScheme(), getHost(), getPort());
  }

  @Test public void sendsGetConfigRequestToTheCorrectPath() throws Exception {
    enqueueMockResponse(OK_CODE);

    apiClient.getConfig();

    assertRequestSentTo("/config");
  }
}
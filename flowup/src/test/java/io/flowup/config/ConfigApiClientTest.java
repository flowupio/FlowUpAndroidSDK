/*
 * Copyright (C) 2016 Go Karumi S.L.
 */

package io.flowup.config;

import io.flowup.BuildConfig;
import io.flowup.MockWebServerTestCase;
import io.flowup.apiclient.ApiClientResult;
import io.flowup.config.apiclient.ConfigApiClient;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ConfigApiClientTest extends MockWebServerTestCase {

  private static final String ANY_API_KEY = "123456";
  private static final String ANY_UUID = "device123";

  private ConfigApiClient apiClient;

  @Before public void setUp() throws Exception {
    super.setUp();
    apiClient = new ConfigApiClient(ANY_API_KEY, ANY_UUID, getScheme(), getHost(), getPort());
  }

  @Test public void sendsAcceptApplicationJsonHeader() throws Exception {
    enqueueMockResponse();

    apiClient.getConfig();

    assertRequestContainsHeader("Accept", "application/json");
  }

  @Test public void sendsApiKeyHeader() throws Exception {
    enqueueMockResponse();

    apiClient.getConfig();

    assertRequestContainsHeader("X-Api-key", ANY_API_KEY);
  }

  @Test public void sendsUUIDHeader() throws Exception {
    enqueueMockResponse();

    apiClient.getConfig();

    assertRequestContainsHeader("X-UUID", ANY_UUID);
  }

  @Test public void sendsUserAgentHeader() throws Exception {
    enqueueMockResponse();

    apiClient.getConfig();

    assertRequestContainsHeader("User-Agent", "FlowUpAndroidSDK/" + BuildConfig.VERSION_NAME);
  }

  @Test public void sendsGetConfigRequestToTheCorrectPath() throws Exception {
    enqueueMockResponse(OK_CODE);

    apiClient.getConfig();

    assertRequestSentTo("/config");
  }

  @Test public void returnsUnknownErrorIfItIsAServerError() throws Exception {
    enqueueMockResponse(SERVER_ERROR_CODE);

    ApiClientResult<Config> result = apiClient.getConfig();

    assertEquals(ApiClientResult.Error.UNKNOWN, result.getError());
  }

  @Test public void returnsUnknownErrorIfItIsAnUnauthorized() throws Exception {
    enqueueMockResponse(UNAUTHORIZED_ERROR_CODE);

    ApiClientResult<Config> result = apiClient.getConfig();

    assertEquals(ApiClientResult.Error.UNKNOWN, result.getError());
  }

  @Test public void returnsTheConfigObtainedFromTheEndpoint() throws Exception {
    enqueueMockResponse(OK_CODE, getContentFromFile("config/getConfigResponse.json"));

    Config config = apiClient.getConfig().getValue();

    assertFalse(config.isEnabled());
  }

  @Test public void returnsEnabledAsTrueIfTheConfigDoesNotContainsTheEnabledValue()
      throws Exception {
    enqueueMockResponse(OK_CODE, getContentFromFile("config/emptyConfigResponse.json"));

    Config config = apiClient.getConfig().getValue();

    assertTrue(config.isEnabled());
  }
}
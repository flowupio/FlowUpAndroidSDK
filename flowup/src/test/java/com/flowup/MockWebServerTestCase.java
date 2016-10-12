/*
 * Copyright (C) 2016 Go Karumi S.L.
 */

package com.flowup;

import com.flowup.utils.TestResourcesFileReader;
import com.google.gson.JsonParser;
import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

@RunWith(MockitoJUnitRunner.class) public abstract class MockWebServerTestCase {

  protected static final int OK_CODE = 200;
  protected static final int ANY_SERVER_ERROR_CODE = 500;

  private MockWebServer server;

  @Before public void setUp() throws Exception {
    this.server = new MockWebServer();
    this.server.start();
  }

  @After public void tearDown() throws Exception {
    server.shutdown();
  }

  protected void enqueueMockResponse() throws IOException {
    enqueueMockResponse(OK_CODE);
  }

  protected void enqueueMockResponse(int code) throws IOException {
    enqueueMockResponse(code, "{}");
  }

  protected void enqueueMockResponse(int code, String response) throws IOException {
    MockResponse mockResponse = new MockResponse();
    mockResponse.setResponseCode(code);
    mockResponse.setBody(response);
    server.enqueue(mockResponse);
  }

  protected void enqueueMockResponse(String fileName) throws IOException {
    MockResponse mockResponse = new MockResponse();
    String fileContent = getContentFromFile(fileName);
    mockResponse.setBody(fileContent);
    server.enqueue(mockResponse);
  }

  protected void assertRequestSentTo(String url) throws InterruptedException {
    RecordedRequest request = server.takeRequest();
    assertEquals(url, request.getPath());
  }

  protected void assertRequestSentTo(String url, int requestIndex) throws InterruptedException {
    RecordedRequest request = getRecordedRequestAtIndex(requestIndex);
    assertEquals(url, request.getPath());
  }

  protected void assertRequestSentContainsQueryElement(String element) throws InterruptedException {
    assertRequestSentContainsQueryElements(element);
  }

  protected void assertRequestSentContainsQueryElements(String... elements)
      throws InterruptedException {
    RecordedRequest request = server.takeRequest();
    for (String path : elements) {
      URI uri = URI.create(request.getPath());
      assertTrue(uri.getQuery().contains(path));
    }
  }

  protected void assertRequestDoesNotContainQueryElement(String element)
      throws InterruptedException {
    RecordedRequest request = server.takeRequest();
    URI uri = URI.create(request.getPath());
    String query = uri.getQuery();
    if (query != null) {
      assertFalse(query.contains(element));
    }
  }

  protected void assertRequestSentContainsQueryElements(int requestIndex, String... elements)
      throws InterruptedException {
    RecordedRequest request = getRecordedRequestAtIndex(requestIndex);
    for (String path : elements) {
      URI uri = URI.create(request.getPath());
      assertTrue(uri.getQuery().contains(path));
    }
  }

  protected void assertRequestSentContainsElementsOnPath(String... elements)
      throws InterruptedException {
    RecordedRequest request = server.takeRequest();
    for (String path : elements) {
      URI uri = URI.create(request.getPath());
      assertTrue(uri.getPath().contains(path));
    }
  }

  protected void assertRequestSentContainsElementsOnPath(int requestIndex, String... elements)
      throws InterruptedException {
    RecordedRequest request = getRecordedRequestAtIndex(requestIndex);
    for (String path : elements) {
      URI uri = URI.create(request.getPath());
      assertTrue(uri.getPath().contains(path));
    }
  }

  protected void assertRequestBodyEquals(String fileName) throws InterruptedException, IOException {
    RecordedRequest request = server.takeRequest();
    JsonParser jsonParser = new JsonParser();
    String fileContent = jsonParser.parse(getContentFromFile(fileName)).toString();
    assertEquals(fileContent, request.getBody().readUtf8());
  }

  protected void assertRequestBodyEquals(String fileName, int requestIndex)
      throws InterruptedException, IOException {
    RecordedRequest request = getRecordedRequestAtIndex(requestIndex);
    JsonParser jsonParser = new JsonParser();
    String fileContent = jsonParser.parse(getContentFromFile(fileName)).toString();
    assertEquals(fileContent, request.getBody().readUtf8());
  }

  protected String getScheme() {
    return "http";
  }

  protected String getHost() {
    return server.getHostName();
  }

  protected int getPort() {
    return server.getPort();
  }

  private RecordedRequest getRecordedRequestAtIndex(int requestIndex) throws InterruptedException {
    RecordedRequest request = null;
    for (int i = 0; i <= requestIndex; i++) {
      request = server.takeRequest();
    }
    return request;
  }

  protected Map<String, String> getFormDataFromRequestBody(String requestBody) {
    Map<String, String> formData = new HashMap<>();
    String[] requestParts = requestBody.split("&");
    for (int i = 0; i < requestParts.length; i++) {
      String[] fieldParts = requestParts[i].split("=");
      formData.put(fieldParts[0], fieldParts[1]);
    }
    return formData;
  }

  protected String getContentFromFile(String fileName) throws IOException {
    return TestResourcesFileReader.getContentFromFile(fileName);
  }

  protected void assertRequestContainsHeader(String key, String expectedValue)
      throws InterruptedException {
    assertRequestContainsHeader(key, expectedValue, 0);
  }

  protected void assertRequestContainsHeader(String key, String expectedValue, int requestIndex)
      throws InterruptedException {
    RecordedRequest recordedRequest = getRecordedRequestAtIndex(requestIndex);
    String value = recordedRequest.getHeader(key);
    assertEquals(expectedValue, value);
  }
}

/*
 * Copyright (C) 2016 Go Karumi S.L.
 */

package com.flowup.reporter.apiclient;

import com.flowup.reporter.MetricsReport;
import com.flowup.reporter.model.Metrics;
import com.google.gson.Gson;
import java.io.IOException;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ApiClient {

  private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

  private final OkHttpClient httpClient;
  private final Gson jsonParser;
  private final HttpUrl reportEndpoint;

  public ApiClient(String scheme, String host, int port) {
    this.httpClient = ApiClientConfig.getHttpClient();
    this.jsonParser = ApiClientConfig.getJsonParser();
    this.reportEndpoint = ApiClientConfig.buildURL(scheme, host, port);
  }

  public ApiReportResult sendMetrics(Metrics metrics) {
    Request request = generateReportRequest(metrics);
    try {
      Response response = httpClient.newCall(request).execute();
      if (response.isSuccessful()) {
        return new ApiReportResult(metrics);
      }
    } catch (IOException e) {
      return new ApiReportResult(ApiReportResult.Error.NETWORK_ERROR);
    }
    return new ApiReportResult(ApiReportResult.Error.UNKNOWN);
  }

  private Request generateReportRequest(Metrics metrics) {
    RequestBody body = RequestBody.create(JSON, jsonParser.toJson(metrics));
    return new Request.Builder().url(reportEndpoint).post(body).build();
  }
}

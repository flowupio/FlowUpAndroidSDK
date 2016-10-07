/*
 * Copyright (C) 2016 Go Karumi S.L.
 */

package com.flowup.reporter.apiclient;

import com.flowup.reporter.Metrics;
import com.google.gson.Gson;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ApiClient {

  private static final String SCHEME = "https";
  private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
  private static final long HTTP_TIMEOUT = 10;
  private static final Gson GSON = new Gson();

  private final MetricsToMetricsDTOMapper mapper;
  private final OkHttpClient httpClient;
  private final HttpUrl reportEndpoint;

  public ApiClient(String host, int port) {
    this.mapper = new MetricsToMetricsDTOMapper();
    this.httpClient = new OkHttpClient.Builder().connectTimeout(HTTP_TIMEOUT, TimeUnit.SECONDS)
        .readTimeout(HTTP_TIMEOUT, TimeUnit.SECONDS)
        .writeTimeout(HTTP_TIMEOUT, TimeUnit.SECONDS)
        .build();
    this.reportEndpoint = new HttpUrl.Builder().scheme(SCHEME).host(host).port(port).build();
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
    MetricsDTO metricsDTO = mapper.map(metrics);
    RequestBody body = RequestBody.create(JSON, GSON.toJson(metricsDTO));
    return new Request.Builder().url(reportEndpoint).post(body).build();
  }
}

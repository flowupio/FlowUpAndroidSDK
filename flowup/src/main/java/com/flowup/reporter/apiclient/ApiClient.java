/*
 * Copyright (C) 2016 Go Karumi S.L.
 */

package com.flowup.reporter.apiclient;

import com.flowup.reporter.ReportResult;
import com.flowup.reporter.model.Reports;
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
  private static final int FORBIDDEN_STATUS_CODE = 403;
  private static final int UNAUTHORIZED_STATUS_CODE = 401;

  private final OkHttpClient httpClient;
  private final Gson jsonParser;
  private final HttpUrl baseUrl;

  public ApiClient(String apiKey, String scheme, String host, int port) {
    this(apiKey, scheme, host, port, false);
  }

  public ApiClient(String apiKey, String scheme, String host, int port, boolean logEnabled) {
    this(apiKey, scheme, host, port, logEnabled, true);
  }

  public ApiClient(String apiKey, String scheme, String host, int port, boolean logEnabled,
      boolean useGzip) {
    this.httpClient = ApiClientConfig.getHttpClient(apiKey, logEnabled, useGzip);
    this.jsonParser = ApiClientConfig.getJsonParser();
    this.baseUrl = ApiClientConfig.buildURL(scheme, host, port);
  }

  public ReportResult sendReports(Reports reports) {
    Request request = generateReportRequest(reports);
    Response response = null;
    try {
      response = httpClient.newCall(request).execute();
      if (response.isSuccessful()) {
        return new ReportResult(reports);
      }
    } catch (IOException e) {
      return new ReportResult(ReportResult.Error.NETWORK_ERROR);
    }
    ReportResult.Error error = mapError(response);
    return new ReportResult(error);
  }

  private ReportResult.Error mapError(Response response) {
    switch (response.code()) {
      case FORBIDDEN_STATUS_CODE:
      case UNAUTHORIZED_STATUS_CODE:
        return ReportResult.Error.UNAUTHORIZED;
      default:
        return ReportResult.Error.UNKNOWN;
    }
  }

  private Request generateReportRequest(Reports metrics) {
    HttpUrl reportUrl = baseUrl.newBuilder("/report").build();
    RequestBody body = RequestBody.create(JSON, jsonParser.toJson(metrics));
    return new Request.Builder().url(reportUrl).post(body).build();
  }
}

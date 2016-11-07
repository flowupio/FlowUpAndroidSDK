/*
 * Copyright (C) 2016 Go Karumi S.L.
 */

package io.flowup.reporter.apiclient;

import io.flowup.apiclient.ApiClient;
import io.flowup.apiclient.ApiClientResult;
import io.flowup.reporter.model.Reports;
import java.io.IOException;
import okhttp3.HttpUrl;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ReporterApiClient extends ApiClient {

  private HttpUrl reportUrl;

  public ReporterApiClient(String apiKey, String scheme, String host, int port) {
    this(apiKey, scheme, host, port, true);
  }

  public ReporterApiClient(String apiKey, String scheme, String host, int port, boolean useGzip) {
    super(apiKey, scheme, host, port, useGzip);
    this.reportUrl = baseUrl.newBuilder("/report").build();
  }

  public ApiClientResult sendReports(Reports reports) {
    Request request = generateReportRequest(reports);
    Response response = null;
    try {
      response = httpClient.newCall(request).execute();
      if (response.isSuccessful()) {
        return new ApiClientResult(reports);
      }
    } catch (IOException e) {
      return new ApiClientResult(ApiClientResult.Error.NETWORK_ERROR);
    }
    ApiClientResult.Error error = mapError(response);
    return new ApiClientResult(error);
  }

  private ApiClientResult.Error mapError(Response response) {
    switch (response.code()) {
      case FORBIDDEN_STATUS_CODE:
      case UNAUTHORIZED_STATUS_CODE:
        return ApiClientResult.Error.UNAUTHORIZED;
      case SERVER_ERROR_STATUS_CODE:
        return ApiClientResult.Error.SERVER_ERROR;
      case PRECONDITION_FAILED_STATUS_CODE:
        return ApiClientResult.Error.CLIENT_DISABLED;
      default:
        return ApiClientResult.Error.UNKNOWN;
    }
  }

  private Request generateReportRequest(Reports metrics) {
    RequestBody body = RequestBody.create(JSON, jsonParser.toJson(metrics));
    return new Request.Builder().url(reportUrl).post(body).build();
  }
}
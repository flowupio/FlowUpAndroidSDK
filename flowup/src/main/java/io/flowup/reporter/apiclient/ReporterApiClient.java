/*
 * Copyright (C) 2016 Go Karumi S.L.
 */

package io.flowup.reporter.apiclient;

import io.flowup.apiclient.ApiClient;
import io.flowup.logger.Logger;
import io.flowup.reporter.ReportResult;
import io.flowup.reporter.model.Reports;
import com.google.gson.Gson;
import java.io.IOException;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ReporterApiClient extends ApiClient {

  public ReporterApiClient(String apiKey, String scheme, String host, int port) {
    super(apiKey, scheme, host, port);
  }

  public ReporterApiClient(String apiKey, String scheme, String host, int port, boolean useGzip) {
    super(apiKey, scheme, host, port, useGzip);
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
      case SERVER_ERROR_STATUS_CODE:
        return ReportResult.Error.SERVER_ERROR;
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

/*
 * Copyright (C) 2016 Go Karumi S.L.
 */

package io.flowup.crashreporter.apiclient;

import io.flowup.android.Device;
import io.flowup.apiclient.ApiClient;
import io.flowup.apiclient.ApiClientResult;
import io.flowup.utils.ExceptionUtils;
import java.io.IOException;
import okhttp3.HttpUrl;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class CrashReporterApiClient extends ApiClient {

  private final HttpUrl reportCrashUrl;

  public CrashReporterApiClient(String apiKey, Device device, String scheme, String host, int port,
      boolean debugEnabled) {
    super(apiKey, device, scheme, host, port, debugEnabled);
    reportCrashUrl = baseUrl.newBuilder("/errorReport").build();
  }

  public ApiClientResult<Void> reportError(Throwable t) {
    ErrorReport errorReport = mapExceptionToErrorReport(t);
    RequestBody body = RequestBody.create(JSON, jsonParser.toJson(errorReport));
    Request request = new Request.Builder().url(reportCrashUrl).post(body).build();
    try {
      Response response = httpClient.newCall(request).execute();
      if (response.isSuccessful()) {
        response.close();
        return new ApiClientResult<>((Void) null);
      }
    } catch (IOException e) {
      return new ApiClientResult<>(ApiClientResult.Error.NETWORK_ERROR);
    }
    return new ApiClientResult<>(ApiClientResult.Error.UNKNOWN);
  }

  private ErrorReport mapExceptionToErrorReport(Throwable t) {
    String message = ExceptionUtils.getMessage(t);
    String stackTrace = ExceptionUtils.getStackTrace(t);
    return new ErrorReport(message, stackTrace);
  }
}

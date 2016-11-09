/*
 * Copyright (C) 2016 Go Karumi S.L.
 */

package io.flowup.apiclient;

import com.google.gson.Gson;
import io.flowup.logger.Logger;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;

public class ApiClient {

  protected static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
  protected static final int FORBIDDEN_STATUS_CODE = 403;
  protected static final int UNAUTHORIZED_STATUS_CODE = 401;
  protected static final int SERVER_ERROR_STATUS_CODE = 500;
  protected static final int PRECONDITION_FAILED_STATUS_CODE = 412;

  protected final OkHttpClient httpClient;
  protected final Gson jsonParser;
  protected final HttpUrl baseUrl;

  public ApiClient(String apiKey, String uuid, String scheme, String host, int port) {
    this(apiKey, uuid, scheme, host, port, true);
  }

  public ApiClient(String apiKey, String uuid, String scheme, String host, int port,
      boolean useGzip) {
    this.httpClient = ApiClientConfig.getHttpClient(apiKey, uuid, Logger.isLogEnabled(), useGzip);
    this.jsonParser = ApiClientConfig.getJsonParser();
    this.baseUrl = ApiClientConfig.buildURL(scheme, host, port);
  }
}

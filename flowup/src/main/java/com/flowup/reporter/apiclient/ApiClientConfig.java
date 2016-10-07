/*
 * Copyright (C) 2016 Go Karumi S.L.
 */

package com.flowup.reporter.apiclient;

import com.google.gson.Gson;
import java.util.concurrent.TimeUnit;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;

class ApiClientConfig {

  private static final String SCHEME = "https";
  private static final long HTTP_TIMEOUT = 10;
  private static final OkHttpClient httpClient =
      new OkHttpClient.Builder()
          .connectTimeout(HTTP_TIMEOUT, TimeUnit.SECONDS)
          .readTimeout(HTTP_TIMEOUT, TimeUnit.SECONDS)
          .writeTimeout(HTTP_TIMEOUT, TimeUnit.SECONDS)
          .addInterceptor(new HeadersInterceptor())
          .build();
  private static final Gson GSON = new Gson();

  static OkHttpClient getHttpClient() {
    return httpClient;
  }

  static Gson getJsonParser() {
    return GSON;
  }

  static HttpUrl buildURL(String host, int port) {
    return new HttpUrl.Builder().scheme(SCHEME).host(host).port(port).build();
  }
}

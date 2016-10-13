/*
 * Copyright (C) 2016 Go Karumi S.L.
 */

package com.flowup.reporter.apiclient;

import com.google.gson.Gson;
import java.util.concurrent.TimeUnit;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

class ApiClientConfig {

  private static final long HTTP_TIMEOUT = 10;
  private static final OkHttpClient HTTP_CLIENT =
      new OkHttpClient.Builder().connectTimeout(HTTP_TIMEOUT, TimeUnit.SECONDS)
          .readTimeout(HTTP_TIMEOUT, TimeUnit.SECONDS)
          .writeTimeout(HTTP_TIMEOUT, TimeUnit.SECONDS)
          .addInterceptor(new FlowUpHeadersInterceptor())
          .build();

  private static final Gson GSON = new Gson();

  static OkHttpClient getHttpClient(boolean debug, boolean useGzip) {
    OkHttpClient httpClient = HTTP_CLIENT;
    if (debug) {
      HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
      httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
      httpClient = httpClient.newBuilder().addInterceptor(httpLoggingInterceptor).build();
    }
    if (useGzip) {
      GzipRequestInterceptor gzipInterceptor = new GzipRequestInterceptor();
      httpClient = httpClient.newBuilder().addInterceptor(gzipInterceptor).build();
    }
    return httpClient;
  }

  static Gson getJsonParser() {
    return GSON;
  }

  static HttpUrl buildURL(String scheme, String host, int port) {
    return new HttpUrl.Builder().scheme(scheme).host(host).port(port).build();
  }
}

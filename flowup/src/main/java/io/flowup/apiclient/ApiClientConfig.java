/*
 * Copyright (C) 2016 Go Karumi S.L.
 */

package io.flowup.apiclient;

import com.google.gson.Gson;
import io.flowup.android.Device;
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
          .build();

  private static final Gson GSON = new Gson();

  static OkHttpClient getHttpClient(String apiKey, Device device, boolean debugEnabled,
      boolean logEnabled, boolean useGzip) {
    OkHttpClient httpClient = HTTP_CLIENT;
    if (logEnabled) {
      HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
      httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
      httpClient = httpClient.newBuilder().addInterceptor(httpLoggingInterceptor).build();
    }
    if (useGzip) {
      GzipRequestInterceptor gzipInterceptor = new GzipRequestInterceptor();
      httpClient = httpClient.newBuilder().addInterceptor(gzipInterceptor).build();
    }
    return httpClient.newBuilder()
        .addInterceptor(new FlowUpHeadersInterceptor(apiKey, device, debugEnabled))
        .build();
  }

  static Gson getJsonParser() {
    return GSON;
  }

  static HttpUrl buildURL(String scheme, String host, int port) {
    return new HttpUrl.Builder().scheme(scheme).host(host).port(port).build();
  }
}

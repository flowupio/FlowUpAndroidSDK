/*
 * Copyright (C) 2016 Go Karumi S.L.
 */

package io.flowup.apiclient;

import io.flowup.BuildConfig;
import java.io.IOException;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

class FlowUpHeadersInterceptor implements Interceptor {

  private final String apiKey;
  private final String uuid;

  public FlowUpHeadersInterceptor(String apiKey, String uuid) {
    this.apiKey = apiKey;
    this.uuid = uuid;
  }

  @Override public Response intercept(Chain chain) throws IOException {
    Request request = chain.request()
        .newBuilder()
        .addHeader("Accept", "application/json")
        .addHeader("X-Api-Key", apiKey)
        .addHeader("X-UUID", uuid)
        .addHeader("User-Agent", "FlowUpAndroidSDK/" + BuildConfig.VERSION_NAME)
        .build();
    return chain.proceed(request);
  }
}

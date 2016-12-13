/*
 * Copyright (C) 2016 Go Karumi S.L.
 */

package io.flowup.apiclient;

import io.flowup.BuildConfig;
import io.flowup.android.Device;
import java.io.IOException;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

class FlowUpHeadersInterceptor implements Interceptor {

  private final String apiKey;
  private final Device device;
  private final boolean forceReportsEnabled;

  public FlowUpHeadersInterceptor(String apiKey, Device device, boolean forceReportsEnabled) {
    this.apiKey = apiKey;
    this.device = device;
    this.forceReportsEnabled = forceReportsEnabled;
  }

  @Override public Response intercept(Chain chain) throws IOException {
    Request request = chain.request()
        .newBuilder()
        .addHeader("Accept", "application/json")
        .addHeader("X-Api-Key", apiKey)
        .addHeader("X-UUID", device.getInstallationUUID())
        .addHeader("User-Agent", getUserAgent())
        .addHeader("X-Debug-Mode", String.valueOf(forceReportsEnabled))
        .build();
    return chain.proceed(request);
  }

  private String getUserAgent() {
    String debugTag = forceReportsEnabled ? ("-DEBUG") : "";
    return "FlowUpAndroidSDK/" + BuildConfig.VERSION_NAME + debugTag;
  }
}

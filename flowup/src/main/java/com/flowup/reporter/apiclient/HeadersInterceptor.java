/*
 * Copyright (C) 2016 Go Karumi S.L.
 */

package com.flowup.reporter.apiclient;

import com.flowup.BuildConfig;
import java.io.IOException;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

class HeadersInterceptor implements Interceptor {
  @Override public Response intercept(Chain chain) throws IOException {
    Request request = chain.request()
        .newBuilder()
        .addHeader("Accept", "application/json")
        .addHeader("Content-Type", "application/json")
        .addHeader("Accept-Encoding", "gzip, deflate")
        .addHeader("X-Api-Key", "<This will be implemented in the future>")
        .addHeader("User-Agent", "FlowUpAndroidSDK/" + BuildConfig.VERSION_NAME)
        .build();
    return chain.proceed(request);
  }
}

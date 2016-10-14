/*
 * Copyright (C) 2016 Go Karumi S.L.
 */

package com.flowup.reporter.apiclient;

import com.flowup.BuildConfig;
import java.io.IOException;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

class FlowUpHeadersInterceptor implements Interceptor {
  @Override public Response intercept(Chain chain) throws IOException {
    Request request = chain.request()
        .newBuilder()
        .addHeader("Accept", "application/json")
        .addHeader("X-Api-Key", "15207698c544f617e2c11151ada4972e1e7d6e8e")
        .addHeader("User-Agent", "FlowUpAndroidSDK/" + BuildConfig.VERSION_NAME)
        .build();
    return chain.proceed(request);
  }
}

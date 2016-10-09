/*
 * Copyright (C) 2016 Go Karumi S.L.
 */

package com.flowup.reporter.apiclient;

import com.flowup.BuildConfig;
import java.io.IOException;
import okhttp3.Interceptor;
import okhttp3.Response;

class OnlyDebugInterceptor implements Interceptor {

  private final Interceptor interceptor;

  public OnlyDebugInterceptor(Interceptor interceptor) {
    this.interceptor = interceptor;
  }

  @Override public Response intercept(Chain chain) throws IOException {
    if (BuildConfig.DEBUG) {
      return interceptor.intercept(chain);
    } else {
      return chain.proceed(chain.request());
    }
  }
}

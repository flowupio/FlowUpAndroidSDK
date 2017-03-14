/*
 * Copyright (C) 2016 Go Karumi S.L.
 */

package io.flowup.crashreporter.apiclient;

import com.google.gson.annotations.SerializedName;

class ErrorReport {

  @SerializedName("message") private final String message;
  @SerializedName("stackTrace") private final String stackTrace;

  ErrorReport(String message, String stackTrace) {
    this.message = message;
    this.stackTrace = stackTrace;
  }
}

/*
 * Copyright (C) 2016 Go Karumi S.L.
 */

package io.flowup.crashreporter.apiclient;

import com.google.gson.annotations.SerializedName;

public class ErrorReport {

  @SerializedName("stackTrace") private final String stackTrace;

  public ErrorReport(String stackTrace) {
    this.stackTrace = stackTrace;
  }
}

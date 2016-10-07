/*
 * Copyright (C) 2016 Go Karumi S.L.
 */

package com.flowup.reporter.apiclient;

import com.flowup.reporter.Metrics;

public class ApiReportResult {

  private Metrics metricsReported;
  private Error error;

  public ApiReportResult(Metrics metricsReported) {
    this.metricsReported = metricsReported;
  }

  public ApiReportResult(Error error) {
    this.error = error;
  }

  public boolean isSuccess() {
    return metricsReported != null && error == null;
  }

  public Metrics getMetricsReported() {
    return metricsReported;
  }

  public Error getError() {
    return error;
  }

  enum Error {
    NETWORK_ERROR,
    UNKNOWN
  }
}

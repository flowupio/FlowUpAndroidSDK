/*
 * Copyright (C) 2016 Go Karumi S.L.
 */

package com.flowup.reporter.apiclient;

import com.flowup.reporter.model.Metrics;

public class ApiReportResult {

  private Metrics metrics;
  private Error error;

  public ApiReportResult(Metrics metrics) {
    this.metrics = metrics;
  }

  public ApiReportResult(Error error) {
    this.error = error;
  }

  public boolean isSuccess() {
    return metrics != null && error == null;
  }

  public Metrics getMetricsReported() {
    return metrics;
  }

  public Error getError() {
    return error;
  }

  enum Error {
    NETWORK_ERROR,
    UNKNOWN
  }
}

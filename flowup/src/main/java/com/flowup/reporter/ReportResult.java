/*
 * Copyright (C) 2016 Go Karumi S.L.
 */

package com.flowup.reporter;

import com.flowup.reporter.model.Reports;

public class ReportResult {

  private Reports metrics;
  private Error error;

  public ReportResult(Reports metrics) {
    this.metrics = metrics;
  }

  public ReportResult(Error error) {
    this.error = error;
  }

  public boolean isSuccess() {
    return metrics != null && error == null;
  }

  public boolean hasDataPendingToSync() {
    return false;
  }

  public Reports getMetricsReported() {
    return metrics;
  }

  public Error getError() {
    return error;
  }

  public enum Error {
    NETWORK_ERROR,
    UNKNOWN
  }
}

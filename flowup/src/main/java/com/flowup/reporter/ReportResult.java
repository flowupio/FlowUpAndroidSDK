/*
 * Copyright (C) 2016 Go Karumi S.L.
 */

package com.flowup.reporter;

import com.flowup.reporter.model.Reports;

public class ReportResult {

  private Reports reports;
  private Error error;

  public ReportResult(Reports reports) {
    this.reports = reports;
  }

  public ReportResult(Error error) {
    this.error = error;
  }

  public boolean isSuccess() {
    return reports != null && error == null;
  }

  public boolean hasDataPendingToSync() {
    return false;
  }

  public Reports getReports() {
    return reports;
  }

  public Error getError() {
    return error;
  }

  public enum Error {
    NETWORK_ERROR,
    UNAUTHORIZED,
    UNKNOWN
  }
}

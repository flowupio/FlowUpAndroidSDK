/*
 * Copyright (C) 2016 Go Karumi S.L.
 */

package com.flowup.reporter.apiclient;

import com.flowup.reporter.Metrics;

public class ApiClient {

  private final String host;
  private final int port;

  public ApiClient(String host, int port) {
    this.host = host;
    this.port = port;
  }

  public void sendMetrics(Metrics metrics) {

  }
}

/*
 * Copyright (C) 2016 Go Karumi S.L.
 */

package io.flowup;

import android.app.Application;

public class FlowUp {

  FlowUp() {
  }

  void start() {
  }

  public static class Builder {

    Builder() {
    }

    public static Builder with(Application application) {
      return new Builder();
    }

    public Builder forceReports(boolean forceReports) {
      return this;
    }

    public Builder apiKey(String apiKey) {
      return this;
    }

    public Builder sampling(double sampling) {
      return this;
    }

    public Builder logEnabled(boolean logEnabled) {
      return this;
    }

    public void start() {
      new FlowUp().start();
    }
  }
}

/*
 * Copyright (C) 2016 Go Karumi S.L.
 */

package com.flowup.reporter.storage;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class RealmReport extends RealmObject {

  static final String ID_FIELD_NAME = "reportTimestamp";

  @PrimaryKey private String reportTimestamp;
  private RealmList<RealmMetricReport> metrics;

  public String getReportTimestamp() {
    return reportTimestamp;
  }

  public RealmList<RealmMetricReport> getMetrics() {
    return metrics;
  }

  public void setMetrics(RealmList<RealmMetricReport> metrics) {
    this.metrics = metrics;
  }
}

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
  private RealmList<RealmMetric> metrics;

  public String getReportTimestamp() {
    return reportTimestamp;
  }

  public RealmList<RealmMetric> getMetrics() {
    return metrics;
  }

  public void setMetrics(RealmList<RealmMetric> metrics) {
    this.metrics = metrics;
  }
}

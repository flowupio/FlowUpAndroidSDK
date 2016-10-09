/*
 * Copyright (C) 2016 Go Karumi S.L.
 */

package com.flowup.reporter.storage;

import io.realm.RealmObject;
import io.realm.annotations.Index;

public class RealmMetricReport extends RealmObject {

  @Index private String metricInfo;
  private RealmStatisticalValue value;

  public String getMetricInfo() {
    return metricInfo;
  }

  public void setMetricInfo(String metricInfo) {
    this.metricInfo = metricInfo;
  }

  public RealmStatisticalValue getValue() {
    return value;
  }

  public void setValue(RealmStatisticalValue value) {
    this.value = value;
  }
}

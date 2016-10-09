/*
 * Copyright (C) 2016 Go Karumi S.L.
 */

package com.flowup.reporter.storage;

import io.realm.RealmObject;
import io.realm.annotations.Index;

public class RealmMetricReport extends RealmObject {

  @Index private String metricName;
  private RealmStatisticalValue value;

  public String getMetricName() {
    return metricName;
  }

  public void setMetricName(String metricName) {
    this.metricName = metricName;
  }

  public RealmStatisticalValue getValue() {
    return value;
  }

  public void setValue(RealmStatisticalValue value) {
    this.value = value;
  }
}

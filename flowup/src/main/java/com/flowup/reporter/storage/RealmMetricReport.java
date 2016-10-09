/*
 * Copyright (C) 2016 Go Karumi S.L.
 */

package com.flowup.reporter.storage;

import io.realm.RealmObject;
import io.realm.annotations.Index;

public class RealmMetricReport extends RealmObject {

  @Index private String metricName;
  private RealmStatisticalValue statisticalValue;

  public String getMetricName() {
    return metricName;
  }

  public void setMetricName(String metricName) {
    this.metricName = metricName;
  }

  public RealmStatisticalValue getStatisticalValue() {
    return statisticalValue;
  }

  public void setStatisticalValue(RealmStatisticalValue value) {
    this.statisticalValue = value;
  }
}

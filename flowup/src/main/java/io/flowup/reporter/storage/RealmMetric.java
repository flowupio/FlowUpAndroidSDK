/*
 * Copyright (C) 2016 Go Karumi S.L.
 */

package io.flowup.reporter.storage;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class RealmMetric extends RealmObject {

  static final String ID_FIELD_NAME = "id";

  @PrimaryKey private String id;
  private String metricName;
  private RealmStatisticalValue statisticalValue;

  public String getId() {
    return id;
  }

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

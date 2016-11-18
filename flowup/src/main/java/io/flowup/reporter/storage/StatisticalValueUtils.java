/*
 * Copyright (C) 2016 Go Karumi S.L.
 */

package io.flowup.reporter.storage;

import io.flowup.reporter.model.StatisticalValue;

class StatisticalValueUtils {

  public static StatisticalValue fromSQLDelightMetric(SQLDelightMetric metric) {
    if (metric.count() == null || metric.count() == 0) {
      return null;
    }
    return new StatisticalValue(metric.mean(), metric.p10(), metric.p90());
  }
}

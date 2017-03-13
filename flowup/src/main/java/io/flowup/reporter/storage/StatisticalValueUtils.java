/*
 * Copyright (C) 2016 Go Karumi S.L.
 */

package io.flowup.reporter.storage;

import io.flowup.reporter.model.StatisticalValue;

class StatisticalValueUtils {

  static StatisticalValue fromSQLDelightMetric(SQLDelightMetric metric) {
    if (metric == null || !metric.containsData()) {
      return null;
    }
    return new StatisticalValue(metric.mean(), metric.p10(), metric.p90());
  }
}

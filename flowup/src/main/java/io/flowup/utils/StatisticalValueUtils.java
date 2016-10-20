/*
 * Copyright (C) 2016 Go Karumi S.L.
 */

package io.flowup.utils;

import io.flowup.reporter.model.StatisticalValue;
import io.flowup.reporter.storage.RealmStatisticalValue;

public class StatisticalValueUtils {

  public static StatisticalValue fromRealm(RealmStatisticalValue realmValue) {
    if (realmValue.getCount() == 0) {
      return null;
    }
    return new StatisticalValue(realmValue.getCount(), realmValue.getMin(), realmValue.getMax(),
        realmValue.getMean(), realmValue.getStandardDev(), realmValue.getMedian(),
        realmValue.getP1(), realmValue.getP2(), realmValue.getP5(), realmValue.getP10(),
        realmValue.getP90(), realmValue.getP95(), realmValue.getP98(), realmValue.getP99());
  }
}

/*
 * Copyright (C) 2016 Go Karumi S.L.
 */

package io.flowup.utils;

import io.flowup.reporter.model.StatisticalValue;
import io.flowup.reporter.storage.RealmStatisticalValue;

public class StatisticalValueUtils {

  public static StatisticalValue fromRealm(RealmStatisticalValue realmValue) {
    return new StatisticalValue(realmValue.getCount(), realmValue.getMin(), realmValue.getMax(),
        realmValue.getMean(), realmValue.getStandardDev(), realmValue.getMedian(),
        realmValue.getP5(), realmValue.getP10(), realmValue.getP15(), realmValue.getP20(),
        realmValue.getP25(), realmValue.getP30(), realmValue.getP40(), realmValue.getP50(),
        realmValue.getP60(), realmValue.getP70(), realmValue.getP75(), realmValue.getP80(),
        realmValue.getP85(), realmValue.getP90(), realmValue.getP95(), realmValue.getP98(),
        realmValue.getP99());
  }
}

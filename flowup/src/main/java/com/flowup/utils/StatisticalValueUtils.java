/*
 * Copyright (C) 2016 Go Karumi S.L.
 */

package com.flowup.utils;

import com.codahale.metrics.Sampling;
import com.codahale.metrics.Snapshot;
import com.flowup.reporter.model.StatisticalValue;

public class StatisticalValueUtils {

  public static StatisticalValue fromSampling(Sampling sampling) {
    Snapshot snapshot = sampling.getSnapshot();
    return new StatisticalValue(snapshot.getValues().length, snapshot.getMin(), snapshot.getMax(),
        snapshot.getMean(), snapshot.getStdDev(), snapshot.getMedian(), snapshot.getValue(0.5),
        snapshot.getValue(0.10), snapshot.getValue(0.15), snapshot.getValue(0.20),
        snapshot.getValue(0.25), snapshot.getValue(0.30), snapshot.getValue(0.40),
        snapshot.getValue(0.50), snapshot.getValue(0.60), snapshot.getValue(0.70),
        snapshot.getValue(0.75), snapshot.getValue(0.80), snapshot.getValue(0.85),
        snapshot.getValue(0.90), snapshot.getValue(0.95), snapshot.getValue(0.98),
        snapshot.getValue(0.99));
  }
}

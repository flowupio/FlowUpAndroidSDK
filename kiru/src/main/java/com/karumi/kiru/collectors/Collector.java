/*
 * Copyright (C) 2016 Go Karumi S.L.
 */

package com.karumi.kiru.collectors;

import com.codahale.metrics.MetricRegistry;

public interface Collector {

  void initialize(MetricRegistry registry);

}

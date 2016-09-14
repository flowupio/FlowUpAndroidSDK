/*
 * Copyright (C) 2016 Go Karumi S.L.
 */

package com.flowup.collectors;

import com.codahale.metrics.MetricRegistry;

public interface Collector {

  void initialize(MetricRegistry registry);

}

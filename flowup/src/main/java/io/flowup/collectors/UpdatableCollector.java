/*
 * Copyright (C) 2016 Go Karumi S.L.
 */

package io.flowup.collectors;

import com.codahale.metrics.MetricRegistry;

public interface UpdatableCollector extends Collector {
  void forceUpdate(MetricRegistry registry);
}

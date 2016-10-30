/*
 * Copyright (C) 2016 Go Karumi S.L.
 */

package io.flowup.collectors;

import com.codahale.metrics.Gauge;
import com.codahale.metrics.MetricRegistry;
import io.flowup.doubles.AnyApp;
import io.flowup.doubles.AnyDevice;
import io.flowup.metricnames.MetricNamesGenerator;
import io.flowup.utils.Time;
import java.util.concurrent.TimeUnit;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static junit.framework.Assert.assertNull;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class) public class NetworkUsageCollectorTest {

  private NetworkUsageCollector collector;

  @Mock private MetricRegistry registry;
  @Captor private ArgumentCaptor<Gauge<Long>> bytesUploadedCaptor;
  @Captor private ArgumentCaptor<Gauge<Long>> bytesDownloadedCaptor;

  @Before public void setUp() {
    MetricNamesGenerator generator =
        new MetricNamesGenerator(new AnyApp(), new AnyDevice(), new Time());
    when(registry.register(eq(generator.getBytesUploadedMetricName()),
        bytesUploadedCaptor.capture())).thenReturn(mock(Gauge.class));
    when(registry.register(eq(generator.getBytesDownloadedMetricName()),
        bytesDownloadedCaptor.capture())).thenReturn(mock(Gauge.class));
    TimeUnit anyTimeUnit = TimeUnit.NANOSECONDS;
    int anySamplingInterval = 0;
    collector = new NetworkUsageCollector(generator, anySamplingInterval, anyTimeUnit);
  }

  @Test public void doesNotReportBytesUploadedIfItIsTheFirstTimeTheInfoIsCollected() {
    collector.initialize(registry);

    Gauge<Long> gauge = bytesUploadedCaptor.getValue();

    assertNull(gauge.getValue());
  }

  @Test public void doesNotReportBytesDownloadedIfItIsTheFirstTimeTheInfoIsCollected() {
    collector.initialize(registry);

    Gauge<Long> gauge = bytesDownloadedCaptor.getValue();

    assertNull(gauge.getValue());
  }
}
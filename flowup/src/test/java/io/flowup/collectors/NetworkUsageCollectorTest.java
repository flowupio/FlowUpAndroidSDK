/*
 * Copyright (C) 2016 Go Karumi S.L.
 */

package io.flowup.collectors;

import com.codahale.metrics.Gauge;
import com.codahale.metrics.MetricRegistry;
import io.flowup.android.AppTrafficStats;
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

  private static final int ANY_AMOUNT_OF_BYTES = 1024;

  private NetworkUsageCollector collector;

  @Mock private MetricRegistry registry;
  @Mock private AppTrafficStats trafficStats;
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
    collector =
        new NetworkUsageCollector(trafficStats, generator, anySamplingInterval, anyTimeUnit);
  }

  @Test public void doesNotReportBytesUploadedIfItIsTheFirstTimeTheInfoIsCollected() {
    givenTheUploadedBytesAre(ANY_AMOUNT_OF_BYTES);
    collector.initialize(registry);

    Gauge<Long> gauge = bytesUploadedCaptor.getValue();

    assertNull(gauge.getValue());
  }

  @Test public void doesNotReportBytesDownloadedIfItIsTheFirstTimeTheInfoIsCollected() {
    givenTheDownloadedBytesAre(ANY_AMOUNT_OF_BYTES);
    collector.initialize(registry);

    Gauge<Long> gauge = bytesDownloadedCaptor.getValue();

    assertNull(gauge.getValue());
  }

  private void givenTheDownloadedBytesAre(long bytes) {
    when(trafficStats.getRxBytes()).thenReturn(bytes);
  }

  private void givenTheUploadedBytesAre(long bytes) {
    when(trafficStats.getTxBytes()).thenReturn(bytes);
  }
}
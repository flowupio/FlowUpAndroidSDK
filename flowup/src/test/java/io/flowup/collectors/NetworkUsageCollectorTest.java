/*
 * Copyright (C) 2016 Go Karumi S.L.
 */

package io.flowup.collectors;

import com.codahale.metrics.Gauge;
import com.codahale.metrics.MetricRegistry;
import io.flowup.android.App;
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

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class) public class NetworkUsageCollectorTest {

  private static final long ANY_AMOUNT_OF_BYTES = 1024;
  private static final long ANY_DELTA = 128;
  private static final double COMPARATION_DELTA = 0.1d;

  private NetworkUsageCollector collector;

  @Mock private MetricRegistry registry;
  @Mock private AppTrafficStats trafficStats;
  @Captor private ArgumentCaptor<Gauge<Long>> bytesUploadedCaptor;
  @Captor private ArgumentCaptor<Gauge<Long>> bytesDownloadedCaptor;
  @Mock private App app;

  @Before public void setUp() {
    MetricNamesGenerator generator =
        new MetricNamesGenerator(new AnyApp(), new AnyDevice(), new Time());
    when(registry.register(eq(generator.getBytesUploadedMetricName(false)),
        bytesUploadedCaptor.capture())).thenReturn(mock(Gauge.class));
    when(registry.register(eq(generator.getBytesUploadedMetricName(true)),
        bytesUploadedCaptor.capture())).thenReturn(mock(Gauge.class));
    when(registry.register(eq(generator.getBytesDownloadedMetricName(false)),
        bytesDownloadedCaptor.capture())).thenReturn(mock(Gauge.class));
    when(registry.register(eq(generator.getBytesDownloadedMetricName(true)),
        bytesDownloadedCaptor.capture())).thenReturn(mock(Gauge.class));
    TimeUnit anyTimeUnit = TimeUnit.NANOSECONDS;
    int anySamplingInterval = 0;
    collector =
        new NetworkUsageCollector(trafficStats, generator, anySamplingInterval, anyTimeUnit, app);
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

  @Test public void collectsTheDifferenceOfBytesUploadedBetweenTwoGetValueCalls() {
    givenTheUploadedBytesAre(ANY_AMOUNT_OF_BYTES);
    collector.initialize(registry);

    Gauge<Long> gauge = bytesUploadedCaptor.getValue();
    gauge.getValue();
    givenTheUploadedBytesAre(ANY_AMOUNT_OF_BYTES + ANY_DELTA);
    Long expectedValue = gauge.getValue();

    assertEquals(ANY_DELTA, expectedValue, COMPARATION_DELTA);
  }

  @Test public void collectsTheDifferenceOfBytesDownloadedBetweenTwoGetValueCalls() {
    givenTheDownloadedBytesAre(ANY_AMOUNT_OF_BYTES);
    collector.initialize(registry);

    Gauge<Long> gauge = bytesDownloadedCaptor.getValue();
    gauge.getValue();
    givenTheDownloadedBytesAre(ANY_AMOUNT_OF_BYTES + ANY_DELTA);
    Long expectedValue = gauge.getValue();

    assertEquals(ANY_DELTA, expectedValue, COMPARATION_DELTA);
  }

  private void givenTheDownloadedBytesAre(long bytes) {
    when(trafficStats.getRxBytes()).thenReturn(bytes);
  }

  private void givenTheUploadedBytesAre(long bytes) {
    when(trafficStats.getTxBytes()).thenReturn(bytes);
  }
}
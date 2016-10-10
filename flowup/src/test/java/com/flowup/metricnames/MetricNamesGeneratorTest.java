/*
 * Copyright (C) 2016 Go Karumi S.L.
 */

package com.flowup.metricnames;

import android.app.Activity;
import com.flowup.doubles.AnyApp;
import com.flowup.doubles.AnyDevice;
import com.flowup.utils.MetricNameUtils;
import com.flowup.utils.Time;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static com.flowup.utils.MetricNameUtils.replaceDashes;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class) public class MetricNamesGeneratorTest {

  private static final long ANY_TIMESTAMP = 12345678;

  @Mock private Time time;
  @Mock private Activity activity;

  private Device device;
  private App app;
  private MetricNamesGenerator generator;
  private MetricNamesExtractor extractor;

  @Before public void setUp() {
    device = new AnyDevice();
    app = new AnyApp();
    generator = new MetricNamesGenerator(app, device, time);
    extractor = new MetricNamesExtractor();
  }

  @Test public void fpsMetricNameShouldContainExactly13FieldsSeparatedByDots() {
    String fps = generator.getFPSMetricName(activity);

    assertEquals(13, MetricNameUtils.split(fps).length);
  }

  @Test public void fpsMetricNameShouldContainTheCrossMetricInfoName() {
    String fps = generator.getFPSMetricName(activity);

    assertContainsCrossMetricInfoName(fps);
  }

  @Test public void fpsMetricNameShouldContainActivityNameAsScreenName() {
    String fps = generator.getFPSMetricName(activity);

    String activityName = extractor.getScreenName(fps);

    assertEquals(activityName, activity.getClass().getSimpleName());
  }

  @Test public void fpsMetricNameShouldContainTheGenerationTime() {
    givenNowIs(ANY_TIMESTAMP);
    String fps = generator.getFPSMetricName(activity);

    long timestamp = extractor.getTimestamp(fps);

    assertEquals(ANY_TIMESTAMP, timestamp);
  }

  @Test public void fpsContainsTheMetricNameInTheCorrectPosition() {
    String fps = generator.getFPSMetricName(activity);

    String[] parts = MetricNameUtils.split(fps);
    assertEquals("ui", parts[9]);
    assertEquals("fps", parts[10]);
  }

  @Test public void frameTimeMetricNameShouldContainExactly13FieldsSeparatedByDots() {
    String frameTime = generator.getFrameTimeMetricName(activity);

    assertEquals(13, MetricNameUtils.split(frameTime).length);
  }

  @Test public void frameTimeMetricNameShouldContainTheCrossMetricInfoName() {
    String frameTime = generator.getFrameTimeMetricName(activity);

    assertContainsCrossMetricInfoName(frameTime);
  }

  @Test public void frameTimeMetricNameShouldContainActivityNameAsScreenName() {
    String frameTime = generator.getFrameTimeMetricName(activity);

    String activityName = extractor.getScreenName(frameTime);

    assertEquals(activityName, activity.getClass().getSimpleName());
  }

  @Test public void frameTimeMetricNameShouldContainTheGenerationTime() {
    givenNowIs(ANY_TIMESTAMP);
    String frameTime = generator.getFrameTimeMetricName(activity);

    long timestamp = extractor.getTimestamp(frameTime);

    assertEquals(ANY_TIMESTAMP, timestamp);
  }

  @Test public void frameTimeContainsTheMetricNameInTheCorrectPosition() {
    String frameTime = generator.getFrameTimeMetricName(activity);

    String[] parts = MetricNameUtils.split(frameTime);
    assertEquals("ui", parts[9]);
    assertEquals("frameTime", parts[10]);
  }

  @Test public void bytesDownloadedMetricShouldContainExactly11FieldsSeparatedByDots() {
    String bytesDownloaded = generator.getBytesDownloadedMetricsName();

    assertEquals(11, MetricNameUtils.split(bytesDownloaded).length);
  }

  @Test public void bytesDownloadedShouldContainTheCrossMetricInfoInTheName() {
    String bytesDownloaded = generator.getBytesDownloadedMetricsName();

    assertContainsCrossMetricInfoName(bytesDownloaded);
  }

  @Test public void bytesDownloadedContainsTheMetricNameInTheCorrectPosition() {
    String bytesDownloaded = generator.getBytesDownloadedMetricsName();

    String[] parts = MetricNameUtils.split(bytesDownloaded);
    assertEquals("network", parts[9]);
    assertEquals("bytesDownloaded", parts[10]);
  }

  @Test public void bytesUploadedMetricShouldContainExactly11FieldsSeparatedByDots() {
    String bytesUploaded = generator.getBytesUploadedMetricsName();

    assertEquals(11, MetricNameUtils.split(bytesUploaded).length);
  }

  @Test public void bytesUploadedShouldContainTheCrossMetricInfoInTheName() {
    String bytesUploaded = generator.getBytesUploadedMetricsName();

    assertContainsCrossMetricInfoName(bytesUploaded);
  }

  @Test public void bytesUploadedContainsTheMetricNameInTheCorrectPosition() {
    String bytesUploaded = generator.getBytesUploadedMetricsName();

    String[] parts = MetricNameUtils.split(bytesUploaded);
    assertEquals("network", parts[9]);
    assertEquals("bytesUploaded", parts[10]);
  }

  @Test
  public void identifiesAFPSMetricProperly() {
    String fps = generator.getFPSMetricName(activity);

    assertTrue(extractor.isFPSMetric(fps));
  }

  @Test
  public void doesNotIdentifyAFPSMetricAsAFrameTimeMetric() {
    String fps = generator.getFPSMetricName(activity);

    assertFalse(extractor.isFrameTimeMetric(fps));
  }

  @Test
  public void identifiesAFrameTimeMetricProperly() {
    String frameTime = generator.getFrameTimeMetricName(activity);

    assertTrue(extractor.isFrameTimeMetric(frameTime));
  }

  @Test
  public void doesNotIdentifyAFrameTimeMetricAsAFPSMetric() {
    String frameTime = generator.getFrameTimeMetricName(activity);

    assertFalse(extractor.isFPSMetric(frameTime));
  }

  @Test
  public void identifiesABytesDownloadedMetricProperly() {
    String bytesDownloaded = generator.getBytesDownloadedMetricsName();

    assertTrue(extractor.isBytesDownloadedMetric(bytesDownloaded));
  }

  @Test
  public void doesNotIdentifyABytesDownloadedMetricAsABytesUploaded() {
    String bytesDownloaded = generator.getBytesDownloadedMetricsName();

    assertFalse(extractor.isBytesUploadedMetric(bytesDownloaded));
  }

  @Test
  public void identifiesABytesUploadedMetricProperly() {
    String bytesUploaded = generator.getBytesUploadedMetricsName();

    assertTrue(extractor.isBytesUploadedMetric(bytesUploaded));
  }

  @Test
  public void doesNotIdentifyABytesUploadedMetricAsABytesDownloaded() {
    String bytesUploaded = generator.getBytesUploadedMetricsName();

    assertFalse(extractor.isBytesDownloadedMetric(bytesUploaded));
  }

  private void assertContainsCrossMetricInfoName(String metricName) {
    assertEquals(replaceDashes(app.getAppPackageName()), extractor.getAppPackage(metricName));
    assertEquals(device.getInstallationUUID(), extractor.getInstallationUUID(metricName));
    assertEquals(device.getModel(), extractor.getDeviceModel(metricName));
    assertEquals(device.getNumberOfCores(), extractor.getNumberOfCores(metricName));
    assertEquals(device.getScreenDensity(), extractor.getScreenDensity(metricName));
    assertEquals(device.getOSVersion(), extractor.getOSVersion(metricName));
    assertEquals(replaceDashes(app.getVersionName()), extractor.getVersionName(metricName));
    assertEquals(device.isBatterySaverOn(), extractor.getIsBatterSaverOn(metricName));
  }

  private void givenNowIs(long timestamp) {
    when(time.now()).thenReturn(timestamp);
  }
}
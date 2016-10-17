/*
 * Copyright (C) 2016 Go Karumi S.L.
 */

package io.flowup.metricnames;

import android.app.Activity;
import io.flowup.android.App;
import io.flowup.android.Device;
import io.flowup.doubles.AnyApp;
import io.flowup.doubles.AnyDevice;
import io.flowup.utils.MetricNameUtils;
import io.flowup.utils.Time;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static io.flowup.utils.MetricNameUtils.replaceDashes;
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
    String bytesDownloaded = generator.getBytesDownloadedMetricName();

    assertEquals(11, MetricNameUtils.split(bytesDownloaded).length);
  }

  @Test public void bytesDownloadedShouldContainTheCrossMetricInfoInTheName() {
    String bytesDownloaded = generator.getBytesDownloadedMetricName();

    assertContainsCrossMetricInfoName(bytesDownloaded);
  }

  @Test public void bytesDownloadedContainsTheMetricNameInTheCorrectPosition() {
    String bytesDownloaded = generator.getBytesDownloadedMetricName();

    String[] parts = MetricNameUtils.split(bytesDownloaded);
    assertEquals("network", parts[9]);
    assertEquals("bytesDownloaded", parts[10]);
  }

  @Test public void bytesUploadedMetricShouldContainExactly11FieldsSeparatedByDots() {
    String bytesUploaded = generator.getBytesUploadedMetricName();

    assertEquals(11, MetricNameUtils.split(bytesUploaded).length);
  }

  @Test public void bytesUploadedShouldContainTheCrossMetricInfoInTheName() {
    String bytesUploaded = generator.getBytesUploadedMetricName();

    assertContainsCrossMetricInfoName(bytesUploaded);
  }

  @Test public void bytesUploadedContainsTheMetricNameInTheCorrectPosition() {
    String bytesUploaded = generator.getBytesUploadedMetricName();

    String[] parts = MetricNameUtils.split(bytesUploaded);
    assertEquals("network", parts[9]);
    assertEquals("bytesUploaded", parts[10]);
  }

  @Test public void identifiesAFPSMetricProperly() {
    String fps = generator.getFPSMetricName(activity);

    assertTrue(extractor.isFPSMetric(fps));
  }

  @Test public void doesNotIdentifyAFPSMetricAsAFrameTimeMetric() {
    String fps = generator.getFPSMetricName(activity);

    assertFalse(extractor.isFrameTimeMetric(fps));
  }

  @Test public void identifiesAFrameTimeMetricProperly() {
    String frameTime = generator.getFrameTimeMetricName(activity);

    assertTrue(extractor.isFrameTimeMetric(frameTime));
  }

  @Test public void doesNotIdentifyAFrameTimeMetricAsAFPSMetric() {
    String frameTime = generator.getFrameTimeMetricName(activity);

    assertFalse(extractor.isFPSMetric(frameTime));
  }

  @Test public void identifiesABytesDownloadedMetricProperly() {
    String bytesDownloaded = generator.getBytesDownloadedMetricName();

    assertTrue(extractor.isBytesDownloadedMetric(bytesDownloaded));
  }

  @Test public void doesNotIdentifyABytesDownloadedMetricAsABytesUploaded() {
    String bytesDownloaded = generator.getBytesDownloadedMetricName();

    assertFalse(extractor.isBytesUploadedMetric(bytesDownloaded));
  }

  @Test public void identifiesABytesUploadedMetricProperly() {
    String bytesUploaded = generator.getBytesUploadedMetricName();

    assertTrue(extractor.isBytesUploadedMetric(bytesUploaded));
  }

  @Test public void doesNotIdentifyABytesUploadedMetricAsABytesDownloaded() {
    String bytesUploaded = generator.getBytesUploadedMetricName();

    assertFalse(extractor.isBytesDownloadedMetric(bytesUploaded));
  }

  @Test public void cpuUsageMetricNameShouldContainExactly10FieldsSeparatedByDots() {
    String cpuUsage = generator.getCPUUsageMetricName();

    assertEquals(10, MetricNameUtils.split(cpuUsage).length);
  }

  @Test public void cpuUsageMetricNameShouldContainTheCrossMetricInfoName() {
    String cpuUsage = generator.getCPUUsageMetricName();

    assertContainsCrossMetricInfoName(cpuUsage);
  }

  @Test public void cpuUsageContainsTheMetricNameInTheCorrectPosition() {
    String cpuUsage = generator.getCPUUsageMetricName();

    String[] parts = MetricNameUtils.split(cpuUsage);
    assertEquals("cpuUsage", parts[9]);
  }

  @Test public void memoryUsageMetricNameShouldContainExactly11FieldsSeparatedByDots() {
    String memoryUsage = generator.getMemoryUsageMetricName();

    assertEquals(11, MetricNameUtils.split(memoryUsage).length);
  }

  @Test public void memoryUsageUsageMetricNameShouldContainTheCrossMetricInfoName() {
    String memoryUsage = generator.getMemoryUsageMetricName();

    assertContainsCrossMetricInfoName(memoryUsage);
  }

  @Test public void memoryUsageContainsTheMetricNameInTheCorrectPosition() {
    String memoryUsage = generator.getMemoryUsageMetricName();

    String[] parts = MetricNameUtils.split(memoryUsage);
    assertEquals("memoryUsage", parts[10]);
  }

  @Test public void identifiesAMemoryUsageMetricProperly() {
    String memoryUsage = generator.getMemoryUsageMetricName();

    assertTrue(extractor.isMemoryUsageMetric(memoryUsage));
  }

  @Test public void doesNotIdentifyABytesAllocatedMetricAsMemoryUsage() {
    String bytesAllocated = generator.getBytesAllocatedMetricName();

    assertFalse(extractor.isMemoryUsageMetric(bytesAllocated));
  }

  @Test public void bytesAllocatedMetricNameShouldContainExactly11FieldsSeparatedByDots() {
    String bytesAllocated = generator.getBytesAllocatedMetricName();

    assertEquals(11, MetricNameUtils.split(bytesAllocated).length);
  }

  @Test public void bytesAllocatedUsageUsageMetricNameShouldContainTheCrossMetricInfoName() {
    String bytesAllocated = generator.getBytesAllocatedMetricName();

    assertContainsCrossMetricInfoName(bytesAllocated);
  }

  @Test public void bytesAllocatedUsageContainsTheMetricNameInTheCorrectPosition() {
    String bytesAllocated = generator.getBytesAllocatedMetricName();

    String[] parts = MetricNameUtils.split(bytesAllocated);
    assertEquals("bytesAllocated", parts[10]);
  }

  @Test public void identifiesABytesAllocatedMetricProperly() {
    String bytesAllocated = generator.getBytesAllocatedMetricName();

    assertTrue(extractor.isBytesAllocatedMetric(bytesAllocated));
  }

  @Test public void doesNotIdentifyAMemoryUsageMetricAsBytesAllocated() {
    String memoryUsage = generator.getMemoryUsageMetricName();

    assertFalse(extractor.isBytesAllocatedMetric(memoryUsage));
  }

  @Test
  public void internalStorageWrittenBytesMetricNameShouldContainExactly11FieldsSeparatedByDots() {
    String writtenBytes = generator.getInternalStorageWrittenBytes();

    assertEquals(11, MetricNameUtils.split(writtenBytes).length);
  }

  @Test public void internalStorageWrittenBytesMetricNameShouldContainTheCrossMetricInfoName() {
    String writtenBytes = generator.getInternalStorageWrittenBytes();

    assertContainsCrossMetricInfoName(writtenBytes);
  }

  @Test public void internalStorageWrittenBytesContainsTheMetricNameInTheCorrectPosition() {
    String writtenBytes = generator.getInternalStorageWrittenBytes();

    String[] parts = MetricNameUtils.split(writtenBytes);
    assertEquals("internalStorageWrittenBytes", parts[10]);
  }

  @Test public void identifiesAInternalStorageWrittenBytesMetricProperly() {
    String writtenBytes = generator.getInternalStorageWrittenBytes();

    assertTrue(extractor.isInternalStorageAllocatedBytesMetric(writtenBytes));
  }

  @Test public void doesNotIdentifyAnInternalStorageWrittenBytesMetricAsSharedPrefsWrittenBytes() {
    String writtenBytes = generator.getSharedPreferencesWrittenBytes();

    assertFalse(extractor.isBytesAllocatedMetric(writtenBytes));
  }

  @Test public void sharedPrefsWrittenBytesMetricNameShouldContainExactly11FieldsSeparatedByDots() {
    String writtenBytes = generator.getSharedPreferencesWrittenBytes();

    assertEquals(11, MetricNameUtils.split(writtenBytes).length);
  }

  @Test public void sharedPrefsWrittenBytesMetricNameShouldContainTheCrossMetricInfoName() {
    String writtenBytes = generator.getSharedPreferencesWrittenBytes();

    assertContainsCrossMetricInfoName(writtenBytes);
  }

  @Test public void sharedPrefsWrittenBytesContainsTheMetricNameInTheCorrectPosition() {
    String writtenBytes = generator.getSharedPreferencesWrittenBytes();

    String[] parts = MetricNameUtils.split(writtenBytes);
    assertEquals("sharedPreferencesStorageWrittenBytes", parts[10]);
  }

  @Test public void identifiesASharedPrefsWrittenBytesMetricProperly() {
    String writtenBytes = generator.getSharedPreferencesWrittenBytes();

    assertTrue(extractor.isSharedPreferencesAllocatedBytesMetric(writtenBytes));
  }

  @Test public void doesNotIdentifyASharedPrefsWrittenBytesMetricAsInternalStorageWrittenBytes() {
    String writtenBytes = generator.getSharedPreferencesWrittenBytes();

    assertFalse(extractor.isInternalStorageAllocatedBytesMetric(writtenBytes));
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
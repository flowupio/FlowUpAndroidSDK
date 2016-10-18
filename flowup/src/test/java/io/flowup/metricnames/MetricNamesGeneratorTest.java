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

  @Test public void onActivityCreatedMetricNameShouldContainExactly12FieldsSeparatedByDots() {
    String name = generator.getOnActivityCreatedMetricName(activity);

    assertEquals(12, MetricNameUtils.split(name).length);
  }

  @Test public void onActivityCreatedMetricNameShouldContainTheCrossMetricInfoName() {
    String name = generator.getOnActivityCreatedMetricName(activity);

    assertContainsCrossMetricInfoName(name);
  }

  @Test public void onActivityCreatedContainsTheMetricNameInTheCorrectPosition() {
    String name = generator.getOnActivityCreatedMetricName(activity);

    String[] parts = MetricNameUtils.split(name);
    assertEquals("onActivityCreated", parts[10]);
  }

  @Test public void identifiesAnOnActivityCreatedMetricProperly() {
    String name = generator.getOnActivityCreatedMetricName(activity);

    assertTrue(extractor.isOnActivityCreatedMetric(name));
  }

  @Test public void doesNotIdentifyAnOnActivityResumedMetricAsAnOnActivityCreatedMetric() {
    String name = generator.getOnActivityResumedMetricName(activity);

    assertFalse(extractor.isOnActivityCreatedMetric(name));
  }

  @Test public void onActivityCreatedMetricContainsActivityNameAsTheLastParam() {
    String name = generator.getOnActivityCreatedMetricName(activity);

    String[] parts = MetricNameUtils.split(name);
    assertEquals(activity.getClass().getSimpleName(), parts[11]);
  }

  @Test public void onActivityResumedMetricNameShouldContainExactly12FieldsSeparatedByDots() {
    String name = generator.getOnActivityResumedMetricName(activity);

    assertEquals(12, MetricNameUtils.split(name).length);
  }

  @Test public void onActivityResumedMetricNameShouldContainTheCrossMetricInfoName() {
    String name = generator.getOnActivityResumedMetricName(activity);

    assertContainsCrossMetricInfoName(name);
  }

  @Test public void onActivityResumedContainsTheMetricNameInTheCorrectPosition() {
    String name = generator.getOnActivityResumedMetricName(activity);

    String[] parts = MetricNameUtils.split(name);
    assertEquals("onActivityResumed", parts[10]);
  }

  @Test public void identifiesAnOnActivityResumedMetricProperly() {
    String name = generator.getOnActivityResumedMetricName(activity);

    assertTrue(extractor.isOnActivityResumedMetric(name));
  }

  @Test public void doesNotIdentifyAnOnActivityCreatedMetricAsAnOnActivityResumedMetric() {
    String name = generator.getOnActivityCreatedMetricName(activity);

    assertFalse(extractor.isOnActivityResumedMetric(name));
  }

  @Test public void onActivityResumedContainsActivityNameAsTheLastParam() {
    String name = generator.getOnActivityResumedMetricName(activity);

    String[] parts = MetricNameUtils.split(name);
    assertEquals(activity.getClass().getSimpleName(), parts[11]);
  }

  @Test public void onActivityStartedMetricNameShouldContainExactly12FieldsSeparatedByDots() {
    String name = generator.getOnActivityStartedMetricName(activity);

    assertEquals(12, MetricNameUtils.split(name).length);
  }

  @Test public void onActivityStartedMetricNameShouldContainTheCrossMetricInfoName() {
    String name = generator.getOnActivityStartedMetricName(activity);

    assertContainsCrossMetricInfoName(name);
  }

  @Test public void onActivityStartedContainsTheMetricNameInTheCorrectPosition() {
    String name = generator.getOnActivityStartedMetricName(activity);

    String[] parts = MetricNameUtils.split(name);
    assertEquals("onActivityStarted", parts[10]);
  }

  @Test public void identifiesAnOnActivityStartedMetricProperly() {
    String name = generator.getOnActivityStartedMetricName(activity);

    assertTrue(extractor.isOnActivityStartedMetric(name));
  }

  @Test public void doesNotIdentifyAnOnActivityCreatedMetricAsAnOnActivityStartedMetric() {
    String name = generator.getOnActivityStartedMetricName(activity);

    assertFalse(extractor.isOnActivityCreatedMetric(name));
  }

  @Test public void onActivityStartedContainsActivityNameAsTheLastParam() {
    String name = generator.getOnActivityStartedMetricName(activity);

    String[] parts = MetricNameUtils.split(name);
    assertEquals(activity.getClass().getSimpleName(), parts[11]);
  }

  @Test public void activityVisibleMetricNameShouldContainExactly12FieldsSeparatedByDots() {
    String name = generator.getActivityVisibleMetricName(activity);

    assertEquals(12, MetricNameUtils.split(name).length);
  }

  @Test public void activityVisibleMetricNameShouldContainTheCrossMetricInfoName() {
    String name = generator.getActivityVisibleMetricName(activity);

    assertContainsCrossMetricInfoName(name);
  }

  @Test public void activityVisibleMetricContainsTheMetricNameInTheCorrectPosition() {
    String name = generator.getActivityVisibleMetricName(activity);

    String[] parts = MetricNameUtils.split(name);
    assertEquals("activityVisible", parts[10]);
  }

  @Test public void identifiesAnActivityVisibleMetricProperly() {
    String name = generator.getActivityVisibleMetricName(activity);

    assertTrue(extractor.isActivityVisibleMetric(name));
  }

  @Test public void doesNotIdentifyAnOnActivityCreatedMetricAsAnActivityVisibleMetric() {
    String name = generator.getOnActivityCreatedMetricName(activity);

    assertFalse(extractor.isActivityVisibleMetric(name));
  }

  @Test public void activityVisibleContainsActivityNameAsTheLastParam() {
    String name = generator.getActivityVisibleMetricName(activity);

    String[] parts = MetricNameUtils.split(name);
    assertEquals(activity.getClass().getSimpleName(), parts[11]);
  }

  @Test public void onActivityPausedMetricNameShouldContainExactly12FieldsSeparatedByDots() {
    String name = generator.getOnActivityPausedMetricName(activity);

    assertEquals(12, MetricNameUtils.split(name).length);
  }

  @Test public void onActivityPausedMetricNameShouldContainTheCrossMetricInfoName() {
    String name = generator.getOnActivityPausedMetricName(activity);

    assertContainsCrossMetricInfoName(name);
  }

  @Test public void onActivityPausedMetricContainsTheMetricNameInTheCorrectPosition() {
    String name = generator.getOnActivityPausedMetricName(activity);

    String[] parts = MetricNameUtils.split(name);
    assertEquals("onActivityPaused", parts[10]);
  }

  @Test public void identifiesAnOnActivityPausedMetricProperly() {
    String name = generator.getOnActivityPausedMetricName(activity);

    assertTrue(extractor.isOnActivityPausedMetric(name));
  }

  @Test public void doesNotIdentifyAnOnActivityCreatedMetricAsAnOnActivityPaused() {
    String name = generator.getOnActivityCreatedMetricName(activity);

    assertFalse(extractor.isOnActivityPausedMetric(name));
  }

  @Test public void onActivityPausedContainsActivityNameAsTheLastParam() {
    String name = generator.getActivityVisibleMetricName(activity);

    String[] parts = MetricNameUtils.split(name);
    assertEquals(activity.getClass().getSimpleName(), parts[11]);
  }

  @Test public void onActivityStoppedMetricNameShouldContainExactly12FieldsSeparatedByDots() {
    String name = generator.getOnActivityStoppedMetricName(activity);

    assertEquals(12, MetricNameUtils.split(name).length);
  }

  @Test public void onActivityStoppedMetricNameShouldContainTheCrossMetricInfoName() {
    String name = generator.getOnActivityStoppedMetricName(activity);

    assertContainsCrossMetricInfoName(name);
  }

  @Test public void onActivityStoppedMetricContainsTheMetricNameInTheCorrectPosition() {
    String name = generator.getOnActivityStoppedMetricName(activity);

    String[] parts = MetricNameUtils.split(name);
    assertEquals("onActivityStopped", parts[10]);
  }

  @Test public void identifiesAnOnActivityStoppedMetricProperly() {
    String name = generator.getOnActivityStoppedMetricName(activity);

    assertTrue(extractor.isOnActivityStoppedMetric(name));
  }

  @Test public void doesNotIdentifyAnOnActivityStoppedMetricAsAnOnActivityPaused() {
    String name = generator.getOnActivityCreatedMetricName(activity);

    assertFalse(extractor.isOnActivityStoppedMetric(name));
  }

  @Test public void onActivityStoppedContainsActivityNameAsTheLastParam() {
    String name = generator.getOnActivityStoppedMetricName(activity);

    String[] parts = MetricNameUtils.split(name);
    assertEquals(activity.getClass().getSimpleName(), parts[11]);
  }

  @Test public void onActivityDestroyedMetricNameShouldContainExactly12FieldsSeparatedByDots() {
    String name = generator.getOnActivityDestroyedMetricName(activity);

    assertEquals(12, MetricNameUtils.split(name).length);
  }

  @Test public void onActivityDestroyedMetricNameShouldContainTheCrossMetricInfoName() {
    String name = generator.getOnActivityDestroyedMetricName(activity);

    assertContainsCrossMetricInfoName(name);
  }

  @Test public void onActivityDestroyedMetricContainsTheMetricNameInTheCorrectPosition() {
    String name = generator.getOnActivityDestroyedMetricName(activity);

    String[] parts = MetricNameUtils.split(name);
    assertEquals("onActivityDestroyed", parts[10]);
  }

  @Test public void identifiesAnOnActivityDestroyedMetricProperly() {
    String name = generator.getOnActivityDestroyedMetricName(activity);

    assertTrue(extractor.isOnActivityDestroyedMetric(name));
  }

  @Test public void doesNotIdentifyAnOnActivityStoppedMetricAsAnOnActivityDestroyed() {
    String name = generator.getOnActivityCreatedMetricName(activity);

    assertFalse(extractor.isOnActivityDestroyedMetric(name));
  }

  @Test public void onActivityDestroyedContainsActivityNameAsTheLastParam() {
    String name = generator.getOnActivityDestroyedMetricName(activity);

    String[] parts = MetricNameUtils.split(name);
    assertEquals(activity.getClass().getSimpleName(), parts[11]);
  }

  @Test public void returnsTrueJustWithUIMetrics() {
    String fps = generator.getFPSMetricName(activity);
    String frameTime = generator.getFPSMetricName(activity);
    String activityCreated = generator.getFPSMetricName(activity);
    String activityStarted = generator.getFPSMetricName(activity);
    String activityResumed = generator.getFPSMetricName(activity);
    String activityVisible = generator.getFPSMetricName(activity);
    String activityPaused = generator.getFPSMetricName(activity);
    String activityStopped = generator.getFPSMetricName(activity);
    String activityDestroyed = generator.getFPSMetricName(activity);
    String nonUIMetricName = generator.getBytesAllocatedMetricName();

    assertTrue(extractor.isUIMetric(fps));
    assertTrue(extractor.isUIMetric(frameTime));
    assertTrue(extractor.isUIMetric(activityCreated));
    assertTrue(extractor.isUIMetric(activityStarted));
    assertTrue(extractor.isUIMetric(activityResumed));
    assertTrue(extractor.isUIMetric(activityVisible));
    assertTrue(extractor.isUIMetric(activityPaused));
    assertTrue(extractor.isUIMetric(activityStopped));
    assertTrue(extractor.isUIMetric(activityDestroyed));
    assertFalse(extractor.isUIMetric(nonUIMetricName));
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
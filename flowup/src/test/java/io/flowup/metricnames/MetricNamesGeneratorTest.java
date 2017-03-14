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
  private static final boolean ANY_IN_IN_BACKGROUND_VALUE = false;

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

  @Test public void frameTimeMetricNameShouldContainExactly13FieldsSeparatedByDots() {
    String frameTime = generator.getFrameTimeMetricName(activity);

    assertEquals(14, MetricNameUtils.split(frameTime).length);
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
    assertEquals("ui", parts[10]);
    assertEquals("frameTime", parts[11]);
  }

  @Test public void bytesDownloadedMetricShouldContainExactly11FieldsSeparatedByDots() {
    String bytesDownloaded = generator.getBytesDownloadedMetricName(ANY_IN_IN_BACKGROUND_VALUE);

    assertEquals(12, MetricNameUtils.split(bytesDownloaded).length);
  }

  @Test public void bytesDownloadedShouldContainTheCrossMetricInfoInTheName() {
    String bytesDownloaded = generator.getBytesDownloadedMetricName(ANY_IN_IN_BACKGROUND_VALUE);

    assertContainsCrossMetricInfoName(bytesDownloaded);
  }

  @Test public void bytesDownloadedContainsTheMetricNameInTheCorrectPosition() {
    String bytesDownloaded = generator.getBytesDownloadedMetricName(ANY_IN_IN_BACKGROUND_VALUE);

    String[] parts = MetricNameUtils.split(bytesDownloaded);
    assertEquals("network", parts[10]);
    assertEquals("bytesDownloaded", parts[11]);
  }

  @Test public void bytesUploadedMetricShouldContainExactly11FieldsSeparatedByDots() {
    String bytesUploaded = generator.getBytesUploadedMetricName(ANY_IN_IN_BACKGROUND_VALUE);

    assertEquals(12, MetricNameUtils.split(bytesUploaded).length);
  }

  @Test public void bytesUploadedShouldContainTheCrossMetricInfoInTheName() {
    String bytesUploaded = generator.getBytesUploadedMetricName(ANY_IN_IN_BACKGROUND_VALUE);

    assertContainsCrossMetricInfoName(bytesUploaded);
  }

  @Test public void bytesUploadedContainsTheMetricNameInTheCorrectPosition() {
    String bytesUploaded = generator.getBytesUploadedMetricName(ANY_IN_IN_BACKGROUND_VALUE);

    String[] parts = MetricNameUtils.split(bytesUploaded);
    assertEquals("network", parts[10]);
    assertEquals("bytesUploaded", parts[11]);
  }

  @Test public void identifiesAFrameTimeMetricProperly() {
    String frameTime = generator.getFrameTimeMetricName(activity);

    assertTrue(extractor.isFrameTimeMetric(frameTime));
  }

  @Test public void identifiesABytesDownloadedMetricProperly() {
    String bytesDownloaded = generator.getBytesDownloadedMetricName(ANY_IN_IN_BACKGROUND_VALUE);

    assertTrue(extractor.isBytesDownloadedMetric(bytesDownloaded));
  }

  @Test public void doesNotIdentifyABytesDownloadedMetricAsABytesUploaded() {
    String bytesDownloaded = generator.getBytesDownloadedMetricName(ANY_IN_IN_BACKGROUND_VALUE);

    assertFalse(extractor.isBytesUploadedMetric(bytesDownloaded));
  }

  @Test public void identifiesABytesUploadedMetricProperly() {
    String bytesUploaded = generator.getBytesUploadedMetricName(ANY_IN_IN_BACKGROUND_VALUE);

    assertTrue(extractor.isBytesUploadedMetric(bytesUploaded));
  }

  @Test public void doesNotIdentifyABytesUploadedMetricAsABytesDownloaded() {
    String bytesUploaded = generator.getBytesUploadedMetricName(ANY_IN_IN_BACKGROUND_VALUE);

    assertFalse(extractor.isBytesDownloadedMetric(bytesUploaded));
  }

  @Test public void cpuUsageMetricNameShouldContainExactly10FieldsSeparatedByDots() {
    String cpuUsage = generator.getCPUUsageMetricName(ANY_IN_IN_BACKGROUND_VALUE);

    assertEquals(11, MetricNameUtils.split(cpuUsage).length);
  }

  @Test public void cpuUsageMetricNameShouldContainTheCrossMetricInfoName() {
    String cpuUsage = generator.getCPUUsageMetricName(ANY_IN_IN_BACKGROUND_VALUE);

    assertContainsCrossMetricInfoName(cpuUsage);
  }

  @Test public void cpuUsageContainsTheMetricNameInTheCorrectPosition() {
    String cpuUsage = generator.getCPUUsageMetricName(ANY_IN_IN_BACKGROUND_VALUE);

    String[] parts = MetricNameUtils.split(cpuUsage);
    assertEquals("cpuUsage", parts[10]);
  }

  @Test public void memoryUsageMetricNameShouldContainExactly11FieldsSeparatedByDots() {
    String memoryUsage = generator.getMemoryUsageMetricName(ANY_IN_IN_BACKGROUND_VALUE);

    assertEquals(12, MetricNameUtils.split(memoryUsage).length);
  }

  @Test public void memoryUsageUsageMetricNameShouldContainTheCrossMetricInfoName() {
    String memoryUsage = generator.getMemoryUsageMetricName(ANY_IN_IN_BACKGROUND_VALUE);

    assertContainsCrossMetricInfoName(memoryUsage);
  }

  @Test public void memoryUsageContainsTheMetricNameInTheCorrectPosition() {
    String memoryUsage = generator.getMemoryUsageMetricName(ANY_IN_IN_BACKGROUND_VALUE);

    String[] parts = MetricNameUtils.split(memoryUsage);
    assertEquals("memoryUsage", parts[11]);
  }

  @Test public void identifiesAMemoryUsageMetricProperly() {
    String memoryUsage = generator.getMemoryUsageMetricName(ANY_IN_IN_BACKGROUND_VALUE);

    assertTrue(extractor.isMemoryUsageMetric(memoryUsage));
  }

  @Test public void doesNotIdentifyABytesAllocatedMetricAsMemoryUsage() {
    String bytesAllocated = generator.getBytesAllocatedMetricName(ANY_IN_IN_BACKGROUND_VALUE);

    assertFalse(extractor.isMemoryUsageMetric(bytesAllocated));
  }

  @Test public void bytesAllocatedMetricNameShouldContainExactly11FieldsSeparatedByDots() {
    String bytesAllocated = generator.getBytesAllocatedMetricName(ANY_IN_IN_BACKGROUND_VALUE);

    assertEquals(12, MetricNameUtils.split(bytesAllocated).length);
  }

  @Test public void bytesAllocatedUsageUsageMetricNameShouldContainTheCrossMetricInfoName() {
    String bytesAllocated = generator.getBytesAllocatedMetricName(ANY_IN_IN_BACKGROUND_VALUE);

    assertContainsCrossMetricInfoName(bytesAllocated);
  }

  @Test public void bytesAllocatedUsageContainsTheMetricNameInTheCorrectPosition() {
    String bytesAllocated = generator.getBytesAllocatedMetricName(ANY_IN_IN_BACKGROUND_VALUE);

    String[] parts = MetricNameUtils.split(bytesAllocated);
    assertEquals("bytesAllocated", parts[11]);
  }

  @Test public void identifiesABytesAllocatedMetricProperly() {
    String bytesAllocated = generator.getBytesAllocatedMetricName(ANY_IN_IN_BACKGROUND_VALUE);

    assertTrue(extractor.isBytesAllocatedMetric(bytesAllocated));
  }

  @Test public void doesNotIdentifyAMemoryUsageMetricAsBytesAllocated() {
    String memoryUsage = generator.getMemoryUsageMetricName(ANY_IN_IN_BACKGROUND_VALUE);

    assertFalse(extractor.isBytesAllocatedMetric(memoryUsage));
  }

  @Test
  public void internalStorageWrittenBytesMetricNameShouldContainExactly11FieldsSeparatedByDots() {
    String writtenBytes = generator.getInternalStorageWrittenBytes(ANY_IN_IN_BACKGROUND_VALUE);

    assertEquals(12, MetricNameUtils.split(writtenBytes).length);
  }

  @Test public void internalStorageWrittenBytesMetricNameShouldContainTheCrossMetricInfoName() {
    String writtenBytes = generator.getInternalStorageWrittenBytes(ANY_IN_IN_BACKGROUND_VALUE);

    assertContainsCrossMetricInfoName(writtenBytes);
  }

  @Test public void internalStorageWrittenBytesContainsTheMetricNameInTheCorrectPosition() {
    String writtenBytes = generator.getInternalStorageWrittenBytes(ANY_IN_IN_BACKGROUND_VALUE);

    String[] parts = MetricNameUtils.split(writtenBytes);
    assertEquals("internalStorageWrittenBytes", parts[11]);
  }

  @Test public void identifiesAInternalStorageWrittenBytesMetricProperly() {
    String writtenBytes = generator.getInternalStorageWrittenBytes(ANY_IN_IN_BACKGROUND_VALUE);

    assertTrue(extractor.isInternalStorageAllocatedBytesMetric(writtenBytes));
  }

  @Test public void doesNotIdentifyAnInternalStorageWrittenBytesMetricAsSharedPrefsWrittenBytes() {
    String writtenBytes = generator.getSharedPreferencesWrittenBytes(ANY_IN_IN_BACKGROUND_VALUE);

    assertFalse(extractor.isBytesAllocatedMetric(writtenBytes));
  }

  @Test public void sharedPrefsWrittenBytesMetricNameShouldContainExactly11FieldsSeparatedByDots() {
    String writtenBytes = generator.getSharedPreferencesWrittenBytes(ANY_IN_IN_BACKGROUND_VALUE);

    assertEquals(12, MetricNameUtils.split(writtenBytes).length);
  }

  @Test public void sharedPrefsWrittenBytesMetricNameShouldContainTheCrossMetricInfoName() {
    String writtenBytes = generator.getSharedPreferencesWrittenBytes(ANY_IN_IN_BACKGROUND_VALUE);

    assertContainsCrossMetricInfoName(writtenBytes);
  }

  @Test public void sharedPrefsWrittenBytesContainsTheMetricNameInTheCorrectPosition() {
    String writtenBytes = generator.getSharedPreferencesWrittenBytes(ANY_IN_IN_BACKGROUND_VALUE);

    String[] parts = MetricNameUtils.split(writtenBytes);
    assertEquals("sharedPreferencesStorageWrittenBytes", parts[11]);
  }

  @Test public void identifiesASharedPrefsWrittenBytesMetricProperly() {
    String writtenBytes = generator.getSharedPreferencesWrittenBytes(ANY_IN_IN_BACKGROUND_VALUE);

    assertTrue(extractor.isSharedPreferencesAllocatedBytesMetric(writtenBytes));
  }

  @Test public void doesNotIdentifyASharedPrefsWrittenBytesMetricAsInternalStorageWrittenBytes() {
    String writtenBytes = generator.getSharedPreferencesWrittenBytes(ANY_IN_IN_BACKGROUND_VALUE);

    assertFalse(extractor.isInternalStorageAllocatedBytesMetric(writtenBytes));
  }

  @Test public void onActivityCreatedMetricNameShouldContainExactly12FieldsSeparatedByDots() {
    String name = generator.getOnActivityCreatedMetricName(activity);

    assertEquals(13, MetricNameUtils.split(name).length);
  }

  @Test public void onActivityCreatedMetricNameShouldContainTheCrossMetricInfoName() {
    String name = generator.getOnActivityCreatedMetricName(activity);

    assertContainsCrossMetricInfoName(name);
  }

  @Test public void onActivityCreatedContainsTheMetricNameInTheCorrectPosition() {
    String name = generator.getOnActivityCreatedMetricName(activity);

    String[] parts = MetricNameUtils.split(name);
    assertEquals("onActivityCreated", parts[11]);
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
    assertEquals(activity.getClass().getSimpleName(), parts[12]);
  }

  @Test public void onActivityResumedMetricNameShouldContainExactly12FieldsSeparatedByDots() {
    String name = generator.getOnActivityResumedMetricName(activity);

    assertEquals(13, MetricNameUtils.split(name).length);
  }

  @Test public void onActivityResumedMetricNameShouldContainTheCrossMetricInfoName() {
    String name = generator.getOnActivityResumedMetricName(activity);

    assertContainsCrossMetricInfoName(name);
  }

  @Test public void onActivityResumedContainsTheMetricNameInTheCorrectPosition() {
    String name = generator.getOnActivityResumedMetricName(activity);

    String[] parts = MetricNameUtils.split(name);
    assertEquals("onActivityResumed", parts[11]);
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
    assertEquals(activity.getClass().getSimpleName(), parts[12]);
  }

  @Test public void onActivityStartedMetricNameShouldContainExactly12FieldsSeparatedByDots() {
    String name = generator.getOnActivityStartedMetricName(activity);

    assertEquals(13, MetricNameUtils.split(name).length);
  }

  @Test public void onActivityStartedMetricNameShouldContainTheCrossMetricInfoName() {
    String name = generator.getOnActivityStartedMetricName(activity);

    assertContainsCrossMetricInfoName(name);
  }

  @Test public void onActivityStartedContainsTheMetricNameInTheCorrectPosition() {
    String name = generator.getOnActivityStartedMetricName(activity);

    String[] parts = MetricNameUtils.split(name);
    assertEquals("onActivityStarted", parts[11]);
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
    assertEquals(activity.getClass().getSimpleName(), parts[12]);
  }

  @Test public void activityVisibleMetricNameShouldContainExactly12FieldsSeparatedByDots() {
    String name = generator.getActivityVisibleMetricName(activity);

    assertEquals(13, MetricNameUtils.split(name).length);
  }

  @Test public void activityVisibleMetricNameShouldContainTheCrossMetricInfoName() {
    String name = generator.getActivityVisibleMetricName(activity);

    assertContainsCrossMetricInfoName(name);
  }

  @Test public void activityVisibleMetricContainsTheMetricNameInTheCorrectPosition() {
    String name = generator.getActivityVisibleMetricName(activity);

    String[] parts = MetricNameUtils.split(name);
    assertEquals("activityVisible", parts[11]);
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
    assertEquals(activity.getClass().getSimpleName(), parts[12]);
  }

  @Test public void onActivityPausedMetricNameShouldContainExactly12FieldsSeparatedByDots() {
    String name = generator.getOnActivityPausedMetricName(activity);

    assertEquals(13, MetricNameUtils.split(name).length);
  }

  @Test public void onActivityPausedMetricNameShouldContainTheCrossMetricInfoName() {
    String name = generator.getOnActivityPausedMetricName(activity);

    assertContainsCrossMetricInfoName(name);
  }

  @Test public void onActivityPausedMetricContainsTheMetricNameInTheCorrectPosition() {
    String name = generator.getOnActivityPausedMetricName(activity);

    String[] parts = MetricNameUtils.split(name);
    assertEquals("onActivityPaused", parts[11]);
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
    assertEquals(activity.getClass().getSimpleName(), parts[12]);
  }

  @Test public void onActivityStoppedMetricNameShouldContainExactly12FieldsSeparatedByDots() {
    String name = generator.getOnActivityStoppedMetricName(activity);

    assertEquals(13, MetricNameUtils.split(name).length);
  }

  @Test public void onActivityStoppedMetricNameShouldContainTheCrossMetricInfoName() {
    String name = generator.getOnActivityStoppedMetricName(activity);

    assertContainsCrossMetricInfoName(name);
  }

  @Test public void onActivityStoppedMetricContainsTheMetricNameInTheCorrectPosition() {
    String name = generator.getOnActivityStoppedMetricName(activity);

    String[] parts = MetricNameUtils.split(name);
    assertEquals("onActivityStopped", parts[11]);
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
    assertEquals(activity.getClass().getSimpleName(), parts[12]);
  }

  @Test public void onActivityDestroyedMetricNameShouldContainExactly12FieldsSeparatedByDots() {
    String name = generator.getOnActivityDestroyedMetricName(activity);

    assertEquals(13, MetricNameUtils.split(name).length);
  }

  @Test public void onActivityDestroyedMetricNameShouldContainTheCrossMetricInfoName() {
    String name = generator.getOnActivityDestroyedMetricName(activity);

    assertContainsCrossMetricInfoName(name);
  }

  @Test public void onActivityDestroyedMetricContainsTheMetricNameInTheCorrectPosition() {
    String name = generator.getOnActivityDestroyedMetricName(activity);

    String[] parts = MetricNameUtils.split(name);
    assertEquals("onActivityDestroyed", parts[11]);
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
    assertEquals(activity.getClass().getSimpleName(), parts[12]);
  }

  @Test public void returnsTrueJustWithUIMetrics() {
    String frameTime = generator.getFrameTimeMetricName(activity);
    String activityCreated = generator.getOnActivityCreatedMetricName(activity);
    String activityStarted = generator.getOnActivityStartedMetricName(activity);
    String activityResumed = generator.getOnActivityResumedMetricName(activity);
    String activityVisible = generator.getActivityVisibleMetricName(activity);
    String activityPaused = generator.getOnActivityPausedMetricName(activity);
    String activityStopped = generator.getOnActivityStoppedMetricName(activity);
    String activityDestroyed = generator.getOnActivityDestroyedMetricName(activity);
    String nonUIMetricName = generator.getBytesAllocatedMetricName(ANY_IN_IN_BACKGROUND_VALUE);

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

  @Test
  public void cpuMetricsNameInBackgroundOrForegroundHaveToBeDifferent() {
    String foreground = generator.getCPUUsageMetricName(false);
    String background = generator.getCPUUsageMetricName(true);

    assertFalse(foreground.equals(background));
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
    assertEquals(ANY_IN_IN_BACKGROUND_VALUE,
        extractor.getIsApplicationInBackground(metricName));
  }

  private void givenNowIs(long timestamp) {
    when(time.now()).thenReturn(timestamp);
  }
}
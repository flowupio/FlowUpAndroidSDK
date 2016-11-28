/*
 * Copyright (C) 2016 Go Karumi S.L.
 */

package io.flowup.storage;

import android.app.Activity;
import android.content.Context;
import com.codahale.metrics.Counter;
import com.codahale.metrics.Gauge;
import com.codahale.metrics.Histogram;
import com.codahale.metrics.Meter;
import com.codahale.metrics.Timer;
import io.flowup.android.App;
import io.flowup.android.Device;
import io.flowup.config.Config;
import io.flowup.config.storage.ConfigStorage;
import io.flowup.doubles.ActivityTwo;
import io.flowup.logger.Logger;
import io.flowup.metricnames.MetricNamesGenerator;
import io.flowup.reporter.DropwizardReport;
import io.flowup.reporter.model.CPUMetric;
import io.flowup.reporter.model.DiskMetric;
import io.flowup.reporter.model.MemoryMetric;
import io.flowup.reporter.model.NetworkMetric;
import io.flowup.reporter.model.Reports;
import io.flowup.reporter.model.UIMetric;
import io.flowup.reporter.storage.ReportsStorage;
import io.flowup.utils.Time;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ReportsStorageTest {

  private static final double ANY_FRAME_TIME = 17;
  private static final long ANY_BYTES_UPLOADED = 1024L;
  private static final long ANY_BYTES_DOWNLOADED = 1024L;
  private static final double DELTA = 0.1d;
  private static final long ANY_CPU_USAGE = 11;
  private static final long ANY_MEMORY_USAGE = 21;
  private static final long ANY_BYTES_ALLOCATED = 1024;
  private static final long ANY_INTERNAL_STORAGE_WRITTEN_BYTES = 2048;
  private static final long ANY_SHARED_PREFS_WRITTEN_BYTES = 3072;
  private static final long ANY_LIFECYCLE_TIME = 11;
  private static final int ANY_NUMBER_OF_REPORTS = 10;
  private static final long ANY_TIMESTAMP = Long.MAX_VALUE;

  private ReportsStorage storage;
  private ConfigStorage configStorage;
  private MetricNamesGenerator generator;
  private Time time;

  @Before public void setUp() {
    Context context = getInstrumentation().getContext();
    SQLDelightfulOpenHelper openHelper = new SQLDelightfulOpenHelper(context);
    initializeTimeMock();
    storage = new ReportsStorage(openHelper, time);
    generator = new MetricNamesGenerator(new App(context), new Device(context), new Time());
    configStorage = new ConfigStorage(new SQLDelightfulOpenHelper(context));
    clearDatabase();
  }

  @After public void tearDown() {
    clearDatabase();
  }

  @Test public void storesOneReport() {
    DropwizardReport dropwizardReport = givenAnEmptyDropwizardReport();

    Reports reports = storeAndGet(dropwizardReport);

    assertEquals(1, reports.getReportsIds().size());
  }

  @Test public void storesMoreThanOneReport() {
    int numberOfReports = 12;
    List<DropwizardReport> dropwizardReports = givenSomeEmptyDropwizardReports(numberOfReports);

    Reports reports = storeAndGet(dropwizardReports);

    assertEquals(numberOfReports, reports.getReportsIds().size());
  }

  @Test public void doesNotHaveInfoAssociatedIfTheDropwizardReportsAreEmpty() {
    DropwizardReport dropwizardReport = givenAnEmptyDropwizardReport();

    Reports reports = storeAndGet(dropwizardReport);

    assertThereAreNoReports(reports);
  }

  @Test public void returnsReportInfoBasedOnDropwizardMetricsWithOnlyNetworkMetricsReported() {
    SortedMap<String, Gauge> networkMetrics = givenANetworkMetric();
    DropwizardReport dropwizardReport = givenADropWizardReport(networkMetrics);

    Reports reports = storeAndGet(dropwizardReport);

    assertEquals(1, reports.getNetworkMetrics().size());
    assertEquals(0, reports.getUIMetrics().size());
    assertEquals(0, reports.getCpuMetrics().size());
    assertEquals(0, reports.getMemoryMetrics().size());
    assertEquals(0, reports.getDiskMetrics().size());
  }

  @Test public void returnsReportInfoBasedOnDropwizardMetricsWithOnlyUIMetricsReported() {
    SortedMap<String, Timer> frameTimeMetric = givenAFrameTimeMetric();
    DropwizardReport dropwizardReport =
        givenADropWizardReport(new TreeMap<String, Gauge>(), new TreeMap<String, Histogram>(),
            frameTimeMetric);

    Reports reports = storeAndGet(dropwizardReport);

    assertEquals(1, reports.getUIMetrics().size());
    assertEquals(0, reports.getNetworkMetrics().size());
    assertEquals(0, reports.getCpuMetrics().size());
    assertEquals(0, reports.getMemoryMetrics().size());
    assertEquals(0, reports.getDiskMetrics().size());
  }

  @Test public void returnsReportInfoBasedOnDropwizardMetricsWithOnlyCPUMetricsReported() {
    SortedMap<String, Gauge> cpuMetrics = givenACPUMetric();
    DropwizardReport dropwizardReport = givenADropWizardReport(cpuMetrics);

    Reports reports = storeAndGet(dropwizardReport);

    assertEquals(0, reports.getUIMetrics().size());
    assertEquals(0, reports.getNetworkMetrics().size());
    assertEquals(1, reports.getCpuMetrics().size());
    assertEquals(0, reports.getMemoryMetrics().size());
    assertEquals(0, reports.getDiskMetrics().size());
  }

  @Test public void returnsReportInfoBasedOnDropwizardMetricsWithOnlyMemoryMetricsReported() {
    SortedMap<String, Gauge> memoryMetric = givenAMemoryMetric();
    DropwizardReport dropwizardReport = givenADropWizardReport(memoryMetric);

    Reports reports = storeAndGet(dropwizardReport);

    assertEquals(0, reports.getUIMetrics().size());
    assertEquals(0, reports.getNetworkMetrics().size());
    assertEquals(0, reports.getCpuMetrics().size());
    assertEquals(1, reports.getMemoryMetrics().size());
    assertEquals(0, reports.getDiskMetrics().size());
  }

  @Test public void returnsReportInfoBasedOnDropwizardMetricsWithOnlyDiskMetricsReported() {
    SortedMap<String, Gauge> diskMetric = givenADiskMetric();
    DropwizardReport dropwizardReport = givenADropWizardReport(diskMetric);

    Reports reports = storeAndGet(dropwizardReport);

    assertEquals(0, reports.getUIMetrics().size());
    assertEquals(0, reports.getNetworkMetrics().size());
    assertEquals(0, reports.getCpuMetrics().size());
    assertEquals(0, reports.getMemoryMetrics().size());
    assertEquals(1, reports.getDiskMetrics().size());
  }

  @Test public void joinsSomeReportsIntoOneReportsInstanceWithAllTheMetricsInside() {
    int numberOfReports = 10;
    List<DropwizardReport> dropwizardReports = givenSomeDropwizardReports(numberOfReports);

    Reports reports = storeAndGet(dropwizardReports);

    assertNetworkMetricsContainsExpectedValues(numberOfReports, reports);
    assertUIMetricsContainsExpectedValues(numberOfReports, reports);
    assertCPUMetricsContainsExpectedValues(numberOfReports, reports);
    assertMemoryMetricsContainsExpectedValues(numberOfReports, reports);
    assertDiskMetricsContainsExpectedValues(numberOfReports, reports);
  }

  @Test public void deletesTheReportsPreviouslyObtained() {
    int numberOfReports = 10;
    List<DropwizardReport> dropwizardReports = givenSomeDropwizardReports(numberOfReports);
    Reports reports = storeAndGet(dropwizardReports);

    storage.deleteReports(reports);
    reports = storage.getReports(numberOfReports);

    assertNull(reports);
  }

  @Test public void deletesTheReportsMatchingWithTheReportId() {
    int numberOfReports = 10;
    List<DropwizardReport> dropwizardReports = givenSomeDropwizardReports(numberOfReports);
    Reports reports = storeAndGet(dropwizardReports);

    List<String> idsToRemove = new LinkedList<>();
    int numberOfReportsToRemove = numberOfReports / 2;
    for (int i = 0; i < numberOfReportsToRemove; i++) {
      idsToRemove.add(reports.getReportsIds().get(i));
    }
    Reports reportsToRemove = new Reports(idsToRemove);
    storage.deleteReports(reportsToRemove);
    Reports restOfReports = storage.getReports(numberOfReports);

    assertEquals(numberOfReportsToRemove, restOfReports.getReportsIds().size());
  }

  @Test public void savesDataAssociatedToDifferentScreensAsDifferentUIMetricsInsideTheSameReport() {
    SortedMap<String, Timer> frameTimeMetric = givenSomeFrameTimeMetricsTwoDifferentScreens();
    DropwizardReport dropwizardReport =
        givenADropWizardReport(new TreeMap<String, Gauge>(), new TreeMap<String, Histogram>(),
            frameTimeMetric);

    Reports reports = storeAndGet(dropwizardReport);

    assertEquals(2, reports.getUIMetrics().size());
  }

  @Test public void clearsTheDatabase() {
    int numberOfReports = 12;
    List<DropwizardReport> dropwizardReports = givenSomeEmptyDropwizardReports(numberOfReports);
    storeAndGet(dropwizardReports);

    storage.clear();

    assertNull(storage.getReports(numberOfReports));
  }

  @Test public void doesNotRemoveReportsIfTheDatabaseIsEmpty() {
    storage.deleteOldReports();

    assertNull(storage.getReports(1));
  }

  @Test public void doesNotRemoveAnyReportIfItIsNotOld() {
    int numberOfReports = 2;
    List<DropwizardReport> dropwizardReport = givenSomeDropwizardReports(numberOfReports);
    storeAndGet(dropwizardReport);

    when(time.twoDaysAgo()).thenReturn(0L);
    storage.deleteOldReports();

    assertEquals(2, storage.getReports(numberOfReports).size());
  }

  @Test public void removesJustOldReports() {
    int numberOfReports = 2;
    List<DropwizardReport> dropwizardReport = givenSomeDropwizardReports(numberOfReports);
    storeAndGet(dropwizardReport);

    when(time.twoDaysAgo()).thenReturn(1L);
    storage.deleteOldReports();

    assertEquals(1, storage.getReports(numberOfReports).size());
  }

  @Test public void removesEveryReportIfAllAreOld() {
    int numberOfReports = 2;
    List<DropwizardReport> dropwizardReport = givenSomeDropwizardReports(numberOfReports);
    storeAndGet(dropwizardReport);

    when(time.twoDaysAgo()).thenReturn(2L);
    storage.deleteOldReports();

    assertNull(storage.getReports(numberOfReports));
  }

  @Test public void returnsTheNumberOfReportsDeleted() {
    int numberOfReports = 2;
    List<DropwizardReport> dropwizardReport = givenSomeDropwizardReports(numberOfReports);
    storeAndGet(dropwizardReport);

    when(time.twoDaysAgo()).thenReturn(2L);
    int numberOfReportsDeleted = storage.deleteOldReports();

    assertEquals(numberOfReports, numberOfReportsDeleted);
  }

  @Test public void supportsNThreadsWritingAtTheSameTime() throws Exception {
    Logger.setEnabled(true);
    int numberOfReports = 20;
    int numberOfThreads = 10;
    int totalNumberOfReports = numberOfReports * numberOfThreads;

    writeABunchOfReports(numberOfReports, numberOfThreads);
    updateAndDisableConfig(numberOfThreads);
    Reports reports = storage.getReports(totalNumberOfReports);

    assertEquals(totalNumberOfReports, reports.size());
  }

  @Test public void returnsANullInstanceIfThereAreNoReports() throws Exception {
    Reports reports = storage.getReports(ANY_NUMBER_OF_REPORTS);

    assertNull(reports);
  }

  @Test public void doesNotReturnNullIfThereIsJustOneReportPersistedWithoutMetrics() throws Exception {
    DropwizardReport reportWithoutMetrics =
        new DropwizardReport(ANY_TIMESTAMP, new TreeMap<String, Gauge>(),
            new TreeMap<String, Counter>(), new TreeMap<String, Histogram>(),
            new TreeMap<String, Meter>(), new TreeMap<String, Timer>());
    storage.storeMetrics(reportWithoutMetrics);

    Reports reports = storage.getReports(ANY_NUMBER_OF_REPORTS);

    assertEquals(1, reports.size());
    assertNull(reports.getAppPackage());
    assertNull(reports.getCpuMetrics());
    assertNull(reports.getDeviceModel());
    assertNull(reports.getDiskMetrics());
    assertNull(reports.getMemoryMetrics());
    assertNull(reports.getNetworkMetrics());
    assertNull(reports.getNumberOfCores());
    assertNull(reports.getScreenDensity());
    assertNull(reports.getScreenSize());
    assertNull(reports.getUIMetrics());
    assertNull(reports.getUUID());
  }

  private void writeABunchOfReports(final int numberOfReports, int numberOfThreads)
      throws Exception {
    final CountDownLatch latch = new CountDownLatch(numberOfThreads);
    for (int i = 0; i < numberOfThreads; i++) {
      new Thread(new Runnable() {
        @Override public void run() {
          List<DropwizardReport> dropwizardReport = givenSomeDropwizardReports(numberOfReports);
          storeAndGet(dropwizardReport);
          latch.countDown();
        }
      }).start();
    }
    latch.await();
  }

  private void updateAndDisableConfig(int numberOfThreads) throws Exception {
    final CountDownLatch latch = new CountDownLatch(numberOfThreads);
    for (int i = 0; i < numberOfThreads; i++) {
      new Thread(new Runnable() {
        @Override public void run() {
          Config currentConig = configStorage.getConfig();
          configStorage.updateConfig(new Config(!currentConig.isEnabled()));
          configStorage.clearConfig();
          latch.countDown();
        }
      }).start();
    }
    latch.await();
  }

  private SortedMap<String, Timer> givenSomeFrameTimeMetricsTwoDifferentScreens() {
    SortedMap<String, Timer> frameTimeMetrics = new TreeMap<>();
    frameTimeMetrics.putAll(givenAFrameTimeMetric(mock(Activity.class)));
    frameTimeMetrics.putAll(givenAFrameTimeMetric(mock(ActivityTwo.class)));
    return frameTimeMetrics;
  }

  private void assertNetworkMetricsContainsExpectedValues(int numberOfReports, Reports reports) {
    List<NetworkMetric> networkReports = reports.getNetworkMetrics();
    assertEquals(numberOfReports, networkReports.size());
    for (int i = 0; i < numberOfReports; i++) {
      assertEquals(ANY_BYTES_UPLOADED, networkReports.get(i).getBytesUploaded());
      assertEquals(ANY_BYTES_DOWNLOADED, networkReports.get(i).getBytesDownloaded());
    }
  }

  private void assertCPUMetricsContainsExpectedValues(int numberOfReports, Reports reports) {
    List<CPUMetric> networkReports = reports.getCpuMetrics();
    assertEquals(numberOfReports, networkReports.size());
    for (int i = 0; i < numberOfReports; i++) {
      assertEquals(ANY_CPU_USAGE, networkReports.get(i).getCpuUsage());
    }
  }

  private void assertMemoryMetricsContainsExpectedValues(int numberOfReports, Reports reports) {
    List<MemoryMetric> memoryMetrics = reports.getMemoryMetrics();
    assertEquals(numberOfReports, memoryMetrics.size());
    for (int i = 0; i < numberOfReports; i++) {
      assertEquals(ANY_MEMORY_USAGE, memoryMetrics.get(i).getMemoryUsage());
      assertEquals(ANY_BYTES_ALLOCATED, memoryMetrics.get(i).getBytesAllocated());
    }
  }

  private void assertDiskMetricsContainsExpectedValues(int numberOfReports, Reports reports) {
    List<DiskMetric> diskMetrics = reports.getDiskMetrics();
    assertEquals(numberOfReports, diskMetrics.size());
    for (int i = 0; i < numberOfReports; i++) {
      assertEquals(ANY_INTERNAL_STORAGE_WRITTEN_BYTES,
          diskMetrics.get(i).getInternalStorageWrittenBytes());
      assertEquals(ANY_SHARED_PREFS_WRITTEN_BYTES,
          diskMetrics.get(i).getSharedPreferencesWrittenBytes());
    }
  }

  private void assertUIMetricsContainsExpectedValues(int numberOfReports, Reports reports) {
    List<UIMetric> uiReports = reports.getUIMetrics();
    assertEquals(numberOfReports, uiReports.size());
    for (int i = 0; i < numberOfReports; i++) {
      UIMetric uiMetric = uiReports.get(i);
      assertEquals(1.7E7, uiMetric.getFrameTime().getMean(), DELTA);
      assertEquals(ANY_LIFECYCLE_TIME, uiMetric.getOnActivityCreatedTime().getMean(), DELTA);
      assertEquals(ANY_LIFECYCLE_TIME, uiMetric.getOnActivityStartedTime().getMean(), DELTA);
      assertEquals(ANY_LIFECYCLE_TIME, uiMetric.getOnActivityResumedTime().getMean(), DELTA);
      assertEquals(ANY_LIFECYCLE_TIME, uiMetric.getActivityVisibleTime().getMean(), DELTA);
      assertEquals(ANY_LIFECYCLE_TIME, uiMetric.getOnActivityPausedTime().getMean(), DELTA);
      assertEquals(ANY_LIFECYCLE_TIME, uiMetric.getOnActivityStoppedTime().getMean(), DELTA);
      assertEquals(ANY_LIFECYCLE_TIME, uiMetric.getOnActivityDestroyedTime().getMean(), DELTA);
    }
  }

  private List<DropwizardReport> givenSomeDropwizardReports(int numberOfReports) {
    List<DropwizardReport> dropwizardReports = new LinkedList<>();
    for (int i = 0; i < numberOfReports; i++) {
      SortedMap<String, Gauge> networkMetrics = givenANetworkMetric();
      SortedMap<String, Timer> frameTimeMetric = givenAFrameTimeMetric();
      SortedMap<String, Timer> onActivityCreatedMetric = givenAnyLifecycleMetric();
      SortedMap<String, Timer> onActivityStartedMetric = givenAFrameTimeMetric();
      SortedMap<String, Timer> onActivityResumedMetric = givenAFrameTimeMetric();
      SortedMap<String, Timer> activityVisible = givenAFrameTimeMetric();
      SortedMap<String, Timer> onActivityPausedMetric = givenAFrameTimeMetric();
      SortedMap<String, Timer> onActivityStoppedMetric = givenAFrameTimeMetric();
      SortedMap<String, Timer> onActivityDestroyedMetric = givenAFrameTimeMetric();
      SortedMap<String, Gauge> cpuMetrics = givenACPUMetric();
      SortedMap<String, Gauge> memoryMetrics = givenAMemoryMetric();
      SortedMap<String, Gauge> diskMetrics = givenADiskMetric();
      SortedMap<String, Gauge> gauges = new TreeMap<>();
      gauges.putAll(networkMetrics);
      gauges.putAll(cpuMetrics);
      gauges.putAll(memoryMetrics);
      gauges.putAll(diskMetrics);
      SortedMap<String, Timer> timers = new TreeMap<>();
      SortedMap<String, Histogram> histograms = new TreeMap<>();
      timers.putAll(frameTimeMetric);
      timers.putAll(onActivityCreatedMetric);
      timers.putAll(onActivityStartedMetric);
      timers.putAll(onActivityResumedMetric);
      timers.putAll(activityVisible);
      timers.putAll(onActivityPausedMetric);
      timers.putAll(onActivityStoppedMetric);
      timers.putAll(onActivityDestroyedMetric);
      DropwizardReport dropwizardReport = givenADropWizardReport(i, gauges, histograms, timers);
      dropwizardReports.add(dropwizardReport);
    }
    return dropwizardReports;
  }

  private SortedMap<String, Timer> givenAnyLifecycleMetric() {
    Activity activity = mock(Activity.class);
    SortedMap<String, Timer> timers = new TreeMap<>();
    Timer timer = new Timer();
    timer.update(ANY_LIFECYCLE_TIME, TimeUnit.NANOSECONDS);
    timers.put(generator.getOnActivityCreatedMetricName(activity), timer);
    timers.put(generator.getOnActivityStartedMetricName(activity), timer);
    timers.put(generator.getOnActivityResumedMetricName(activity), timer);
    timers.put(generator.getActivityVisibleMetricName(activity), timer);
    timers.put(generator.getOnActivityPausedMetricName(activity), timer);
    timers.put(generator.getOnActivityStoppedMetricName(activity), timer);
    timers.put(generator.getOnActivityDestroyedMetricName(activity), timer);
    return timers;
  }

  private SortedMap<String, Timer> givenAFrameTimeMetric() {
    Activity activity = mock(Activity.class);
    return givenAFrameTimeMetric(activity);
  }

  private SortedMap<String, Timer> givenAFrameTimeMetric(Activity activity) {
    SortedMap<String, Timer> timers = new TreeMap<>();
    Timer timer = new Timer();
    timer.update((long) ANY_FRAME_TIME, TimeUnit.MILLISECONDS);
    timers.put(generator.getFrameTimeMetricName(activity), timer);
    return timers;
  }

  private DropwizardReport givenADropWizardReport(SortedMap<String, Gauge> gauges) {
    return givenADropWizardReport(time.now(), gauges, new TreeMap<String, Histogram>(),
        new TreeMap<String, Timer>());
  }

  private DropwizardReport givenADropWizardReport(SortedMap<String, Gauge> gauges,
      SortedMap<String, Histogram> histograms, SortedMap<String, Timer> timers) {
    return givenADropWizardReport(time.now(), gauges, histograms, timers);
  }

  private DropwizardReport givenADropWizardReport(long timestamp, SortedMap<String, Gauge> gauges,
      SortedMap<String, Histogram> histograms, SortedMap<String, Timer> timers) {
    SortedMap<String, Counter> counters = new TreeMap<>();
    SortedMap<String, Meter> meters = new TreeMap<>();
    return new DropwizardReport(timestamp, gauges, counters, histograms, meters, timers);
  }

  private SortedMap<String, Gauge> givenANetworkMetric() {
    Gauge<Long> bytesUploaded = new Gauge<Long>() {
      @Override public Long getValue() {
        return ANY_BYTES_UPLOADED;
      }
    };
    Gauge<Long> bytesDownloaded = new Gauge<Long>() {
      @Override public Long getValue() {
        return ANY_BYTES_DOWNLOADED;
      }
    };
    SortedMap<String, Gauge> gauges = new TreeMap<>();
    gauges.put(generator.getBytesDownloadedMetricName(), bytesUploaded);
    gauges.put(generator.getBytesUploadedMetricName(), bytesDownloaded);
    return gauges;
  }

  private SortedMap<String, Gauge> givenACPUMetric() {
    Gauge<Long> cpuUsage = new Gauge<Long>() {
      @Override public Long getValue() {
        return ANY_CPU_USAGE;
      }
    };
    SortedMap<String, Gauge> gauges = new TreeMap<>();
    gauges.put(generator.getCPUUsageMetricName(), cpuUsage);
    return gauges;
  }

  private SortedMap<String, Gauge> givenAMemoryMetric() {
    Gauge<Long> memoryUsage = new Gauge<Long>() {
      @Override public Long getValue() {
        return ANY_MEMORY_USAGE;
      }
    };
    Gauge<Long> bytesAllocated = new Gauge<Long>() {
      @Override public Long getValue() {
        return ANY_BYTES_ALLOCATED;
      }
    };
    SortedMap<String, Gauge> gauges = new TreeMap<>();
    gauges.put(generator.getMemoryUsageMetricName(), memoryUsage);
    gauges.put(generator.getBytesAllocatedMetricName(), bytesAllocated);
    return gauges;
  }

  private SortedMap<String, Gauge> givenADiskMetric() {
    Gauge<Long> internalStorageWrittenBytes = new Gauge<Long>() {
      @Override public Long getValue() {
        return ANY_INTERNAL_STORAGE_WRITTEN_BYTES;
      }
    };
    Gauge<Long> sharedPrefsWrittenBytes = new Gauge<Long>() {
      @Override public Long getValue() {
        return ANY_SHARED_PREFS_WRITTEN_BYTES;
      }
    };
    SortedMap<String, Gauge> gauges = new TreeMap<>();
    gauges.put(generator.getInternalStorageWrittenBytes(), internalStorageWrittenBytes);
    gauges.put(generator.getSharedPreferencesWrittenBytes(), sharedPrefsWrittenBytes);
    return gauges;
  }

  private void assertThereAreNoReports(Reports reports) {
    assertNull(reports.getAppPackage());
    assertNull(reports.getDeviceModel());
    assertNull(reports.getNumberOfCores());
    assertNull(reports.getScreenSize());
    assertNull(reports.getScreenDensity());
    assertNull(reports.getUUID());
    assertNull(reports.getNetworkMetrics());
    assertNull(reports.getUIMetrics());
  }

  private Reports storeAndGet(DropwizardReport report) {
    return storeAndGet(Collections.singletonList(report));
  }

  private Reports storeAndGet(List<DropwizardReport> dropwizardReports) {
    int numberOfReports = dropwizardReports.size();
    for (int i = 0; i < numberOfReports; i++) {
      storage.storeMetrics(dropwizardReports.get(i));
    }
    return storage.getReports(numberOfReports);
  }

  private List<DropwizardReport> givenSomeEmptyDropwizardReports(int numberOfReports) {
    List<DropwizardReport> reports = new LinkedList<>();
    for (int i = 0; i < numberOfReports; i++) {
      DropwizardReport report = givenAnEmptyDropwizardReport(i);
      reports.add(report);
    }
    return reports;
  }

  private DropwizardReport givenAnEmptyDropwizardReport() {
    return givenAnEmptyDropwizardReport(0);
  }

  private DropwizardReport givenAnEmptyDropwizardReport(long timestamp) {
    SortedMap<String, Gauge> gauges = generateGauges();
    SortedMap<String, Counter> counters = new TreeMap<>();
    SortedMap<String, Histogram> histograms = generateHistograms();
    SortedMap<String, Meter> meters = new TreeMap<>();
    SortedMap<String, Timer> timers = generateTimers();
    return new DropwizardReport(timestamp, gauges, counters, histograms, meters, timers);
  }

  private SortedMap<String, Gauge> generateGauges() {
    return new TreeMap<>();
  }

  private SortedMap<String, Histogram> generateHistograms() {
    return new TreeMap<>();
  }

  private SortedMap<String, Timer> generateTimers() {
    return new TreeMap<>();
  }

  private void clearDatabase() {
    storage.clear();
  }

  private void initializeTimeMock() {
    Time defaultTime = new Time();
    time = mock(Time.class);
    when(time.now()).thenReturn(defaultTime.now());
    when(time.nowInNanos()).thenReturn(defaultTime.nowInNanos());
    when(time.twoDaysAgo()).thenReturn(defaultTime.twoDaysAgo());
  }
}
/*
 * Copyright (C) 2016 Go Karumi S.L.
 */

package io.flowup.crashreporter;

import io.flowup.crashreporter.apiclient.CrashReporterApiClient;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class) public class SafetyNetTest {

  private SafetyNet safetyNet;

  @Mock CrashReporterApiClient apiClient;

  @Before public void setUp() {
    safetyNet = new SafetyNet(apiClient, true);
  }

  @Test public void sendsTheExceptionCatchToTheCrashReporterApiClient() {
    NullPointerException nullPointerException = new NullPointerException();
    BrokenRunnable brokenRunnable = new BrokenRunnable(nullPointerException);

    safetyNet.executeSafely(brokenRunnable);

    verify(apiClient).reportError(nullPointerException);
  }

  @Test public void doesNotSendAnyExceptionIfThereIsNoCrash() {
    safetyNet.executeSafely(new Runnable() {
      @Override public void run() {

      }
    });

    verify(apiClient, never()).reportError(any(Throwable.class));
  }

  @Test public void sendsTheExceptionCatchToTheCrashReporterApiClientOnANewThread() {
    NullPointerException nullPointerException = new NullPointerException();
    BrokenRunnable brokenRunnable = new BrokenRunnable(nullPointerException);

    safetyNet.executeSafelyOnNewThread(brokenRunnable);

    verify(apiClient).reportError(nullPointerException);
  }

  @Test public void doesNotSendAnyExceptionIfThereIsNoCrashOnANewThread() {
    safetyNet.executeSafelyOnNewThread(new Runnable() {
      @Override public void run() {

      }
    });

    verify(apiClient, never()).reportError(any(Throwable.class));
  }

  private static class BrokenRunnable implements Runnable {

    private final RuntimeException throwable;

    public BrokenRunnable(RuntimeException exception) {
      this.throwable = exception;
    }

    @Override public void run() {
      throw throwable;
    }
  }
}

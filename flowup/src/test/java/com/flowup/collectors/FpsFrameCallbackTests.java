/*
 * Copyright (C) 2016 Go Karumi S.L.
 */

package com.flowup.collectors;

import android.view.Choreographer;
import com.codahale.metrics.Histogram;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class) public class FpsFrameCallbackTests {

  private static final long ANY_FRAME_TIME = 11;

  private FpsFrameCallback fpsFrameCallback;
  @Mock private Choreographer choreographer;
  @Mock private Histogram histogram;

  @Before public void setUp() {
    fpsFrameCallback = new FpsFrameCallback(histogram, choreographer);
  }

  @Test public void shouldCalculateTheNumberOfFramesPerSecondBasedOnJustOneFrameTime() {
    fpsFrameCallback.doFrame(16000000);
    fpsFrameCallback.doFrame(16000000 * 2);

    verify(histogram).update(62);
  }

  @Test public void shouldCalculateSomeFramesPerSecondIfThereIsMoreThanOneDoFrameCalls() {
    fpsFrameCallback.doFrame(16000000);

    for (int i = 2; i <= 10; i++) {
      fpsFrameCallback.doFrame(16000000 * i);
    }

    verify(histogram, times(9)).update(62);
  }

  @Test public void shouldPostAnotherCallbackToTheChoreographerAfterTheDoFrameExecution() {
    fpsFrameCallback.doFrame(ANY_FRAME_TIME);

    verify(choreographer).postFrameCallback(fpsFrameCallback);
  }
}

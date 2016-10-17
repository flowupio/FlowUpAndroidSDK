/*
 * Copyright (C) 2016 Go Karumi S.L.
 */

package io.flowup.collectors;

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
  private static final long SIXTEEN_MILLIS_IN_NANOSECONDS = 16000000;
  private static final int PERFECT_FPS = 62;
  private static final int ANY_NUMBER_OF_FRAMES = 10;

  private FpsFrameCallback fpsFrameCallback;
  @Mock private Choreographer choreographer;
  @Mock private Histogram histogram;

  @Before public void setUp() {
    fpsFrameCallback = new FpsFrameCallback(histogram, choreographer);
  }

  @Test public void shouldCalculateTheNumberOfFramesPerSecondBasedOnJustOneFrameTime() {
    fpsFrameCallback.doFrame(SIXTEEN_MILLIS_IN_NANOSECONDS);
    fpsFrameCallback.doFrame(SIXTEEN_MILLIS_IN_NANOSECONDS * 2);

    verify(histogram).update(PERFECT_FPS);
  }

  @Test public void shouldCalculateSomeFramesPerSecondIfThereIsMoreThanOneDoFrameCalls() {
    int numberOfDoFrameInvocations = ANY_NUMBER_OF_FRAMES;
    for (int i = 1; i <= numberOfDoFrameInvocations; i++) {
      fpsFrameCallback.doFrame(SIXTEEN_MILLIS_IN_NANOSECONDS * i);
    }

    verify(histogram, times(numberOfDoFrameInvocations - 1)).update(PERFECT_FPS);
  }

  @Test public void shouldPostAnotherCallbackToTheChoreographerAfterTheDoFrameExecution() {
    fpsFrameCallback.doFrame(ANY_FRAME_TIME);

    verify(choreographer).postFrameCallback(fpsFrameCallback);
  }
}

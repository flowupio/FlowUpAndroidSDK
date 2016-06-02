/*
 * Copyright (C) 2016 Go Karumi S.L.
 */

package com.karumi.kiru.collectors;

import android.view.Choreographer;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class) public class FpsFrameCallbackTests {

  private static final long ANY_FRAME_TIME = 11;

  private FpsFrameCallback fpsFrameCallback;
  @Mock private Choreographer choreographer;

  @Before public void setUp() {
    fpsFrameCallback = new FpsFrameCallback(choreographer);
  }

  @Test public void shouldCalculateTheNumberOfFramesPerSecondBasedOnTheAverageFrameTime() {
    for (int i = 0; i < 60; i++) {
      fpsFrameCallback.doFrame(16000000 * (i + 1));
    }

    double framesPerSecond = fpsFrameCallback.getFPS();

    assertEquals(62, framesPerSecond, 0.1);
  }

  @Test public void shouldCalculateTheNumberOfFramesPerSecondBasedOnJustOneFrameTime() {
    fpsFrameCallback.doFrame(16000000);
    fpsFrameCallback.doFrame(16000000 * 2);

    double framesPerSecond = fpsFrameCallback.getFPS();

    assertEquals(62, framesPerSecond, 0.1);
  }

  @Test public void shouldReturnZeroIfTheFrameCallbackHasBeenReset() {
    fpsFrameCallback.doFrame(ANY_FRAME_TIME);

    fpsFrameCallback.reset();

    assertEquals(0, fpsFrameCallback.getFPS(), 0.1);
  }

  @Test public void shouldPostAnotherCallbackToTheChoreographerAfterTheDoFrameExecution() {
    fpsFrameCallback.doFrame(ANY_FRAME_TIME);

    verify(choreographer).postFrameCallback(fpsFrameCallback);
  }
}

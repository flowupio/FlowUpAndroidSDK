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

@RunWith(MockitoJUnitRunner.class) public class FrameTimeCallbackTest {

  private static final long ANY_FRAME_TIME = 11;

  private FrameTimeCallback frameTimeCallback;
  @Mock private Choreographer choreographer;

  @Before public void setUp() {
    frameTimeCallback = new FrameTimeCallback(choreographer);
  }

  @Test public void shouldCalculateTheAverageFrameTime() {
    frameTimeCallback.doFrame(15);
    frameTimeCallback.doFrame(20);
    frameTimeCallback.doFrame(30);

    long frameTimeNanos = frameTimeCallback.getFrameTimeNanos();

    assertEquals(21, frameTimeNanos);
  }

  @Test public void shouldReturnZeroAsFrameTimeIfTheCallbackHasBeenReset() {
    frameTimeCallback.doFrame(ANY_FRAME_TIME);

    frameTimeCallback.reset();

    long frameTimeNanos = frameTimeCallback.getFrameTimeNanos();
    assertEquals(0, frameTimeNanos);
  }

  @Test public void shouldPostAnotherCallbackToTheChoreographerAfterTheDoFrameExecution() {
    frameTimeCallback.doFrame(ANY_FRAME_TIME);

    verify(choreographer).postFrameCallback(frameTimeCallback);
  }
}

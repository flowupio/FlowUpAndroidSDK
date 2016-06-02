/*
 * Copyright (C) 2016 Go Karumi S.L.
 */

package com.karumi.kiru.collectors;

import android.view.Choreographer;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

public class FrameTimeCallbackTest {

  private final FrameTimeCallback frameTimeCallback =
      new FrameTimeCallback(mock(Choreographer.class));

  @Test public void shouldCalculateTheAverageFrameTime() {
    frameTimeCallback.doFrame(15);
    frameTimeCallback.doFrame(20);
    frameTimeCallback.doFrame(30);

    long frameTimeNanos = frameTimeCallback.getFrameTimeNanos();

    assertEquals(21, frameTimeNanos);
  }

  @Test public void shouldReturnZeroAsFrameTimeIfTheCallbackHasBeenReset() {
    frameTimeCallback.doFrame(15);

    frameTimeCallback.reset();

    long frameTimeNanos = frameTimeCallback.getFrameTimeNanos();
    assertEquals(0, frameTimeNanos);
  }
}

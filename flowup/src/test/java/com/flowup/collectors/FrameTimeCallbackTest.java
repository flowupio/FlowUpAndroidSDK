/*
 * Copyright (C) 2016 Go Karumi S.L.
 */

package com.flowup.collectors;

import android.view.Choreographer;
import com.codahale.metrics.Timer;
import java.util.concurrent.TimeUnit;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class) public class FrameTimeCallbackTest {

  private static final long ANY_FRAME_TIME = 11000000000L;

  private FrameTimeCallback frameTimeCallback;
  @Mock private Choreographer choreographer;
  @Mock private Timer timer;

  @Before public void setUp() {
    frameTimeCallback = new FrameTimeCallback(timer, choreographer);
  }

  @Test public void shouldCalculateTheAverageFrameTime() {
    frameTimeCallback.doFrame(16000000L);
    frameTimeCallback.doFrame(16000000L * 2);

    verify(timer).update(16, TimeUnit.MILLISECONDS);
  }

  @Test
  public void shouldCalculateSomeFramesTimesIfThereIsMoreThanOneDoFrameCalls() {
    for (int i = 1; i <= 10; i++) {
      frameTimeCallback.doFrame(16000000 * i);
    }

    verify(timer, times(9)).update(16, TimeUnit.MILLISECONDS);
  }

  @Test public void shouldPostAnotherCallbackToTheChoreographerAfterTheDoFrameExecution() {
    frameTimeCallback.doFrame(ANY_FRAME_TIME);

    verify(choreographer).postFrameCallback(frameTimeCallback);
  }
}

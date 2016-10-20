/*
 * Copyright (C) 2016 Go Karumi S.L.
 */

package io.flowup.collectors;

import android.os.Build;
import android.support.annotation.RequiresApi;
import android.view.Choreographer;
import com.codahale.metrics.Histogram;
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

  private static final long SIXTEEN_MILLIS_IN_NANOSECONDS = 16000000;
  private static final int PERFECT_FRAME_TIME_NANOSECONDS = 16000000;
  private static final int ANY_NUMBER_OF_FRAMES = 10;

  private FrameTimeCallback frameTimeCallback;
  @Mock private Choreographer choreographer;
  @Mock private Timer timer;
  @Mock private Histogram histogram;

  @Before public void setUp() {
    frameTimeCallback = new FrameTimeCallback(timer, histogram, choreographer);
  }

  @Test public void shouldCalculateTheAverageFrameTime() {
    frameTimeCallback.doFrame(SIXTEEN_MILLIS_IN_NANOSECONDS);
    frameTimeCallback.doFrame(SIXTEEN_MILLIS_IN_NANOSECONDS * 2);

    verify(timer).update(PERFECT_FRAME_TIME_NANOSECONDS, TimeUnit.NANOSECONDS);
  }

  @Test public void shouldCalculateSomeFramesTimesIfThereIsMoreThanOneDoFrameCalls() {
    int doFrameInvocations = ANY_NUMBER_OF_FRAMES;
    for (int i = 1; i <= doFrameInvocations; i++) {
      frameTimeCallback.doFrame(SIXTEEN_MILLIS_IN_NANOSECONDS * i);
    }

    verify(timer, times(doFrameInvocations - 1)).update(PERFECT_FRAME_TIME_NANOSECONDS,
        TimeUnit.NANOSECONDS);
  }

  @Test
  public void shouldCalculateFpsBasedOnTheFrameTime() {
    int doFrameInvocations = ANY_NUMBER_OF_FRAMES;
    for (int i = 1; i <= doFrameInvocations; i++) {
      frameTimeCallback.doFrame(SIXTEEN_MILLIS_IN_NANOSECONDS * i);
    }

    verify(histogram, times(doFrameInvocations - 1)).update(62);
  }

  @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN) @Test
  public void shouldPostAnotherCallbackToTheChoreographerAfterTheDoFrameExecution() {
    frameTimeCallback.doFrame(SIXTEEN_MILLIS_IN_NANOSECONDS);

    verify(choreographer).postFrameCallback(frameTimeCallback);
  }
}

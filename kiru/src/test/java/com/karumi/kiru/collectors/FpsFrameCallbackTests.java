package com.karumi.kiru.collectors;

import android.view.Choreographer;

import static org.mockito.Mockito.mock;

public class FpsFrameCallbackTests {

  private final FpsFrameCallback fpsFrameCallback = new FpsFrameCallback(mock(Choreographer.class));
}

/*
 * Copyright (C) 2016 Go Karumi S.L.
 */

package com.flowup.collectors;

import android.view.Choreographer;

class FpsFrameCallback extends FrameTimeCallback {

  FpsFrameCallback(Choreographer choreographer) {
    super(choreographer);
  }

  double getFPS() {
    if (getFrameTime() == 0) {
      return 0;
    }

    return 1000000000L / getFrameTime();
  }
}

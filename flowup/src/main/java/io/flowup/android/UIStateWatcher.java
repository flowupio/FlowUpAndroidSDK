/*
 * Copyright (C) 2016 Go Karumi S.L.
 */

package io.flowup.android;

import android.app.Activity;

public class UIStateWatcher extends EmptyActivityLifecycleCallback {

  private final App app;

  public UIStateWatcher(App app) {
    this.app = app;
  }

  @Override public void onActivityResumed(Activity activity) {
    super.onActivityResumed(activity);
    app.goToForeground();
  }

  @Override public void onActivityPaused(Activity activity) {
    super.onActivityPaused(activity);
    if (activity.isTaskRoot()) {
      app.goToBackground();
    }
  }
}

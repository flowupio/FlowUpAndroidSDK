/*
 * Copyright (C) 2016 Go Karumi S.L.
 */

package io.flowup.android;

import android.app.Activity;

public class UIStateWatcher extends EmptyActivityLifecycleCallback {

  private final App app;
  private final Listener listener;

  public UIStateWatcher(App app) {
    this(app, null);
  }

  public UIStateWatcher(App app, Listener listener) {
    this.app = app;
    this.listener = listener;
  }

  @Override public void onActivityResumed(Activity activity) {
    super.onActivityResumed(activity);
    app.goToForeground();
    if (listener != null) {
      listener.onGoToForeground();
    }
  }

  @Override public void onActivityPaused(Activity activity) {
    super.onActivityPaused(activity);
    if (activity.isTaskRoot()) {
      app.goToBackground();
      if (listener != null) {
        listener.onGoToBackground();
      }
    }
  }

  public interface Listener {
    void onGoToForeground();

    void onGoToBackground();
  }
}

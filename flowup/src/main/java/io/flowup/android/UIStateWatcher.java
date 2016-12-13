/*
 * Copyright (C) 2016 Go Karumi S.L.
 */

package io.flowup.android;

import android.app.Activity;
import io.flowup.logger.Logger;

public class UIStateWatcher extends EmptyActivityLifecycleCallback {

  private final App app;
  private final Listener listener;

  public UIStateWatcher(App app, Listener listener) {
    this.app = app;
    this.listener = listener;
  }

  @Override public void onActivityResumed(Activity activity) {
    super.onActivityResumed(activity);
    app.goToForeground();
    if (listener != null) {
      Logger.d("The app goes to foreground");
      listener.onGoToForeground();
    }
  }

  @Override public void onActivityPaused(Activity activity) {
    super.onActivityPaused(activity);
    if (activity.isTaskRoot()) {
      Logger.d("The app goes to background");
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

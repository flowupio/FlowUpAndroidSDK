/*
 * Copyright (C) 2016 Go Karumi S.L.
 */

package io.flowup.android;

import android.app.Activity;
import android.os.Bundle;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class) public class UIStateWatcherTest {

  @Mock private Activity activity;
  private UIStateWatcher uiStateWatcher;
  private App app;

  @Before public void setUp() {
    app = new App(activity);
    app.goToBackground();
    uiStateWatcher = new UIStateWatcher(app);
  }

  @After public void tearDown() {
    app.goToBackground();
  }

  @Test public void shouldBeInBackgroundByDefault() {
    assertTrue(app.isApplicationInBackground());
  }

  @Test public void shouldBeInForegroundIfAnActivityIsShown() {
    showActivity();

    assertFalse(app.isApplicationInBackground());
  }

  @Test public void shouldBeInBackgroundIfARootActivityIsShownAndDismissed() {
    showActivity();

    dismissActivity(true);

    assertTrue(app.isApplicationInBackground());
  }

  @Test public void shouldBeInForegroundIfANonRootActivityIsShownAndDismissed() {
    showActivity();

    dismissActivity(false);

    assertFalse(app.isApplicationInBackground());
  }

  private void showActivity() {
    uiStateWatcher.onActivityStarted(activity);
    uiStateWatcher.onActivityCreated(activity, new Bundle());
    uiStateWatcher.onActivityResumed(activity);
  }

  private void dismissActivity(boolean isRootActivity) {
    when(activity.isTaskRoot()).thenReturn(isRootActivity);
    uiStateWatcher.onActivityPaused(activity);
    uiStateWatcher.onActivityStopped(activity);
    uiStateWatcher.onActivityDestroyed(activity);
  }
}
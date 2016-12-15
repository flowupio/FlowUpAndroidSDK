/*
 * Copyright (C) 2016 Go Karumi S.L.
 */

package io.flowup.example;

import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

@RunWith(AndroidJUnit4.class) public class MainActivityTest {

  @Rule public IntentsTestRule<MainActivity> activityRule =
      new IntentsTestRule<>(MainActivity.class, true, false);

  @Test public void tapsOnTheFirstButton() {
    startActivity();

    onView(withId(R.id.download_data_button)).perform(click());
  }

  @Test public void opensSecondActivity() {
    startActivity();

    onView(withId(R.id.open_activity_button)).perform(click());

    intended(hasComponent(SecondActivity.class.getCanonicalName()));
  }

  private void startActivity() {
    activityRule.launchActivity(null);
  }
}

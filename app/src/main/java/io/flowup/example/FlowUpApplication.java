/*
 * Copyright (C) 2016 Go Karumi S.L.
 */

package io.flowup.example;

import android.app.Application;
import android.os.StrictMode;
import io.flowup.FlowUp;

public class FlowUpApplication extends Application {

  @Override public void onCreate() {
    super.onCreate();
    enableStrictMode();
    FlowUp.Builder.with(this)
        .apiKey("15207698c544f617e2c11151ada4972e1e7d6e8e")
        .forceReports(BuildConfig.DEBUG)
        .logEnabled(BuildConfig.DEBUG)
        .start();
  }

  private void enableStrictMode() {
    StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
        .detectAll()
        .penaltyLog()
        .penaltyDeath()
        .build());
    StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
        .detectAll()
        .penaltyLog()
        .penaltyDeath()
        .build());
  }
}

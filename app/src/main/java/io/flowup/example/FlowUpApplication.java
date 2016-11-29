/*
 * Copyright (C) 2016 Go Karumi S.L.
 */

package io.flowup.example;

import android.app.Application;
import android.os.StrictMode;
import com.squareup.leakcanary.LeakCanary;
import io.flowup.FlowUp;

public class FlowUpApplication extends Application {

  @Override public void onCreate() {
    super.onCreate();
    initializeLeakCanary();
    enableStrictMode();
    FlowUp.Builder.with(this)
        .apiKey("0dd955ff0cdc4148ace2aa8a30f43f96")
        .forceReports(BuildConfig.DEBUG)
        .logEnabled(BuildConfig.DEBUG)
        .start();
  }

  private void initializeLeakCanary() {
    if (LeakCanary.isInAnalyzerProcess(this)) {
      return;
    }
    LeakCanary.install(this);
  }

  private void enableStrictMode() {
    StrictMode.setThreadPolicy(
        new StrictMode.ThreadPolicy.Builder().detectAll().penaltyLog().penaltyDeath().build());
    StrictMode.setVmPolicy(
        new StrictMode.VmPolicy.Builder().detectAll().penaltyLog().penaltyDeath().build());
  }
}

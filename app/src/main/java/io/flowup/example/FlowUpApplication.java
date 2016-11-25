/*
 * Copyright (C) 2016 Go Karumi S.L.
 */

package io.flowup.example;

import android.app.Application;
import android.os.Build;
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
    StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
        .detectAll()
        .penaltyLog()
        .penaltyDeath()
        .build());
    StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder()
        .detectLeakedClosableObjects()
        .detectActivityLeaks();
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      builder.detectCleartextNetwork();
    }
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
      builder.detectFileUriExposure();
    }
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
      builder.detectLeakedRegistrationObjects();
    }
    StrictMode.setVmPolicy(builder
        .penaltyLog()
        .penaltyDeath()
        .build());
  }
}

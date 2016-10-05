/*
 * Copyright (C) 2015 Go Karumi S.L.
 */

package com.flowup.example;

import android.app.Application;
import com.flowup.FlowUp;

public class FlowUpApplication extends Application {

  @Override public void onCreate() {
    FlowUp.with(this).start();
    super.onCreate();
  }
}

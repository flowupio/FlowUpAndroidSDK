/*
 * Copyright (C) 2016 Go Karumi S.L.
 */

package com.karumi.kiru.metricnames;

import android.content.Context;
import android.support.graphics.drawable.BuildConfig;

class App {

  private final Context context;

  App(Context context) {
    this.context = context;
  }

  String getApplicationName() {
    return context.getPackageName();
  }

  String getApplicationVersion() {
    return String.valueOf(BuildConfig.VERSION_CODE);
  }

}

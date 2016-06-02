/*
 * Copyright (C) 2016 Go Karumi S.L.
 */

package com.karumi.kiru.metricnames;

import android.content.Context;

class App {

  private final Context context;

  App(Context context) {
    this.context = context;
  }

  String getApplicationName() {
    return "";
  }

  String getApplicationVersion() {
    return "";
  }

}

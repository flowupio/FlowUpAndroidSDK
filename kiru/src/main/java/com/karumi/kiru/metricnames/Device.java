/*
 * Copyright (C) 2016 Go Karumi S.L.
 */

package com.karumi.kiru.metricnames;

import android.content.Context;

class Device {

  private final Context context;

  Device(Context context) {
    this.context = context;
  }

  String getOSVersion() {
    return "";
  }

  String getModel() {
    return "";
  }

  String getScreenDensity() {
    return "";
  }

  String getScreenSize() {
    return "";
  }

}

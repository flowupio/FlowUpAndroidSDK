/*
 * Copyright (C) 2016 Go Karumi S.L.
 */

package com.flowup.metricnames;

import android.content.Context;
import android.os.Build;
import android.util.DisplayMetrics;

class Device {

  private final Context context;

  Device(Context context) {
    this.context = context;
  }

  String getOSVersion() {
    return "API-" + String.valueOf(Build.VERSION.SDK_INT);
  }

  String getModel() {
    return Build.MODEL.replace('.', '-');
  }

  String getScreenDensity() {
    float density = context.getResources().getDisplayMetrics().density;
    if (density >= 4.0) {
      return "xxxhdpi";
    }
    if (density >= 3.0) {
      return "xxhdpi";
    }
    if (density >= 2.0) {
      return "xhdpi";
    }
    if (density >= 1.5) {
      return "hdpi";
    }
    if (density >= 1.0) {
      return "mdpi";
    }
    return "ldpi";
  }

  String getScreenSize() {
    DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
    int width = displayMetrics.widthPixels;
    int height = displayMetrics.heightPixels;
    return width + "X" + height;
  }
}

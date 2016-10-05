/*
 * Copyright (C) 2016 Go Karumi S.L.
 */

package com.flowup.metricnames;

import android.content.Context;
import android.os.Build;
import android.util.DisplayMetrics;

class Device {

  private final Context context;
  private final UUIDGenerator uuidGenerator;

  Device(Context context) {
    this.context = context;
    this.uuidGenerator = new UUIDGenerator(context);
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
    int widthPixels = displayMetrics.widthPixels;
    int heightPixels = displayMetrics.heightPixels;
    int portraitWidth = Math.min(widthPixels, heightPixels);
    int portraitHeight = Math.max(widthPixels, heightPixels);
    return portraitWidth + "X" + portraitHeight;
  }

  String getUUID() {
    return uuidGenerator.getUUID();
  }
}

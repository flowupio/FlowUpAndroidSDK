/*
 * Copyright (C) 2016 Go Karumi S.L.
 */

package com.flowup.metricnames;

import android.content.Context;
import android.os.PowerManager;
import android.util.DisplayMetrics;

import static android.os.Build.MODEL;
import static android.os.Build.VERSION.SDK_INT;
import static android.os.Build.VERSION_CODES.LOLLIPOP;

class Device {

  private final Context context;
  private final UUIDGenerator uuidGenerator;

  Device(Context context) {
    this.context = context;
    this.uuidGenerator = new UUIDGenerator(context);
  }

  String getOSVersion() {
    return "API" + String.valueOf(SDK_INT);
  }

  String getModel() {
    return MODEL.replace(".", "");
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

  String getInstallationUUID() {
    return uuidGenerator.getUUID();
  }

  int getNumberOfCores() {
    return Runtime.getRuntime().availableProcessors();
  }

  boolean isPowerSaverEnabled() {
    PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
    if (SDK_INT >= LOLLIPOP) {
      return powerManager.isPowerSaveMode();
    }
    return false;
  }
}

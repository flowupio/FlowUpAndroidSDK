/*
 * Copyright (C) 2016 Go Karumi S.L.
 */

package com.flowup.android;

import android.app.ActivityManager;
import android.content.Context;
import android.os.PowerManager;
import android.util.DisplayMetrics;

import static android.os.Build.MODEL;
import static android.os.Build.VERSION.SDK_INT;
import static android.os.Build.VERSION_CODES.LOLLIPOP;
import static com.flowup.utils.MetricNameUtils.replaceDots;

public class Device {

  private final Context context;
  private final UUIDGenerator uuidGenerator;

  public Device(Context context) {
    this.context = context;
    this.uuidGenerator = new UUIDGenerator(context);
  }

  public String getOSVersion() {
    return "API" + String.valueOf(SDK_INT);
  }

  public String getModel() {
    return replaceDots(MODEL);
  }

  public String getScreenDensity() {
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

  public String getScreenSize() {
    DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
    int widthPixels = displayMetrics.widthPixels;
    int heightPixels = displayMetrics.heightPixels;
    int portraitWidth = Math.min(widthPixels, heightPixels);
    int portraitHeight = Math.max(widthPixels, heightPixels);
    return portraitWidth + "X" + portraitHeight;
  }

  public String getInstallationUUID() {
    return replaceDots(uuidGenerator.getUUID());
  }

  public int getNumberOfCores() {
    return Runtime.getRuntime().availableProcessors();
  }

  public boolean isBatterySaverOn() {
    PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
    if (SDK_INT >= LOLLIPOP) {
      return powerManager.isPowerSaveMode();
    }
    return false;
  }

  public long getMemoryUsage() {
    ActivityManager activityManager =
        (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
    ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
    activityManager.getMemoryInfo(memoryInfo);
    double availMem = memoryInfo.availMem;
    double totalMem = memoryInfo.totalMem;
    int usage = (int) ((availMem / totalMem) * 100);
    return usage;
  }
}

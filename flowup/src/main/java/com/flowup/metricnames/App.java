/*
 * Copyright (C) 2016 Go Karumi S.L.
 */

package com.flowup.metricnames;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import com.flowup.R;

import static com.flowup.utils.MetricNameUtils.replaceDots;

class App {

  private final Context context;

  App(Context context) {
    this.context = context;
  }

  String getApplicationName() {
    return replaceDots(context.getPackageName());
  }

  String getApplicationVersion() {

    try {
      String packageName = getApplicationName();
      PackageInfo packageInfo = context.getPackageManager().getPackageInfo(packageName, 0);
      return replaceDots(packageInfo.versionName);
    } catch (PackageManager.NameNotFoundException e) {
      return context.getString(R.string.unknown_version_name_cross_metric_name);
    }
  }
}

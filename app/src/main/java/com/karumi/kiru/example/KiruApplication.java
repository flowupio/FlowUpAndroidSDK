/*
 * Copyright (C) 2015 Go Karumi S.L.
 */

package com.karumi.kiru.example;

import android.app.Application;
import com.karumi.kiru.Kiru;

public class KiruApplication extends Application {

  @Override public void onCreate() {
    super.onCreate();
    Kiru.with(this).start();
  }
}

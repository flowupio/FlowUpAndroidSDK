/*
 * Copyright (C) 2016 Go Karumi S.L.
 */

package com.flowup.doubles;

import android.content.Context;
import com.flowup.metricnames.App;

import static org.mockito.Mockito.mock;

public class FakeApp extends App {

  public FakeApp() {
    this(mock(Context.class));
  }

  public FakeApp(Context context) {
    super(context);
  }

  @Override public String getAppPackageName() {
    return "io.flowup.androidsdk";
  }

  @Override public String getAppVersionName() {
    return "1.0.0";
  }
}

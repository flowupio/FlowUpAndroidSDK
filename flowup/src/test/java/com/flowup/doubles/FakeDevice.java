/*
 * Copyright (C) 2016 Go Karumi S.L.
 */

package com.flowup.doubles;

import android.content.Context;
import com.flowup.metricnames.Device;

import static org.mockito.Mockito.mock;

public class FakeDevice extends Device {

  public FakeDevice() {
    this(mock(Context.class));
  }

  public FakeDevice(Context context) {
    super(context);
  }

  @Override public String getOSVersion() {
    return "API23";
  }

  @Override public String getModel() {
    return "Nexus-5X";
  }

  @Override public String getScreenDensity() {
    return "xxhdpi";
  }

  @Override public String getInstallationUUID() {
    return "123456789";
  }

  @Override public int getNumberOfCores() {
    return 4;
  }

  @Override public boolean isBatterySaverOn() {
    return true;
  }
}

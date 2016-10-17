/*
 * Copyright (C) 2016 Go Karumi S.L.
 */

package io.flowup.doubles;

import android.content.Context;
import io.flowup.android.Device;

import static org.mockito.Mockito.mock;

public class AnyDevice extends Device {

  public AnyDevice() {
    this(mock(Context.class));
  }

  public AnyDevice(Context context) {
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

  @Override public String getScreenSize() {
    return "800X600";
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

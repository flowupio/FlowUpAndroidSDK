/*
 * Copyright (C) 2016 Go Karumi S.L.
 */

package io.flowup.config;

import com.google.gson.annotations.SerializedName;

public class Config {

  @SerializedName("enabled") private final boolean enabled;

  public Config() {
    this.enabled = false;
  }

  public boolean isEnabled() {
    return enabled;
  }
}

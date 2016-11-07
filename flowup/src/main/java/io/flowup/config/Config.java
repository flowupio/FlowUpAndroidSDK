/*
 * Copyright (C) 2016 Go Karumi S.L.
 */

package io.flowup.config;

import com.google.gson.annotations.SerializedName;

public class Config {

  @SerializedName("enabled") private final boolean enabled;

  public Config() {
    this.enabled = true;
  }

  public Config(boolean enabled) {
    this.enabled = enabled;
  }

  public boolean isEnabled() {
    return enabled;
  }

  @Override public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Config config = (Config) o;
    return enabled == config.enabled;
  }

  @Override public int hashCode() {
    return (enabled ? 1 : 0);
  }
}

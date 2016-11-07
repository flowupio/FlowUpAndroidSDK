/*
 * Copyright (C) 2016 Go Karumi S.L.
 */

package io.flowup.config;

class ConfigStorage {

  Config getConfig() {
    //Obtain from realm or the shared prefs.
    return new Config();
  }

  void updateConfig(Config config) {

  }
}

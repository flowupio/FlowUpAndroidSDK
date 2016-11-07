/*
 * Copyright (C) 2016 Go Karumi S.L.
 */

package io.flowup.config;

public class FlowUpConfig {

  private final ConfigStorage storage;
  private final ConfigApiClient apiClient;

  public FlowUpConfig(ConfigStorage storage, ConfigApiClient apiClient) {
    this.storage = storage;
    this.apiClient = apiClient;
  }

  public Config getConfig() {
    return storage.getConfig();
  }

  public Config updateConfig() {
    Config config = apiClient.getConfig().getValue();
    storage.updateConfig(config);
    return config;
  }
}
